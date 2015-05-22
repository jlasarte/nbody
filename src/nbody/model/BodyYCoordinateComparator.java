package nbody.model;

import java.util.Comparator;


public class BodyYCoordinateComparator implements Comparator<Body> {

	@Override
	public int compare(Body o1, Body o2) {
		Double ry = o1.ry();
		Double ryo = o1.ry();
		int res = ry.compareTo(ryo);
		if (res != 0)
			//si estan en el mismo lugar ordenamos por trabajo
			return res;

		Integer w = o1.work();
		Integer wo = o2.work();
		return w.compareTo(wo);
	}
	
}