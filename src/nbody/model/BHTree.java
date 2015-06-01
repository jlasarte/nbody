package nbody.model;


/**
 * BHTree.java
 * 
 * Representa un quadtree para el algoritmo de Barnes y Hut
 *
 *
 * @author jlasarte
 */
public class BHTree {

	/**
	 * parametro frontera utilizado para decidir si un cuerpo esta lo suficientemente lejos de otro y asi aproximar el calculo utilizando su nodo padre.
	 * Thetas mayores generan mayor presicion, pero disminuyen la velocidad. Si Theta = 0 el algoritmo degenera a una version secuencial.
	 */
	protected final double Theta = 0.5;
	/**
	 * Cuerpo o "Grupo de cuerpos" de este nodo. Si es un nodo que aproxima un grupo de cuerpos simplemente guardamos la sumatoria de las masas.
	 */
    protected Body body;     
    /**
     * Cuadrante representado por este Arbol.
     */
    protected Quadrant quad;  
    /**
     * BHTree del cuadrante noroeste.
     */
    protected BHTree NW;
    /**
     * BHTree del cuadrante noreste
     */
    protected BHTree NE;
    /**
     * BHTree del cuadrante sudoeste
     */
    protected BHTree SW;
    /**
     * BHTree del cuadrante sudeste.
     */
    protected BHTree SE;
  
    /**
     * Constructor. Crea un nuevo BTree vacio.
     * @param q el cuadrante dentro del que este arbol esta contenido.
     */
    public BHTree(Quadrant q) {
        this.quad = q;
        this.body = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
    }
 

    /**
     * Inserta recursivamente un nuevo cuerpo al arbol
     * @param b cuerpo a insertar. Al insertar el cuerpo se resetea el trabajo asociado al calculo de fuerzas utlizado en ORB. 
     * @see Body 
     * @see ParallelBalancesBarnesHutUniverse
     */
    public void insert(Body b) {
    	
    	// si el nodo esta vacio insertamos el nodo.
        if (body == null) {
        	b.resetWork(); // reseteamos el trabajo de un cuerpo.
            body = b;
            return;
        }
  
        if (! isExternal()) {
        	// si no es hoja, ademas de insertar el nodo tenemos que actualizar el centro de gravedad y la masa.
        	body = body.plus(b);
        	// insertamos el nodo en el cuadrante que corresponda
            putBody(b);
        }

        else {
        	// si el nodo es hoja y tiene un cuerpo, tenemos que subdividirlo en cuadro nuevos nodos.
        	NW = new BHTree(quad.NW());
            NE = new BHTree(quad.NE());
            SE = new BHTree(quad.SE());
            SW = new BHTree(quad.SW());

            //insertamos el cuerpo actual y el nodo nuevo en el cuadrante que corresponda
            putBody(this.body);
            putBody(b);

            // y actualizamos el centro de masa del cuerpo actual.
            body = body.plus(b);
        }
    }

    

    /**
     * Inserta un cuerpo en el BHtree del cuadrante que corresponda.
     */ 
    protected void putBody(Body b) {
        if (b.in(quad.NW()))
            NW.insert(b);
        else if (b.in(quad.NE()))
            NE.insert(b);
        else if (b.in(quad.SE()))
            SE.insert(b);
        else if (b.in(quad.SW()))
            SW.insert(b);
    }


    /**
     * verdadero si el nodo actual es una hoja.
     */
    protected boolean isExternal() {
        // a node is external iff all four children are null
        return (NW == null && NE == null && SW == null && SE == null);
    }


    /**
     * Calcula la fuerza actuando sobre un cuerpo b desde todos los cuerpos, aproximando en los casos que sea posible y actualiza la fuerza de b de acuerdo al resultado.
     */
    public void updateForce(Body b) {
    	// incrementamos el trabajo asociado con el calculo de fuerza del cuerpo b, utlizado por el ORB en el algoritmo paralelo con balanceo de carga
    	b.incWork();
    	// caso base, llegamos al final o encontramos el cuerpo
        if (body == null || b.equals(body))
            return;
        
        // Si estamos en una hoja, agregar la fuerza del cuerpo a b
        if (isExternal()) 
            b.addForce(body);
 
       
        else {
        	
            //ancho del cuadrante representado por el nodo
            double s = quad.length();

            // distancia entre b y el centro de masa de este nodo
            double d = body.distanceTo(b);

            // comparamos s/d con el parametro frontera 
            if ((s / d) < Theta)
            	// el grupo de cuerpos representado por este nodo esta lo suficientemente lejos como para poder aproximarlo como un único cuerpo.
                b.addForce(body);
            
            // sino esta lo suficientemente lejos tenemos que actualizar con cada uno de sus hijos.
            else {
                NW.updateForce(b);
                NE.updateForce(b);
                SW.updateForce(b);
                SE.updateForce(b);
            }
        }
    }
    

}
