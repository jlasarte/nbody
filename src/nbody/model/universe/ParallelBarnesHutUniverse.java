package nbody.model.universe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import nbody.model.BHTree;
import nbody.model.Body;
import nbody.model.IterativeBHTree;
import nbody.model.Quadrant;

/**
 * Universo que se ejecuta utilizando un algoritmo de barnes-hut paralelo
 * @author jlasarte
 *
 */
public class ParallelBarnesHutUniverse extends BarnesHutUniverse {
	/**
	 * arreglo de threads
	 */
	UpdateBarnesHutRunnable[] t;
	private Semaphore semaforo_update;
	private int threads;
	CyclicBarrier barrier;
	CountDownLatch finishedUpdating;
	private double current_dt;

	/**
	 * Constructor
	 * @param threads cantidad de threads a utilizar
	 */
	public ParallelBarnesHutUniverse(int threads) {
		super();
		this.threads = threads;
        Quadrant quad = new Quadrant(0, 0, this.R * 2);
        this.setBodiesTree(new IterativeBHTree(quad));
	}
	
	/**
	 * thread encargada de la actualizacion de cuerpos
	 * @author jlasarte
	 *
	 */
	class UpdateBarnesHutRunnable extends Thread {
		
		private volatile boolean running = true;
		private int start;
		private int stop;
		
		/**
		 * Constructor
		 * @param i indice de esta thread
		 * @param cores cantidad de threads total
		 */
		UpdateBarnesHutRunnable(int i, int cores) { 
        	int n = N / cores;
	        this.start = i * n;
	        if (i != cores -1) {
	        	stop = start + n;
	        } else {
	        	stop = N;
	        }
        	
        }
	    
		/**
		 * Detiene la thread
		 */
	    public void terminate() {
	    	running = false;
	    }
		
		public void run() {
			
			while (running) {
				
				try {
					semaforo_update.acquire();
				} catch (InterruptedException e1) {
					System.err.println("Proceso interrumpido");
					System.exit(1);
				}
				
				if (running) {
					
					double dt = current_dt;

			        	
			        for (int i = this.start; i < this.stop; i++) {
			          	// reset forces to zero 
	
			           	bodies_array[i].resetForce();	                
			           	bodies_tree.updateForce(bodies_array[i]);
			           	bodies_array[i].update(dt);
			        }
			         
			       finishedUpdating.countDown();
				}
			}
		}
	}
	
	/**
	 * Inicializa estructuras de datos
	 */
	protected void initializeDataStructures() {
		this.t = new UpdateBarnesHutRunnable[threads];
		
		this.semaforo_update = new Semaphore(0, true);

		for (int c = 0; c < this.t.length; c++) {
			t[c] = new UpdateBarnesHutRunnable(c,this.t.length);
			t[c].start();
		}
	}
	
	@Override
	public void update(double dt) {
        Quadrant quad = new Quadrant(0, 0, this.R * 2);
        this.setBodiesTree(new BHTree(quad));
        this.current_dt = dt;
        for (Body b : this.bodies_array) {
        	if (quad.contains(b.rx(), b.ry())) {
        		this.bodies_tree().insert(b);
        	}
        }
        
		this.finishedUpdating = new CountDownLatch(this.t.length);
		
		for (int c = 0; c < this.t.length; c++) {
			semaforo_update.release();
		}
		
		// tenemos que esperar a que todas las threads hayan terminado de actualizar el modelo para proseguir
	      try {
			this.finishedUpdating.await();
		} catch (InterruptedException e) {
			System.err.println("Proceso interrumpido");
			System.exit(1);
		} 

	}

	public void stop() {
		for (int c = 0; c < this.t.length; c++) {
			t[c].terminate();
			semaforo_update.release();
		}
	}

}
