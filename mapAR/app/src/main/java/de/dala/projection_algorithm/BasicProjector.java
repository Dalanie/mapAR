package de.dala.projection_algorithm;

public abstract class BasicProjector implements IWorldToScreenProjector {
	@Override
	public String toString() {
		return getProjectionName();
	}
}
