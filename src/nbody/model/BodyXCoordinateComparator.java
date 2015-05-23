package nbody.model;

import java.util.Comparator;

/**
 * Compara dos cuerpos con respecto a su posición en el eje X
 * Los desempates se resuelven con el trabajo asociado al cuerpo.
 * @author jlasarte
 *
 */
public class BodyXCoordinateComparator implements Comparator<Body> {

	@Override
	public int compare(Body o1, Body o2) {
		Double rx = o1.rx();
		Double rxo = o1.rx();
		int res = rx.compareTo(rxo);
		if (res != 0)
			//si estan en el mismo lugar ordenamos por trabajo
			return res;

		Integer w = o1.work();
		Integer wo = o2.work();
		return w.compareTo(wo);
	}

}
