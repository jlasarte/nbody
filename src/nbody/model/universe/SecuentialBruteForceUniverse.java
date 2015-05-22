package nbody.model.universe;



public class SecuentialBruteForceUniverse extends UniverseTemplate {

	@Override
	public void update(double dt) {
        // calculate the net force exerted by body j on body i
        for (int i = 0; i < this.N; i++) {
        	// reset forces to zero 

        	bodies_array[i].resetForce();

            
            for (int j = 0; j < N; j++) {
                if (i != j) {
                	bodies_array[i].addForce(bodies_array[j]);
                }
            } 
        }        
        // calculate the new acceleration, velocity, and position for each body
        for (int i = 0; i < this.N; i++) {
        	bodies_array[i].update(dt);
        }
      }

	@Override
	public void stop() {
		// nothing to do
	}
	
	protected void initializeDataStructures() {
		//nothing to do
	}
}
