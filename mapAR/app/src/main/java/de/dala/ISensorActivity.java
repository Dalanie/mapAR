package de.dala;

public interface ISensorActivity {
	void sensorStatusChanged(double bearing, double pitch, double rotation, int accuracy);
}
