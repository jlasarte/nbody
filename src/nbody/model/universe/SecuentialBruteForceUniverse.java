package nbody.model.universe;


/**
 * Universo que se actualiza utilizando el algoritmo PP, de orden O(n^2)
 * @author jlasarte
 *
 */
public class SecuentialBruteForceUniverse extends UniverseTemplate {

	@Override
	public void update(double dt) {
        // calcula las fuerzas
        for (int i = 0; i < this.N; i++) {

        	bodies_array[i].resetForce();

            
            for (int j = 0; j < N; j++) {
                if (i != j) {
                	bodies_array[i].addForce(bodies_array[j]);
                }
            } 
        }        
        // calcular aceleracion y posicion
        for (int i = 0; i < this.N; i++) {
        	bodies_array[i].update(dt);
        }
      }

	@Override
	public void stop() {
		// nada que hacer
	}
	
	protected void initializeDataStructures() {
		//nada que hhacer
	}
}
