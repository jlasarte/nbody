package nbody.model;

import java.util.Stack;

/**
 * Cuando java crea una thread, alloca un espacio default para la memoria de la thread en la stack.
 * con un sistema de memoria comparida esto no ser�a necesario, pero s�lo puede cambiarse el tama�ano de la stack
 * modificando un parametro en la maquina virtual que corre java.
 * la opcci�n de generar una versi�n de BHTree que utilizara solo procedimientos iterativos,
 * reduciendo la necesidad del stack, me parecio m�s porable.
 * @author jlasarte
 *
 */
public class IterativeBHTree extends BHTree {
	
	class SnapShot {
		public Body b;
		public BHTree thisbh;
	} 
	
	public IterativeBHTree(Quadrant q) {
		super(q);
	}
	
	
	
	public void insert(Body b) {
    	Stack<SnapShot> snapShotStack = new Stack<SnapShot>();
    	
    	SnapShot currentSS = new SnapShot();
    	currentSS.b = b;
    	currentSS.thisbh = this;
    	
    	while (!snapShotStack.empty()) {
    		SnapShot c = snapShotStack.pop();
    		
    		  // if this node does not contain a body, put the new body b here
    	    if (c.thisbh.body == null) {
    	    	c.thisbh.body = c.b;
    	        continue;
    	    } else {
	
	    	    // internal node
	    	    if (! c.thisbh.isExternal()) {
	    	        // update the center-of-mass and total mass
	    	        c.thisbh.body = c.thisbh.body.plus(c.b);
	    	        
	    	        // recursively insert Body b into the appropriate quadrant
	    	        snapShotStack.push(putBody(b,c.thisbh));
	    	        continue;
	    	    }
	
	    	    // external node
	    	    else {
	    	        // subdivide the region further by creating four children
	    	        c.thisbh.NW = new IterativeBHTree(c.thisbh.quad.NW());
	    	        c.thisbh.NE = new IterativeBHTree(c.thisbh.quad.NE());
	    	        c.thisbh.SE = new IterativeBHTree(c.thisbh.quad.SE());
	    	        c.thisbh.SW = new IterativeBHTree(c.thisbh.quad.SW());
	
	    	        // recursively insert both this body and Body b into the appropriate quadrant
	    	        snapShotStack.push(putBody(c.thisbh.body,c.thisbh));
	    	        snapShotStack.push(putBody(c.b,c.thisbh));
	
	    	        // update the center-of-mass and total mass
	    	        c.thisbh.body = c.thisbh.body.plus(c.b);
	    	        continue;
	    	    }
    	    }
    	}
	}
	
	
	private SnapShot putBody(Body b, BHTree thisbh) {
        SnapShot s = new SnapShot();
        s.b = b;
		if (b.in(thisbh.quad.NW()))
			s.thisbh = thisbh.NW;
        else if (b.in(thisbh.quad.NE()))
			s.thisbh = thisbh.NE;
        else if (b.in(thisbh.quad.SE()))
			s.thisbh = thisbh.SE;
        else if (b.in(thisbh.quad.SW()))
			s.thisbh = thisbh.SW;
		
		return s;
	}
	
	/**
	 * 
	 */
    public void updateForce(Body b) {
    	
    	//b.incWork();
    	
    	Stack<SnapShot> snapShotStack = new Stack<SnapShot>();
    	
    	SnapShot currentSS = new SnapShot();
    	currentSS.b = b;
    	currentSS.thisbh = this;
    	
    	snapShotStack.push(currentSS);
    	
    	while (!snapShotStack.empty()) {
    		//b.incWork();
    		SnapShot c = snapShotStack.pop();
    		if (c.thisbh.body == null || c.b.equals(c.thisbh.body)) {
    			continue;
    		} else {
    			// if the current node is external, update net force acting on b
    	        if (c.thisbh.isExternal()) {
    	            c.b.addForce(c.thisbh.body);
    	        } else {
	    			// width of region represented by internal node
	                double s = c.thisbh.quad.length();
	
	                // distance between Body b and this node's center-of-mass
	                double d = c.thisbh.body.distanceTo(c.b);
	
	                // compare ratio (s / d) to threshold value Theta
	                if ((s / d) < Theta) {
	                	c.b.addForce(c.thisbh.body);   // b is far away
	                
	                // recurse on each of current node's children
	                } else {
	                	SnapShot NWSS = new SnapShot();
	                	NWSS.b = c.b;
	                	NWSS.thisbh = c.thisbh.NW;
	                	
	                	SnapShot NESS = new SnapShot();
	                	NESS.b = c.b;
	                	NESS.thisbh = c.thisbh.NE;
	                	
	                	SnapShot SWSS = new SnapShot();
	                	SWSS.b = c.b;
	                	SWSS.thisbh = c.thisbh.SW;
	                	
	                	SnapShot SESS = new SnapShot();
	                	SESS.b = c.b;
	                	SESS.thisbh = c.thisbh.SE;

	                	
	                	snapShotStack.push(NWSS);
	                	snapShotStack.push(NESS);
	                	snapShotStack.push(SWSS);
	                	snapShotStack.push(SESS);
	                	
	                	continue;
	                }
    	        }
    		}
    	}
    }
}
