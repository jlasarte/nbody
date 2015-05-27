package nbody.console;

public class NBodyTester {
	
	public static void main(String[] args) {
		String[] arg = {"-i", "50", "-dt", "0.1", "-a", "brutep", "-n", "25000", "-t", "1"};
		int[] proc = {1,2,4,8,16};
		int[] n = {30000};
		for (int i : n) {
			for (int j: proc) {
				arg[9] = Integer.toString(j);
				arg[7] = Integer.toString(i);
				NBody.main(arg);
			}
		}
	}
}