package nbody.model.universe;

import java.io.FileNotFoundException;

import nbody.model.Body;

/***
 * Interface del Universo.
 * @author jlasarte
 *
 */
public interface UniverseInterface {
		
	/**
	 * Inicializa el universo con los datos contenidos en el archivo pasado como par�metro.
	 * El archivo deber�a tener el siguiente formato
	 *     NumeroDeCuerpos
	 *     Radio
	 *     Lista de Cuerpos, uno por L�nea
	 * Ejemplo:
	 *    3
     *    1.25e11
	 *	  0.000e00 0.000e00 0.0500e04 0.000e00  5.974e24 231 111 247
	 *	  0.000e00 4.500e10 3.000e04 0.000e00   1.989e30 255 136 0
	 *    0.000e00 -4.500e10 -3.000e04 0.000e00 1.989e30 255 136 0
	 *    
	 * Los cuerpos contienen los siguientes datos: 
	 * - Posici�n en el eje X
	 * - Posici�n en el eje Y
	 * - Velocidad en el eje X
	 * - Velocidad en el eje Y
	 * - Color expresado en tres enteros que indican R G B [S�lo usado para visualizaci�n]
	 * @param data_file String ruta del archivo de datos a utilizar
	 * @throws FileNotFoundException cuando el archivo pasado como par�metro no se encuentra o no se puede abrir.
	 */
	public void initialize(String data_file) throws FileNotFoundException;
	/**
	 * Actualiza el universo un "paso" de delta tiempo indicado por dt.
	 * @param dt
	 */
	public void update(double dt);
	/**
	 * Detiene el universo y libera recuersos.
	 */
	public void stop();
	/**
	 * Devuelve la escala de este universo
	 * @return double la escala del universo.
	 */
	public double scale();
	/**
	 * Retorna el arregalo de cuerpos del universo,
	 * @return Body[] el arreglo de cuerpos.
	 */
	public Body[] bodies();
	/**
	 * 
	 */
	public void initialize_random(int number_bodies, double radius);
}
