package nbody.console;

import java.io.IOException;

public class NBodyTester {
	
	public static void main(String[] args) throws IOException {
		String[] arg = {"-i", "50", "-dt", "0.1", "-a", "barnespb", "-f", "saturnrings.txt", "-t", "1"};
		int[] proc = {4};
			for (int j: proc) {
				arg[9] = Integer.toString(j);
				NBody.main(arg);
			}
	}
}
