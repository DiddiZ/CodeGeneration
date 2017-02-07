package de.diddiz.codegeneration;

import java.awt.Point;

public class DataPoints
{
	final int[] dataPoints;

	public DataPoints(int[] dataPoints) {
		this.dataPoints = dataPoints;
	}

	public int get(int i) {
		return dataPoints[i];
	}

	public Point[] toPoints() {
		final Point[] points = new Point[dataPoints.length];
		for (int i = 0; i < dataPoints.length; i++)
			points[i] = new Point(i, dataPoints[i]);
		return points;
	}
}
