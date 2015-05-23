package nbody.model;

/** 
 * Quadrant.java
 *
 * Represents quadrants for the Barnes-Hut algorithm. 
 *
 *
 */

public class Quadrant {
    
    private double xmid;
    private double ymid;
    private double length;   
 
    /**
     * Constructor: creates a new Quad with the given 
     * parameters (assume it is square).
     *
     * @param xmid   x-coordinate of center of quadrant
     * @param ymid   y-coordinate of center of quadrant
     * @param length the side length of the quadrant
     */
    public Quadrant(double xmid, double ymid, double length) {
        this.setXmid(xmid);
        this.setYmid(ymid);
        this.setLength(length);
    }

    /**
     * Returns the length of one side of the square quadrant.
     *
     * @return side length of the quadrant
     */
    public double length() {
        return getLength();
    }

    /**
     * Does this quadrant contain (x, y)?
     *
     * @param x x-coordinate of point to test
     * @param y y-coordinate of point to test
     * @return  true if quadrant contains (x, y), else false
     */
    public boolean contains(double x, double y) {
        double halfLen = this.getLength() / 2.0;
        return (x <= this.getXmid() + halfLen && 
                x >= this.getXmid() - halfLen &&
                y <= this.getYmid() + halfLen && 
                y >= this.getYmid() - halfLen);
    }

    /**
     * Returns a new object that represents the northwest quadrant.
     *
     * @return the northwest quadrant of this Quad
     */
    public Quadrant NW() {
        double x = this.getXmid() - this.getLength() / 4.0;
        double y = this.getYmid() + this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant NW = new Quadrant(x, y, len);
        return NW;
    }

    /**
     * Returns a new object that represents the northeast quadrant.
     *
     * @return the northeast quadrant of this Quad
     */
    public Quadrant NE() {
        double x = this.getXmid() + this.getLength() / 4.0;
        double y = this.getYmid() + this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant NE = new Quadrant(x, y, len);
        return NE;
    }

    /**
     * Returns a new object that represents the southwest quadrant.
     *
     * @return the southwest quadrant of this Quad
     */
    public Quadrant SW() {
        double x = this.getXmid() - this.getLength() / 4.0;
        double y = this.getYmid() - this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant SW = new Quadrant(x, y, len);
        return SW;
    }

    /**
     * Returns a new object that represents the southeast quadrant.
     *
     * @return the southeast quadrant of this Quad
     */
    public Quadrant SE() {
        double x = this.getXmid() + this.getLength() / 4.0;
        double y = this.getYmid() - this.getLength() / 4.0;
        double len = this.getLength() / 2.0;
        Quadrant SE = new Quadrant(x, y, len);
        return SE;
    }

	public double getXmid() {
		return xmid;
	}

	public void setXmid(double xmid) {
		this.xmid = xmid;
	}

	public double getYmid() {
		return ymid;
	}

	public void setYmid(double ymid) {
		this.ymid = ymid;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
}
