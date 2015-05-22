package nbody.console;

import java.io.File;
import java.io.FileNotFoundException;

import nbody.model.universe.*;

/**
 * Mini programita de consola para probar el desempeño de los diferentes algoritmos.
 * @author julia
 *
 */
public class NBody {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	
		// revisamos los parametros
        if (args.length != 4) {
            System.err.println("Usage: java NBody T delta_t algorithm datafile ");
            System.exit(1);
        }
        
        // el tiempo a simular
    	double t = Double.parseDouble(args[0]);
    	// delta tiempo, el "paso" a simular
    	double dt = Double.parseDouble(args[1]);
    	// algoritmo a utilizar en la simulacion: fuerza bruta, fuerza bruta paralelizado o barnes hut.
    	String algorithm = args[2];
    	// archivo de datos con los cuerpos
    	String data_file_path = args[3];
    	
		String workingDir = System.getProperty("user.dir");
		
		File data_file = new File(workingDir+"/src/inputs/"+data_file_path);
		


    	UniverseInterface u = null;

    	switch (algorithm) {
    		case "barnes" : 
    			u = new BarnesHutUniverse();
    			break;
    		case "brute":
    			u = new SecuentialBruteForceUniverse();
    			break;
    		case "brutep":
    			u = new ParallelBruteForceUniverse();
    			break;
    		case "barnesp":
    			u = new ParallelBarnesHutUniverse();
    			break;
    		case "barnespb":
    			u = new ParallelBalancedBarnesHutUniverse();
    			break;
    		default:
                System.err.println("Algoritmo no permitido");
                System.exit(1);
                break;
    	}
    	

    	
    	try {
			u.initialize(data_file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
            System.err.println("Error al abrir el archivo de datos ");
		} 
        long startTime = System.currentTimeMillis();
    	for (double ct = 0; ct < t; ct+=dt) {
    		u.update(dt);
    		System.out.println(ct);
    	}
    	u.stop(); 
    	
    	long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
	}

}
