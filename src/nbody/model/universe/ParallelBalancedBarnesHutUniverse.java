package nbody.model.universe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import nbody.model.BHTree;
import nbody.model.Body;
import nbody.model.BodyXCoordinateComparator;
import nbody.model.BodyYCoordinateComparator;
import nbody.model.IterativeBHTree;
import nbody.model.Quadrant;

public class ParallelBalancedBarnesHutUniverse extends BarnesHutUniverse {
	
	public class quadrant {
		public quadrant(double xmin, double xmax, double ymin, double ymax) {
			this.xmin = xmin;
			this.xmax = xmax;
			this.ymin = ymin;
			this.ymax = ymax;
		}
		public quadrant() {
			super();
		}
		public double xmin;
		public double ymin;
		public double xmax;
		public double ymax;
	}
	
	public class coordinate {
		public double x;
		public double y;
	}
	
	// Semaforos, barreras y datos para la division de cuerpos
	private BodyDividerThread[] body_divider_threads;

	//semaforo para que los procesos de division de cuerpos esperen hasta ser activados
	// para dividir una nueva region
	private Semaphore[] start_division;
	
	private Semaphore[] mutex_bodies_for_threads;
	private List<List<Body>> bodies_for_threads;
	
	// Barrera para sincronizar los procesos divisores de cada canal.
	private CyclicBarrier[] division_finished;

	// exclusion mutua para los buffers de cuerpos.
	private Semaphore[]  mutex_body_buffers;
	private List<List<Body>> body_buffers;
	
	// es necesatio???
	private Semaphore[] mutex_chanels;
	private int[] chanels;
	
	private quadrant[] chanel_quadrant; // quadrant for each channel;
	
	// para esperar que los procesos terminen en cada división
	private Semaphore[] finished_interchange;

	// semaforos, barreras y datos para la actualizacion de fuerzas
	
	private ForceUpdaterThread[] force_updater_threads; 
	
	private Semaphore[] start_force_updating;
	private CountDownLatch force_updating_finished;
	
	private int processor_count;

	public double current_dt;

	private int total_work;

	private coordinate[] chanel_split_coord;
	
	class BodyDividerThread extends Thread {
		
		private int number;
		private int partner;
		private int chanel;
		protected volatile List<Body> bodies;
		private List<Body> bodies_to_remove;
		private volatile boolean running = true;
		
		public BodyDividerThread(int number, int procesors) {
			this.setName("Body Divider Thread "+number);
			this.number = number;
			this.bodies = new ArrayList<Body>();
			this.bodies_to_remove = new ArrayList<Body>();
			int n = N / procesors;
			int end_index = 0;
	        int start_index = number * n;
	        if (number != procesors -1) {
	        	end_index = start_index + n;
	        } else {
	        	end_index = N;
	        }
	        
	        // en un principio simplemente dividimos por cantidad
	        // no es necesario usar exclusion mutua para los cuerpos ya que las regiones no
	        // tienen overlaping
			for (int i = start_index; i < end_index; i++) {
				this.bodies.add(bodies_array[i]);
			}		
		}
		
	    public void terminate() {
	    	running = false;
	    }
		
		@Override
		public void run() {
			while (running) {
				try {
					// espero a que se indique que las threads deben empezar.
					start_division[this.number].acquire();

					// adquiero mi canal
					mutex_chanels[this.number].acquire();
					this.chanel = chanels[this.number]; 
					mutex_chanels[this.number].release();
					
					
					if (running) {
				
				        this.partner = this.number ^ (binlog(processor_count) - this.chanel); // ^ = bitwise xor
						int above = (this.number & this.chanel); // indica si este thread esta "arriba" o "debajo" de la region.
						for (int i = 0; i < this.bodies.size(); i++) {
							// si el cuerpo no esta en mi secci�n, se lo envio al buffer de mi compa�ero.
							// no es necesario exclusion mutua para spli_coordinates ya que se setean ANTES de que el semaforo de star_division sea liberado
							// y los procesos divisores SOLO lo leen.
							if (! this.bodies.get(i).inDirection(above,chanel_split_coord[this.chanel] ,chanel_quadrant[this.chanel])) {
								// no es necesario exclusion mutua ya que la comunicaci�n es UNO A UNO con mi compa�ero de region
								// y los buffers no se leen por sus due�os hasta que TODOS hayan terminado de actualizar.
								Body b = this.bodies.get(i);
								body_buffers.get(this.partner).add(b);
								this.bodies_to_remove.add(b);
								
							}
						}
						//Esperamos a que hayan termianen TODOS los procesos divisores de mi canal, total no se puede terminar hasta que no termine el intercambio
						division_finished[this.chanel].await();
						this.bodies.addAll(body_buffers.get(this.number)); // agrego a mis cuerpos todos los que me paso mi compa�ero de divisi�n.
						body_buffers.set(this.number, new ArrayList<Body>());
						this.bodies.removeAll(bodies_to_remove);
						finished_interchange[this.chanel].release();
					}
				} catch (InterruptedException | BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}
		}
	}
		
	class ForceUpdaterThread extends Thread {
		private List<Body> bodies;
		private volatile boolean running = true;
		private int i;
		
		public ForceUpdaterThread(int i) {
			this.i = i;
			this.setName("Force Updater Thread "+i);
		}
		
		public void terminate() {
			running = false;
		}
		
		public void run() {
			while (running) {
				try {
					start_force_updating[i].acquire();
					this.bodies = bodies_for_threads.get(i);
					if (running) {
						// TODO: update with field;
						double dt = current_dt;
						
				        for (Body b : this.bodies) {
				           	b.resetForce();	                
				           	bodies_tree.updateForce(b);
				           	b.update(dt);
				        }
			        force_updating_finished.countDown();
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static int binlog( int bits ) // returns 0 for bits=0
	{
	    int log = 0;
	    if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
	    if( bits >= 256 ) { bits >>>= 8; log += 8; }
	    if( bits >= 16  ) { bits >>>= 4; log += 4; }
	    if( bits >= 4   ) { bits >>>= 2; log += 2; }
	    return log + ( bits >>> 1 );
	}
	
	public ParallelBalancedBarnesHutUniverse() {
		super();
        Quadrant quad = new Quadrant(0, 0, this.R * 2);
        this.setBodiesTree(new IterativeBHTree(quad));
	}
	
	private coordinate chooseCoordinateToSplit(boolean parallel_to_x, int start, int end) {
		coordinate coord = new coordinate();
		Comparator<Body> c;
		// siempre cortar por el lado mas largo (Salmon)
		if (parallel_to_x) {
			 c = new BodyXCoordinateComparator();
		} else {
			c = new BodyYCoordinateComparator();
		}
		java.util.Arrays.parallelSort(bodies_array,c);
		boolean found_median = false;
		int i = 0;
		int work = 0; 
		while(!found_median) {
			work += bodies_array[i].work();
			if (work > total_work/2) {
				found_median = true;
				coord.x = bodies_array[i].rx();
				coord.y = bodies_array[i].ry();
			} 
		}
		 return coord;
	}
	
	private quadrant splitQuadrant(quadrant q, boolean above,coordinate coord) {
		quadrant new_q = new quadrant();
		double lenghtx = - q.xmin + q.xmax; // -xmin + xmax
		double lenghty = - q.ymin + q.ymax; // -ymin + ymax
		// siempre cortar por el lado mas largo (Salmon)
		if (lenghtx > lenghty) {
			new_q.xmin = q.xmin;
			new_q.xmax = q.xmax;
			if (above) {
				// EL CUADRANTE DE "ARRIBA"
				new_q.ymin = coord.y; // nueva Y minima es la Y de la coordinada
				new_q.ymax = q.ymax; // Y maxima es la Y maxima actual 
			} else {
				new_q.ymin = q.ymin; // MINIMA ES LA MINIMA ACTUAL;
				new_q.ymax = coord.y; //MAXIMA ES LA COORDINADA ES EL CUADRANTE DE ABAJO
			}
		} else {
			// y es igual
			new_q.ymin = q.ymin;
			new_q.ymax = q.ymax;
			if (above) {
				// EL CUADRANTE DE "ARRIBA"
				new_q.xmin = coord.x; // nueva X minima es la X de la coordinada
				new_q.xmax = q.xmax; // Y maxima es la Y maxima actual 
			} else {
				new_q.xmin = q.xmin; // MINIMA ES LA MINIMA ACTUAL;
				new_q.xmax = coord.x; //MAXIMA ES LA COORDINADA ES EL CUADRANTE DE ABAJO
			}
		}
		return new_q;
	}
	
	public void ORB(int chanel, int start, int end, quadrant q, int rank) {
		// TODO: revisar cuenta CASO INPAAAARRR
		// mientras tenga más de un thread en mi grupo.
		if (chanel < binlog(processor_count) ) {
			
			int mid_thread = (int) Math.ceil(((double)start + (double)end)/2);
			int thread_count = end - start;
			
			double lenghtx = - q.xmin + q.xmax; 
			double lenghty = - q.ymin + q.ymax;
			
			coordinate coord =  chooseCoordinateToSplit(lenghtx > lenghty, start,end); 
			quadrant above_quadrant = splitQuadrant(q,true, coord);
			quadrant below_quadrant = splitQuadrant(q,false, coord);
			
			division_finished[chanel] = new CyclicBarrier(thread_count+1);
			finished_interchange[chanel] = new Semaphore(-thread_count);
			chanel_quadrant[chanel] = q;
			chanel_split_coord[chanel] = coord;
		
			try {
	
				for (int i= start; i < end+1; i++) {
					mutex_chanels[i].acquire();
					chanels[i] = chanel; 
					mutex_chanels[i].release();
					start_division[i].release();
				}
				
				finished_interchange[chanel].acquire();
				
				boolean above = chanel % 2 == 1;
				// TODO: se puede hacer algo menos goma
				q = above ? above_quadrant : below_quadrant;
				quadrant other = above ? below_quadrant : above_quadrant;
				
				if (rank > mid_thread) {
					ORB(chanel+1,start,mid_thread-1, other, rank+1);
				} else {
					ORB(chanel+1,mid_thread, end, other, rank+1);
				}
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void initializeDataStructures() {
		processor_count = 4;
		this.body_divider_threads = new BodyDividerThread[processor_count];
		this.force_updater_threads = new ForceUpdaterThread[processor_count];
		this.start_division = new Semaphore[processor_count];
		this.division_finished = new CyclicBarrier[processor_count];
		this.mutex_body_buffers = new Semaphore[processor_count];
		this.finished_interchange = new Semaphore[processor_count];
		this.mutex_chanels = new Semaphore[processor_count];
		this.chanels = new int[processor_count];
		this.start_force_updating = new Semaphore[processor_count];
		this.chanel_quadrant = new quadrant[processor_count];
		this.chanel_split_coord = new coordinate[processor_count];
		this.body_buffers = new ArrayList<List<Body>>();
		this.bodies_for_threads = new ArrayList<List<Body>>();
		this.mutex_bodies_for_threads = new Semaphore[processor_count];
		
		for (int i = 0; i < processor_count ; i++) {
			
			this.start_division[i] = new Semaphore(0);
			this.start_force_updating[i] = new Semaphore(0);
			
			this.body_divider_threads[i] = new BodyDividerThread(i, processor_count);
			this.body_divider_threads[i].start();
			
			this.mutex_body_buffers[i] = new Semaphore(1);
			this.mutex_chanels[i] = new Semaphore(1);
			this.body_buffers.add(i,new LinkedList<Body>());
			this.bodies_for_threads.add(i,new LinkedList<Body>());
			
			this.force_updater_threads[i] = new ForceUpdaterThread(i);
			this.force_updater_threads[i].start();
			
			this.mutex_bodies_for_threads[i] = new Semaphore(1);
		}
	}
	

	private void resetBodyListInDividerThreads() {
		this.total_work = 0;
		for (int i = 0; i < processor_count ; i++) {
			List<Body> bodies = new ArrayList<Body>();
			int n = N / processor_count;
			int end_index = 0;
	        int start_index = i * n;
	        if (i != processor_count -1) {
	        	end_index = start_index + n;
	        } else {
	        	end_index = N;
	        }
	        
	        // en un principio simplemente dividimos por cantidad
	        // no es necesario usar exclusion mutua para los cuerpos ya que las regiones no
	        // tienen overlaping
			for (int j = start_index; j < end_index; j++) {
				this.total_work += bodies_array[j].work();
				bodies.add(bodies_array[j]);
			}
			
			body_divider_threads[i].bodies = bodies;
			body_divider_threads[i].bodies_to_remove = new ArrayList<Body>();
		}
	}
	
	@Override
	public void update(double dt) {
		
		Quadrant quad = new Quadrant(0, 0, this.R * 2);
        this.setBodiesTree(new BHTree(quad));
        this.resetBodyListInDividerThreads();
        this.current_dt = dt;
        
        for (Body b : this.bodies_array) {
        	if (quad.contains(b.rx(), b.ry())) {
        		this.bodies_tree().insert(b);
        	}
        }
        
		
		int start = 0;
		int end = processor_count -1;
		//int chanel = binlog(processor_count);
		int chanel = 0; 
		quadrant quadrant = new quadrant(-this.R, this.R, -this.R, this.R);
		try {
			
			ORB(chanel,start,end,quadrant, 0);
			
			//que todas empiezen en cuanto puedan
			this.force_updating_finished = new CountDownLatch(processor_count);
			
			for (int i=0; i < processor_count; i++) {
				// TODO: revisar sincronización: creo que todoas las treads estan esperando panchas por sus canales.
				bodies_for_threads.set(i, body_divider_threads[i].bodies );
				this.start_force_updating[i].release();
			}
			

			//esperamos a que se haya terminado de actualizar antes de terminar.
			this.force_updating_finished.await();
			// las liberamos para que mueran
			
						
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		for (int c = 0; c < processor_count; c++) {
			body_divider_threads[c].terminate();
			force_updater_threads[c].terminate();
			start_force_updating[c].release();
			start_division[c].release(); // solo uno porque al final hay un proceso divisor por "procesador", le liberamos el sem para que pueda dejar e esperar y morir.
		}

	}


}
