package nbody.model;

import java.util.Stack;

/**
 * Subclase de BHTree que implementa todos sus métodos recursivos de manera iterativa.
 * cuando java crea un thread, le adjudica una cierta cantidad de memoria en el stack. 
 * Lo que sucede entonces es que al crearse una thread, la thread principal pierde 1MB
 * (aproximadamente, depende de la configuración de cada usuario) de memoria. En general,
 * esto no es un problema, pero al construir un arbol de manera recursiva con 50.000 o
 * 100000 elementos, nos encontramos con problemas de stack overflow en la construcción
 * y actualización del árbol.
 * Debido a que JAVA no implementa optimizaciones para tail-recursión, la opción más 
 * viable fue la implementación de una subclase iterativa de BHTree
 * 
 * @author julia
 *
 */
public class IterativeBHTree extends BHTree {
	/**
	 * Guarda el estado actual de la iteración.
	 * @author julia
	 *
	 */
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
    	
    	snapShotStack.push(currentSS);
    	
    	while (!snapShotStack.empty()) {
    		SnapShot c = snapShotStack.pop();
    		
    	    if (c.thisbh.body == null) {
    	    	c.thisbh.body = c.b;
    	        continue;
    	    } else {
	
	    	    if (! c.thisbh.isExternal()) {

	    	    	c.thisbh.body = c.thisbh.body.plus(c.b);
	    	        snapShotStack.push(putBody(b,c.thisbh));
	    	        continue;
	    	    }
	
	    	    else {
	    	    	
	    	        c.thisbh.NW = new IterativeBHTree(c.thisbh.quad.NW());
	    	        c.thisbh.NE = new IterativeBHTree(c.thisbh.quad.NE());
	    	        c.thisbh.SE = new IterativeBHTree(c.thisbh.quad.SE());
	    	        c.thisbh.SW = new IterativeBHTree(c.thisbh.quad.SW());
	
	    	        snapShotStack.push(putBody(c.thisbh.body,c.thisbh));
	    	        snapShotStack.push(putBody(c.b,c.thisbh));
	
	    	        c.thisbh.body = c.thisbh.body.plus(c.b);
	    	        continue;
	    	    }
    	    }
    	}
	}
	
	/**
	 * Retorna una snapshot para la inserción de un cuerpo
	 * @param b el cuerpo a insertar
	 * @param thisbh "this" BHTree, simula el arbol que recibiría la llamada recursiva
	 * @return la snapshot para la isnerción de un cuerpo.
	 */
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
	

    public void updateForce(Body b) {
    	
    	b.incWork();
    	
    	Stack<SnapShot> snapShotStack = new Stack<SnapShot>();
    	
    	SnapShot currentSS = new SnapShot();
    	currentSS.b = b;
    	currentSS.thisbh = this;
    	
    	snapShotStack.push(currentSS);
    	
    	while (!snapShotStack.empty()) {
    		SnapShot c = snapShotStack.pop();
    		if (c.thisbh.body == null || c.b.equals(c.thisbh.body)) {
    			continue;
    		} else {
    	        if (c.thisbh.isExternal()) {
    	            c.b.addForce(c.thisbh.body);
    	        } else {
	                double s = c.thisbh.quad.length();
	
	                double d = c.thisbh.body.distanceTo(c.b);
	
	                if ((s / d) < Theta) {
	                	c.b.addForce(c.thisbh.body);  
	                
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
