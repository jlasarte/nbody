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
	 * Inicializa el universo con los datos contenidos en el archivo pasado como parametro.
	 * El archivo deberaa tener el siguiente formato
	 *     NumeroDeCuerpos
	 *     Radio
	 *     Lista de Cuerpos, uno por Línea
	 * Ejemplo:
	 *    3
     *    1.25e11
	 *	  0.000e00 0.000e00 0.0500e04 0.000e00  5.974e24 231 111 247
	 *	  0.000e00 4.500e10 3.000e04 0.000e00   1.989e30 255 136 0
	 *    0.000e00 -4.500e10 -3.000e04 0.000e00 1.989e30 255 136 0
	 *    
	 * Los cuerpos contienen los siguientes datos: 
	 * - Posicion en el eje X
	 * - Posicion en el eje Y
	 * - Velocidad en el eje X
	 * - Velocidad en el eje Y
	 * - Color expresado en tres enteros que indican R G B [Solo usado para visualizacion]
	 * @param data_file String ruta del archivo de datos a utilizar
	 * @throws FileNotFoundException cuando el archivo pasado como parametro no se encuentra o no se puede abrir.
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
	 * Inicializa el universo con cuerpos colocados de manera aletaria.
	 * @param number_bodies cantidad de cuerpos a crear en el universo.
	 * @param radius radio del universo
	 */
	public void initialize_random(int number_bodies, double radius);
}
