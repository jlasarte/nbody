package nbody.model;

import java.awt.Color;

import nbody.model.universe.ParallelBalancedBarnesHutUniverse.coordinate;

/**
 *
 * Representa un Cuerpo ubicado en un punto en el espacio, con velocidad y fuerzas que actuan sobre él.
 * 
 * @author jlasarte
 */
public class Body {

	/**
	 * Constante gravitatoria universal.
	 */
    private static final double G = 6.67e-11;
    
    /**
     * Trabajo asociado con la actualización de fuerza de este cuerpo. Utilizado para el balanceo de carga.
     */
    private int work;
    /**
     * Posición en el eje X 
     */
    private double rx;
    /**
     * Posición en el eje Y
     */
    private double ry;
    /**
     * Velocidad en el eje X
     */
    private double vx;
    /**
     * Velocidad en el eje Y
     */
    private double vy;
    /**
     * Fuerza que actua sobre este cuerpo sobre el eje X
     */
    private double fx;
    /**
     * Fuerza que actua sobre este cuerpo sobre el eje Y
     */
    private double fy; 
    /**
     * Masa de este cuerpo.
     */
    private double mass;
    /**
     * Color del cuerpo, utilizado para la visualización.
     */
    private Color color;

    /**
     * Constructor: crea a inicializa un nuevo cuerpo.
     *
     * @param rx    posicion en el eje x
     * @param ry    posicion en el eje y
     * @param vx    velocidad sobre x
     * @param vy    velocidad sobre y
     * @param mass  masa del cuerpo
     * @param color color del cuerpo
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
     * Acutaliza la velocidad y posicion del cuerpo con un paso dt
     * @param dt el delta tiempo para esta simulación.
     */
    public void update(double dt) {
        vx += dt * fx / mass;
        vy += dt * fy / mass;
        setRx(rx() + dt * vx);
        setRy(ry() + dt * vy);
    }

    /**
     * Retorna la distancia entre este cuerpo y otro.
     *
     * @param b el cuerpo hacia el cual determinar la distancia.
     * @return la distancia entre este cuerpo y b,
     */
    public double distanceTo(Body b) {
        double dx = rx() - b.rx();
        double dy = ry() - b.ry();
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Resetea las fuerzas actuando sobre el cuerpo a 0.
     */
    public void resetForce() {
        fx = 0.0;
        fy = 0.0;
    }

    /** 
     * Calcula la fuerza actuando entre este cuerpo y b y la suma a este cuerpo.
     *
     * @param b el cuerpo cuya fuerza sobre este cuerpo calcular.
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
	
	/**
	 * Devuelve el trabajo asociado con la actualización de fuerza de este cuerpo.
	 * Utilizado por el algorítmo de ORB en ParallelBalancedBarnesHutUniverse
	 * @return trabajo asociado con la actualización de este cuerpo.
	 */
	public int work() {
		return this.work;
	}
	
	/**
	 * Incrementa el trabajo asociado al cuerpo.
	 */
	public void incWork() {
		this.work++;
	}

	/**
	 * Resetea el trabajo asociado a este cuerpo a 1.
	 */
	public void resetWork() {
		this.work = 1;
	}
}
