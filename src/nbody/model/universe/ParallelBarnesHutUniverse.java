package nbody.model.universe;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import nbody.model.BHTree;
import nbody.model.Body;
import nbody.model.IterativeBHTree;
import nbody.model.Quadrant;
//TODO: revisar hay dos que se quedan quietos!!
public class ParallelBarnesHutUniverse extends BarnesHutUniverse {
	/**
	 * arreglo de threads
	 */
	UpdateBarnesHutRunnable[] t;
	private Semaphore semaforo_update;
	private int threads;
	CyclicBarrier barrier;
	CountDownLatch finishedUpdating;
	
	public ParallelBarnesHutUniverse(int threads) {
		super();
		this.threads = threads;
        Quadrant quad = new Quadrant(0, 0, this.R * 2);
        this.setBodiesTree(new IterativeBHTree(quad));
	}
	
	class UpdateBarnesHutRunnable extends Thread {
		
		private volatile boolean running = true;
		private int start;
		private int stop;
		
		UpdateBarnesHutRunnable(int i, int cores) { 
        	int n = N / cores;
	        this.start = i * n;
	        if (i != cores -1) {
	        	stop = start + n;
	        } else {
	        	stop = N;
	        }
        	
        }
	    
	    public void terminate() {
	    	running = false;
	    }
		
		public void run() {
			
			while (running) {
				
				try {
					semaforo_update.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (running) {
					
					double dt = 0.5;

			        	
			        for (int i = this.start+1; i < this.stop; i++) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public void stop() {
		for (int c = 0; c < this.t.length; c++) {
			t[c].terminate();
			semaforo_update.release();
		}
	}

}
