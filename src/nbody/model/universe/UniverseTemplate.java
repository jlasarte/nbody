package nbody.model.universe;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import nbody.model.Body;

public abstract class UniverseTemplate implements UniverseInterface {
	
	/**
	 * número de cuerpos en el universo
	 */
	protected int N = 0;
	/**
	 * Radio del universo, usado para escalar la visualizacion
	 */
	protected double R = 0.0;
	
	/**
	 * Arreglo de cuerpos en el universo.
	 */
	protected Body[] bodies_array = new Body[0]; // inicializamos en 0 para no tener problemas ocn la visalizacion hasta que el universo se inicializa.

	/**
	 * Método donde iniciar estructuras de datos especificas del universo. Es responsabilidad de las subclases.
	 */
	protected abstract void initializeDataStructures();
	
	@Override
	public void initialize(String data_file) throws FileNotFoundException {
		
		java.util.Scanner console;
		
		console = new Scanner(new File(data_file));
		
		// usamos locale english para no tener problemas con los archivos 
		// de universos bajados de internet, que utlizan el punto como separador de decimales
		// si utilizamos el locale de la pc (normalmente espa�ol), java espera la coma como separador de decimales
		// y la operaci�n retorna un error de parseo.
    	console.useLocale(Locale.ENGLISH); 
    	
    	this.N = console.nextInt();
	    this.R = console.nextDouble();
	    this.bodies_array = new Body[N];
	  
	    for (int i = 0; i < this.N; i++) {
        	double px = console.nextDouble();
            double py = console.nextDouble(); 
            double vx = console.nextDouble();
            double vy = console.nextDouble(); 
            double massi = console.nextDouble(); 
            int red     = console.nextInt();
            int green   = console.nextInt();
            int blue    = console.nextInt();
            Color color = new Color(red, green, blue);
            Body b =  new Body(px,py,vx,vy,massi,color);
            this.bodies_array[i] = b;
        }
		this.initializeDataStructures();

	    console.close();
	}
	
	public void initialize_random(int number_bodies, double radius) {
		this.N = number_bodies;
		this.R = radius;
		double rangeMin = -radius;
		double rangeMax = radius;
		Random r = new Random();
		this.bodies_array = new Body[N];
		for (int i = 0; i < number_bodies; i++ ) {
			double ry = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			double rx = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			double mass = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			double vy = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			double vx = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			Body b = new Body(rx,ry,vx,vy,mass,Color.WHITE);
			this.bodies_array[i] = b;
		}
		this.initializeDataStructures();
	}
	
	public void initialize_random_disk(int number_bodies, double radius) {
		this.N = number_bodies;
		this.R = radius;
		this.bodies_array = new Body[N];
		this.bodies_array = this.createsPointsGaussDisk(number_bodies, 40, 1,  40, .1, 1, 0.01, 0, 0, 0, 200, 10, 76, 12, 0, 10050);
	}
	
	
	public Body[] createsPointsGaussDisk(int count,
            double edge_size,double min_mass,double max_mass,
            double min_radius,double max_radius,
            double max_velocity,
            double disk_x,double disk_y,double disk_z,
            double disk_radius,double disk_thickness,
            double group_vel_ang_h,double group_vel_ang_v,
            double group_mag,double center_mult){
			// making gaussian distribution
		
		Body[] giveback = new Body[count];
		double x,y,z;
		double mass;
		Random rand = new Random();
		for (int i=1;i<count;i++){
			// generate position and velocity...for now the the disk will
			// remain flat on the XY plain...that may change
			x = disk_x+rand.nextGaussian()*disk_radius;
			y = disk_y+rand.nextGaussian()*disk_radius;
			z = 0;
			// create velocity
			// create unit vector from current point to disk center and unit
			// vector along z axis
			double u_pc[] = {disk_x-x,disk_y-y,disk_z-z};
			double z_axis[] = {0,0,1};
			double vel[] = {u_pc[1]*z_axis[2]-u_pc[2]*z_axis[1],
			                u_pc[2]*z_axis[0]-u_pc[0]*z_axis[2],
			                u_pc[0]*z_axis[1]-u_pc[1]*z_axis[0]};
			double dist = Math.sqrt(vel[0]*vel[0]+vel[1]*vel[1]+vel[2]*vel[2]);
			// velocity magnitude depend on how far from center
			double new_mag=(1-(u_pc[0]*u_pc[0]+u_pc[1]*u_pc[1]+u_pc[2]*u_pc[2])/
			(16*disk_radius*disk_radius))*max_velocity;
			new_mag=Math.abs(new_mag);
			vel[0]=vel[0]/dist*new_mag;
			vel[1]=vel[1]/dist*new_mag;
			vel[2]=0;
			// create mass based on how close it is to the center
			mass = (1-(u_pc[0]*u_pc[0]+u_pc[1]*u_pc[1]+u_pc[2]*u_pc[2])/(16*disk_radius*disk_radius))*(max_mass-min_mass)+min_mass;
			mass=Math.abs(mass);
			// apply a group velocity component
			vel[0]+=Math.cos(group_vel_ang_h*Math.PI/180.0)*
			        Math.cos(group_vel_ang_v*Math.PI/180.0)*group_mag;
			vel[1]+=Math.sin(group_vel_ang_h*Math.PI/180.0)*
			        Math.cos(group_vel_ang_v*Math.PI/180.0)*group_mag;
			// create the particle
				giveback[i]=new Body(x,y,vel[0],vel[1],mass,Color.WHITE);
			}
			// create one last point that will act as the center of mass for
			// the entire disk
			mass=center_mult*max_mass;
			mass+=min_mass;
			giveback[0]=new Body(disk_x,disk_y,0,0,mass,Color.RED);
			
			return giveback;
	}

	@Override
	public abstract void update(double dt);

	@Override
	public abstract void stop();

	@Override
	public double scale() {
		return this.R;
	}

	@Override
	public Body[] bodies() {
		return this.bodies_array;
	}

}
