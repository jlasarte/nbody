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
	 * n�mero de cuerpos en el universo
	 */
	protected int N = 0;
	/**
	 * Radio del universo, usado para escalar la visualizaci�n
	 */
	protected double R = 0.0;
	
	/**
	 * Arreglo de cuerpos en el universo.
	 */
	protected Body[] bodies_array = new Body[0]; // inicializamos en 0 para no tener problemas ocn la visalizaci�n hasta que el universo se inicializa.

	/**
	 * Método donde iniciar estructuras de datos específicas del universo
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
	  
	    this.load_bodies(console);
		this.initializeDataStructures();

	    console.close();
	}
	
	protected void load_bodies(Scanner console) {
		
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
