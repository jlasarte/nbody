package nbody.model.universe;

import nbody.model.BHTree;
import nbody.model.Body;
import nbody.model.IterativeBHTree;
import nbody.model.Quadrant;

/***
 * Universo que se actualiza utilizando el Algoritmo de Barnes Hut
 * @author jlasarte
 *
 */
public class BarnesHutUniverse extends UniverseTemplate {

	protected BHTree bodies_tree;
	
	public BarnesHutUniverse() {
		super();
        Quadrant quad = new Quadrant(0, 0, this.R * 2);
        this.setBodiesTree(new BHTree(quad));
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
        
		for (Body b : this.bodies_array) {
            b.resetForce();
			this.bodies_tree().updateForce(b);
			b.update(dt);
		}
	}

	public BHTree bodies_tree() {
		return bodies_tree;
	}

	public void setBodiesTree(BHTree bodies_tree) {
		this.bodies_tree = bodies_tree;
	}

	@Override
	public void stop() {
		// nothing to do
	}
	
	protected void initializeDataStructures() {
		//nothing to do;
	}

}
