package nbody.model.universe;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * Simulaci�n utilizando un alogritmo de fuerza bruta O(n²) paraelizado.
 * @author julia
 *
 */
public class ParallelBruteForceUniverse extends UniverseTemplate {

	/**
	 * arreglo de threads
	 */
	UpdateRunnable[] t;
	private Semaphore semaforo_update;
	CyclicBarrier barrier;
	CountDownLatch finishedUpdating;
	private int threads;

	class UpdateRunnable extends Thread {
		
		private volatile boolean running = true;
		private int start;
		private int stop;
		
	    UpdateRunnable(int i, int cores) { 
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
			                for (int j = 0; j < N; j++) {
			                    if (i != j) {
			                    	bodies_array[i].addForce(bodies_array[j]);
			                    }
			                } 
			        	}
			        
			        try {
						barrier.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			        for (int i =start; i < stop; i++) {
			        	bodies_array[i].update(dt);
			        }
			        
			       finishedUpdating.countDown();
				}
			}
		}
	}
	
	public ParallelBruteForceUniverse(int threads) {
		super();
		this.threads = threads;
	}
	
	protected void initializeDataStructures( ){
		this.t = new UpdateRunnable[this.threads];
		
		// como la barrera es ciclica, solo tenemos que iniciarla esta vez.
		this.barrier = new CyclicBarrier(this.t.length);
		this.semaforo_update = new Semaphore(0, true);

		for (int c = 0; c < this.t.length; c++) {
			t[c] = new UpdateRunnable(c,this.t.length);
			t[c].start();
		}
	}
	
	public void update(double dt) {
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
