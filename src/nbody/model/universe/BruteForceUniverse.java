package nbody.model.universe;

public abstract class BruteForceUniverse extends UniverseTemplate {
	
	@Override
	public abstract void update(double dt);

	@Override
	public abstract void stop();
}

