package de.diddiz.codegeneration.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JPanel;

/**
 * Based on <a href="http://stackoverflow.com/a/8693635">http://stackoverflow.com/a/8693635</a>
 */
@SuppressWarnings("serial")
class DrawGraph extends JPanel
{
	private static final int MAX_SCORE = 300;
	private static final int MAX_X = 100;
	private static final int PREF_W = 800;
	private static final int PREF_H = 650;
	private static final int BORDER_GAP = 30;
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
	private static final int GRAPH_POINT_WIDTH = 12;
	private static final int Y_HATCH_CNT = 20;

	private final Graph[] graphs = new Graph[2];

	public DrawGraph() {
		graphs[0] = new Graph(Color.green, new Color(150, 50, 50, 180));
		graphs[1] = new Graph(Color.blue, new Color(150, 50, 50, 180));
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}

	public void setGraphPoints(int idx, Point[] points) {
		graphs[idx].points = points;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// create x and y axes
		g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
		g2.drawLine(BORDER_GAP, getHeight() / 2 - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() / 2 - BORDER_GAP);

		// create hatch marks for y axis.
		for (int i = 0; i < Y_HATCH_CNT; i++) {
			final int x0 = BORDER_GAP;
			final int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
			final int y0 = getHeight() - ((i + 1) * (getHeight() - BORDER_GAP * 2) / Y_HATCH_CNT + BORDER_GAP);
			final int y1 = y0;
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < MAX_X - 1; i++) {
			final int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (MAX_X - 1) + BORDER_GAP;
			final int x1 = x0;
			final int y0 = getHeight() / 2 - BORDER_GAP;
			final int y1 = y0 - GRAPH_POINT_WIDTH;
			g2.drawLine(x0, y0, x1, y1);
		}

		for (final Graph graph : graphs)
			if (graph.points != null) {
				final Point[] graphPoints = scaleGraphPoints(graph.points);

				final Stroke oldStroke = g2.getStroke();
				g2.setColor(graph.graphColor);
				g2.setStroke(GRAPH_STROKE);
				for (int i = 0; i < graphPoints.length - 1; i++) {
					final int x1 = graphPoints[i].x;
					final int y1 = graphPoints[i].y;
					final int x2 = graphPoints[i + 1].x;
					final int y2 = graphPoints[i + 1].y;
					g2.drawLine(x1, y1, x2, y2);
				}

				g2.setStroke(oldStroke);
				g2.setColor(graph.pointColor);
				for (int i = 0; i < graphPoints.length; i++) {
					final int x = graphPoints[i].x - GRAPH_POINT_WIDTH / 2;
					final int y = graphPoints[i].y - GRAPH_POINT_WIDTH / 2;
					final int ovalW = GRAPH_POINT_WIDTH;
					final int ovalH = GRAPH_POINT_WIDTH;
					g2.fillOval(x, y, ovalW, ovalH);
				}
			}
	}

	private Point[] scaleGraphPoints(Point[] inputPoints) {
		final Point[] scaledPoints = new Point[inputPoints.length];

		final double xScale = ((double)getWidth() - 2 * BORDER_GAP) / (MAX_X - 1);
		final double yScale = ((double)getHeight() / 2 - 2 * BORDER_GAP) / (MAX_SCORE - 1);

		for (int i = 0; i < inputPoints.length; i++)
			scaledPoints[i] = new Point(
					(int)(inputPoints[i].x * xScale + BORDER_GAP),
					(int)((MAX_SCORE - inputPoints[i].y) * yScale + BORDER_GAP));

		return scaledPoints;
	}

	private static class Graph
	{
		private final Color graphColor, pointColor;
		private Point[] points;

		public Graph(Color graphColor, Color pointColor) {
			this.graphColor = graphColor;
			this.pointColor = pointColor;
		}
	}
}