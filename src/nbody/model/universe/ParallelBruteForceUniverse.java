package nbody.model.universe;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * Simulacion utilizando un alogritmo de fuerza bruta O(n^2) paraelizado.
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
	private double current_dt;
	
	/**
	 * Thread encargada de la actualizacion de cuerpos
	 * @author jlasarte
	 *
	 */
	class UpdateRunnable extends Thread {
		
		private volatile boolean running = true;
		private int start;
		private int stop;
		
		/**
		 * Constructor
		 * @param i indice de esta thread
		 * @param cores cantidad de threads total
		 */
	    UpdateRunnable(int i, int cores) { 
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
			                for (int j = 0; j < N; j++) {
			                    if (i != j) {
			                    	bodies_array[i].addForce(bodies_array[j]);
			                    }
			                } 
			        	}
			        
			        try {
						barrier.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						System.err.println("Proceso interrumpido o en estado incorrecto.");
						System.exit(1);
					}
			        
			        for (int i =start; i < stop; i++) {
			        	bodies_array[i].update(dt);
			        }
			        
			       finishedUpdating.countDown();
				}
			}
		}
	}
	
	/**
	 * Constructor
	 * @param threads cantidad de threads a utilizar
	 */
	public ParallelBruteForceUniverse(int threads) {
		super();
		this.threads = threads;
	}
	
	/**
	 * Inicializa estructuras de datos
	 */
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
        this.current_dt = dt;

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
