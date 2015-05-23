package nbody.console;

import java.io.File;
import java.io.FileNotFoundException;
import nbody.model.universe.*;

/**
 * <p>
 * Mini programita de consola para probar los diferentes algoritmos.
 * </p>
 *  Argumentos
 *  <ul>
 * 		<li>-i,--iteraciones=N: El algortimo se ejecuta durante <b>N</b> iteraciones. </li>
 * 		<li>-dt,--delta=DELTA: El algoritmo se ejecuta utilizando <b>DELTA</b> como parámetro para los pasos de la simulación. </li>
 *		<li>-a,--algoritmo=ALGORITMO: El programa se ejecuta utilizando el <b>ALGORITMO</b> seleccionado. El algoritmo puede ser una de las siguientes opciones: 
 *			<ul>
 *				<li>brute: algoritmo de fuerza bruta particula a particula</li>
 *				<li>brutep: algoritmo de fuerza bruta paralelizado</li>
 *				<li>barnes: algoritmo de Barnes y Hut</li>
 *				<li>barnesp: algoritimo de Barnes y Hut paralelizado</li>
 *				<li>barnespb: algoritmo de Barnes y Hut paralelizado y utlizando balanceo de carga por ORB.</li>
 *		</li>
 *		<li>-f,--file=ARCHIVO: El <b>ARCHIVO</b> de donde cargar los datos de los cuerpos. Se incluyen algunos archivos de ejemplo en el código fuente de este programa.</li>
 *		<li>-n,--ncuerpos=N: El algoritmo se corre con <b>N</b> cantidad de cuerpos random.</li>
 *	</ul>
 * 
 * @author jlasarte
 *
 */
public class NBody {
	
	/**
	 * Corre el programa
	 * @param args argumentos. @see Nbody
	 */
	public static void main(String[] args) {
		//TODO: usar los argumentos que documentamos je.
		// revisamos los parametros
        if (args.length != 4) {
            System.err.println("Usage: java NBody -i delta_t algorithm datafile ");
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
