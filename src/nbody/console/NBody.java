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
 * 		<li>-dt,--delta=DELTA: El algoritmo se ejecuta utilizando <b>DELTA</b> como parametro para los pasos de la simulacion. </li>
 *		<li>-a,--algoritmo=ALGORITMO: El programa se ejecuta utilizando el <b>ALGORITMO</b> seleccionado. El algoritmo puede ser una de las siguientes opciones: 
 *			<ul>
 *				<li>brute: algoritmo de fuerza bruta particula a particula</li>
 *				<li>brutep: algoritmo de fuerza bruta paralelizado</li>
 *				<li>barnes: algoritmo de Barnes y Hut</li>
 *				<li>barnesp: algoritimo de Barnes y Hut paralelizado</li>
 *				<li>barnespb: algoritmo de Barnes y Hut paralelizado y utlizando balanceo de carga por ORB.</li>
 *		</li>
 *		<li>-f,--file=ARCHIVO: El <b>ARCHIVO</b> de donde cargar los datos de los cuerpos. Se incluyen algunos archivos de ejemplo en el codigo fuente de este programa.</li>
 *		<li>-n,--ncuerpos=N: El algoritmo se corre con <b>N</b> cantidad de cuerpos random.</li>
 *	</ul>
 * 
 * @author jlasarte
 *
 */
public class NBody {
	
	private static String use_help = "Utilice el Nbody -help para ver las opcciones disponibles.";
		
	/**
	 * Corre el programa
	 * @param args argumentos.
	 */
	public static void main(String[] args) {
		
		int iteraciones = 0;
		double dt = 0.0;
		int threads = 0;
		boolean parallel = false;
		
		if (args.length == 0 || args[0].equals("-help")) {
			String help = "Mini programita de consola para probar los diferentes algoritmos. \n";
			help += "Argumentos \n";
			help += "\t -i,--iteraciones=N: El algortimo se ejecuta durante N iteraciones. \n";
			help += "\t -dt,--delta=DELTA: El algoritmo se ejecuta utilizando DELTA como parametro para los pasos de la simulacion.  \n";
			help += "\t -a,--algoritmo=ALGORITMO: El programa se ejecuta utilizando el ALGORITMO seleccionado. El algoritmo puede ser una de las siguientes opciones: \n";
			help += "\t\t brute: algoritmo de fuerza bruta particula a particula \n";
			help += "\t\t brutep: algoritmo de fuerza bruta paralelizado \n";
			help += "\t\t barnes: algoritmo de Barnes y Hut \n";
			help += "\t\t barnesp: algoritimo de Barnes y Hut paralelizado \n";
			help += "\t\t barnespb: algoritmo de Barnes y Hut paralelizado y utlizando balanceo de carga por ORB. \n";
			help += "\t -f,--file=ARCHIVO: El ARCHIVO de donde cargar los datos de los cuerpos. Se incluyen algunos archivos de ejemplo en el codigo fuente de este programa. \n";
			help += "\t -n,--ncuerpos=N: El algoritmo se corre con N cantidad de cuerpos random. \n";
			help += "\t -t,--threads: En caso de utilizarse un algoritmo paralelizado, indica el numero de threads a utilizar. El default es el numero de nucleos devuelto por la informacion del sistema.";
			help += "\t -help: Imprime esta ayuda";
			System.out.println(help);
	        System.exit(0);
		}
		
		// revisamos los parametros        
		if (args.length < 8) {
			System.err.println("Cantidad de parámetros inválida.");
            System.err.println(use_help);
            System.exit(1);
        }
		
		if (!args[0].equals("-i") && !args[0].equals("--iteraciones")) {
            System.err.println("El parámetro -i es obligario");
        	System.exit(1);
		}
        try {
        	iteraciones = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Valor no válido para el parámetro -i número de iteraciones.");
            System.err.println(use_help);
        	System.exit(1);
        }
        
		if (!args[2].equals("-dt") && !args[2].equals("--delta")) {
            System.err.println("El parámetro -dt es obligario");
        	System.exit(1);
		}        
        try {
        	dt = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Valor no válido para el parámetro -dt delta tiempo.");
            System.err.println(use_help);
        	System.exit(1);
        }
        
        if (!args[4].equals("-a") && !args[4].equals("--algoritmo")) {
            System.err.println("El parámetro -a es obligario");
        	System.exit(1);
        }
        String algorithm = args[5];

    	UniverseInterface u = null;
    	
    	if (algorithm.equals("barnesp") || algorithm.equals("brutep") || algorithm.equals("barnespb")) {
    		parallel = true;
    		if (args.length > 9) {
	    		if (args[8].equals("-t") || args[8].equals("-threads")) {
	    			try {
	    				threads = Integer.parseInt(args[9]);
	    			} catch (NumberFormatException e) {
	    	            System.err.println("Valor no válido para el parámetro -t número de threads.");
	    	            System.err.println(use_help);
	    	        	System.exit(1);
	    	        }
	    		} else {
	    			threads = Runtime.getRuntime().availableProcessors();
	    		}
    		}
    	}

    	switch (algorithm) {
    		case "barnes" : 
    			u = new BarnesHutUniverse();
    			break;
    		case "brute":
    			u = new SecuentialBruteForceUniverse();
    			break;
    		case "brutep":
    			u = new ParallelBruteForceUniverse(threads);
    			break;
    		case "barnesp":
    			u = new ParallelBarnesHutUniverse(threads);
    			break;
    		case "barnespb":
    			u = new ParallelBalancedBarnesHutUniverse(threads);
    			break;
    		default:
                System.err.println("Algoritmo no permitido");
                System.err.println(use_help);
                System.exit(1);
                break;
    	}
        
    	
        if (!args[6].equals("-f") && !args[6].equals("--file") && ! args[6].equals("-n") &&  ! args[6].equals("--ncuerpos")) {
            System.err.println("Debe incluir el parámetro -n o -f");
        	System.exit(1);
		}
        if (args[6].equals("-f") || args[6].equals("-file")) {
        	
        	String data_file_path = args[7];
    		File data_file = new File(data_file_path);
    		try {
    			u.initialize(data_file.getAbsolutePath());
    		} catch (FileNotFoundException e) {
                System.err.println("Error al abrir el archivo de datos ");
                System.exit(1);
    		} 
        } else {
        	 try {
             	int N = Integer.parseInt(args[7]);
             	u.initialize_random(N, 100.0);
             } catch (NumberFormatException e) {
                 System.err.println("Valor no válido para el parámetro -n numero de cuerpos.");
                 System.err.println(use_help);
             	System.exit(1);
             }
        }
        
    	
    	
        long startTime = System.currentTimeMillis();
    	for (int i = 0; i < iteraciones; i++) {
    		u.update(dt);
    		if (i % 10 == 0)
    			System.out.printf("-- Ejecutando iteracion %d \n", i+1);
    	}
    	u.stop(); 

    	String result = "Ejecutadas %d iteraciones del algorítmo %s con un delta tiempo %f en %d milisegundos sobre un total de %d cuerpos.";
    	if (parallel)
    		result += " Utilizando %d threads.";
    	result += "\n";
    	long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        System.out.printf(result,iteraciones,algorithm,dt,elapsedTime,u.bodies().length, threads);
	}

}
