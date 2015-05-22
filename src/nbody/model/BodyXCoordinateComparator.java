package nbody.model;

import java.util.Comparator;


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
