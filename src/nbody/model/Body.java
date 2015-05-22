package nbody.model;

import java.awt.Color;

import nbody.model.universe.ParallelBalancedBarnesHutUniverse.coordinate;

/**
 * 
 * @author jlasarte
 *
 */
/**
 * Body.java
 *
 * Represents a Body (a point mass) and its position, 
 * velocity, mass, color, and the net force acting upon it.
 *
 * @author chindesaurus
 * @version 1.00
 */


public class Body {

    // gravitational constant
    private static final double G = 6.67e-11;

    private int work;
    private double rx, ry;       // position
    private double vx, vy;       // velocity
    private double fx, fy;       // force
    private double mass;         // mass
    private Color color;         // color

    /**
     * Constructor: creates and initializes a new Body.
     *
     * @param rx    the x-position of this new body
     * @param ry    the y-position of this new body
     * @param vx    the x-velocity of this new body
     * @param vy    the y-velocity of this new body
     * @param mass  the mass of this new body
     * @param color the color of this new body (RGB)
     */
    public Body(double rx, double ry, double vx, double vy, double mass, Color color) {
        this.setRx(rx);
        this.setRy(ry);
        this.vx    = vx;
        this.vy    = vy;
        this.mass  = mass;
        this.setColor(color);
        this.work = 1;
    }

    /**
     * Updates the velocity and position of the invoking Body
     * using leapfrom method, with timestep dt.
     *
     * @param dt the timestep for this simulation
     */
    public void update(double dt) {
        vx += dt * fx / mass;
        vy += dt * fy / mass;
        setRx(rx() + dt * vx);
        setRy(ry() + dt * vy);
    }

    /**
     * Returns the Euclidean distance between the invoking Body and b.
     *
     * @param b the body from which to determine the distance
     * @return  the distance between this and Body b
     */
    public double distanceTo(Body b) {
        double dx = rx() - b.rx();
        double dy = ry() - b.ry();
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Resets the force (both x- and y-components) of the invoking Body to 0.
     */
    public void resetForce() {
        fx = 0.0;
        fy = 0.0;
    }

    /** 
     * Computes the net force acting between the invoking body and b, and
     * adds this to the net force acting on the invoking Body.
     *
     * @param b the body whose net force on this body to calculate
     */
    public void addForce(Body b) {
        Body a = this;
        double EPS = 3E4;      // softening parameter
        double dx = b.rx() - a.rx();
        double dy = b.ry() - a.ry();
        double dist = Math.sqrt(dx*dx + dy*dy);
        double F = (G * a.mass * b.mass) / (dist*dist + EPS*EPS);
        a.fx += F * dx / dist;
        a.fy += F * dy / dist;
        
    }
    
    /**
     * Returns a string representation of this body formatted nicely.
     *
     * @return a formatted string containing this body's x- and y- positions,
     *         velocities, and mass
     */
    public String toString() {
        return String.format("%10.3E %10.3E %10.3E %10.3E %10.3E", rx(), ry(), vx, vy, mass);
    }

    /**
     * Returns true if the body is in quadrant q, else false.
     *
     * @param q the Quad to check
     * @return  true iff body is in Quad q, else false
     */
    public boolean in(Quadrant q) {
        return q.contains(this.rx(), this.ry()); 
    }

    /** 
     * Returns a new Body object that represents the center-of-mass
     * of the invoking body and b.
     *
     * @param b the body to aggregate with this Body
     * @return  a Body object representing an aggregate of this 
     *          and b, having this and b's center of gravity and
     *          combined mass
     */
    public Body plus(Body b) {
        Body a = this;

        double m = a.mass + b.mass;
        double x = (a.rx() * a.mass + b.rx() * b.mass) / m;
        double y = (a.ry() * a.mass + b.ry() * b.mass) / m;

        return new Body(x, y, a.vx, b.vx, m, a.color());
    }

	public Color color() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double rx() {
		return rx;
	}

	public void setRx(double rx) {
		this.rx = rx;
	}

	public double ry() {
		return ry;
	}

	public void setRy(double ry) {
		this.ry = ry;
	}

	public boolean inDirection(int above, coordinate spc, nbody.model.universe.ParallelBalancedBarnesHutUniverse.quadrant q) {
		double lenghtx = - q.xmin + q.xmax; // -xmin + xmax
		double lenghty = - q.ymin + q.ymax; // -ymin + ymax
		boolean parallel_to_x = lenghtx > lenghty;
		if (above>0) {
			// true es en direccion X
			if (parallel_to_x)
				return this.rx > q.xmin && this.rx < q.xmax && this.ry > spc.y && this.ry < q.ymax;
			return this.ry > q.ymin && this.ry < q.ymax && this.rx > q.xmin && this.rx < spc.x;
		} else {
			if (parallel_to_x)
				return this.rx > q.xmin && this.rx < q.xmax && this.ry < spc.y && this.ry > q.ymin;
			return this.ry > q.ymin && this.ry < q.ymax && this.rx > spc.x && this.rx < q.xmax;
		}
	}

	public int work() {
		// TODO Auto-generated method stub
		return this.work;
	}
	
	public void incWork() {
		this.work++;
	}

	public void resetWork() {
		// TODO Auto-generated method stub
		this.work = 1;
	}
}
