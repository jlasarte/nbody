package nbody.model;

/**
 * Cuadrante para el algoritmo de Banres-Hut
 * @author jlasarte
 *
 */
public class Quadrant {
    
    private double xmid;
    private double ymid;
    private double length;   
 
    /**
     * Constructor: Crea un nuevo cuadrante
     *
     * @param xmid   centro x del cuadrante
     * @param ymid   centro y del cuadrante
     * @param longuitud del cuadrante
     */
    public Quadrant(double xmid, double ymid, double length) {
        this.setXmid(xmid);
        this.setYmid(ymid);
        this.setLength(length);
    }

    /**
     * Retorna la longitud del cuadrante
     *
     * @return longitud del cuadrante
     */
    public double length() {
        return getLength();
    }

    /**
     * Retorna si esta cuadrante contiene una posicion (x,y)
     *
     * @param x coordenada x a testear
     * @param y coordenada y a testear
     * @return  verdaderi si el cuadrante contiene a (x,y)
     */
    public boolean contains(double x, double y) {
        double halfLen = this.getLength() / 2.0;
        return (x <= this.getXmid() + halfLen && 
                x >= this.getXmid() - halfLen &&
                y <= this.getYmid() + halfLen && 
                y >= this.getYmid() - halfLen);
    }

    /**
     * Cuadrante NW de este cuadrante
     *
     * @return retorna el cuadrante NW de este cuadrante
     */
    public Quadrant NW() {
        double x = this.getXmid() - this.getLength() / 4.0;
        double y = this.getYmid() + this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant NW = new Quadrant(x, y, len);
        return NW;
    }

    /**
     * Cuadrante NE de este cuadrante
     *
     * @return retorna el cuadrante NE de este cuadrante
     */
    public Quadrant NE() {
        double x = this.getXmid() + this.getLength() / 4.0;
        double y = this.getYmid() + this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant NE = new Quadrant(x, y, len);
        return NE;
    }

    /**
     * Cuadrante SW  de este cuadrante
     *
     * @return retorna el cuadrante SW de este cuadrante
     */
    public Quadrant SW() {
        double x = this.getXmid() - this.getLength() / 4.0;
        double y = this.getYmid() - this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant SW = new Quadrant(x, y, len);
        return SW;
    }

    /**
     * Cuadrante SE de este cuadrante
     *
     * @return retorna el cuadrante SE de este cuadrante
     */
    public Quadrant SE() {
        double x = this.getXmid() + this.getLength() / 4.0;
        double y = this.getYmid() - this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant SE = new Quadrant(x, y, len);
        return SE;
    }

    /**
     * Retorna el punto medio del cuadrante en x
     * @return el punto medio del cuadrante en x
     */
	public double getXmid() {
		return xmid;
	}
    /**
     * Setea el punto medio del cuadrante en x
     * @return el punto medio del cuadrante en x a utilizar
     */
	public void setXmid(double xmid) {
		this.xmid = xmid;
	}

    /**
     * Retorna el punto medio del cuadrante en y
     * @return el punto medio del cuadrante en y
     */
	public double getYmid() {
		return ymid;
	}
    /**
     * Setea el punto medio del cuadrante en y
     * @return el punto medio del cuadrante en y a utilizar
     */
	public void setYmid(double ymid) {
		this.ymid = ymid;
	}

	/**
	 * Devuelve la longuitud del cuadrante
	 * @return longitud del cuadrante.
	 */
	public double getLength() {
		return length;
	}
	/**
	 * Setea la longuitud del cuadrante
	 * @return longitud del cuadrante a utilizar.
	 */
	public void setLength(double length) {
		this.length = length;
	}
}
