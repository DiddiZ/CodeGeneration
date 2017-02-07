package de.diddiz.codegeneration.visualization;

import java.awt.HeadlessException;
import javax.swing.JFrame;
import com.google.common.eventbus.Subscribe;
import de.diddiz.codegeneration.Agent;
import de.diddiz.codegeneration.events.GenerationCompleteEvent;

public class GraphWindow extends JFrame
{
	private final DrawGraph mainPanel;

	public GraphWindow() throws HeadlessException {
		super("DrawGraph");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel = new DrawGraph();
		getContentPane().add(mainPanel);
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	@Subscribe
	public void onGenerationCompleted(GenerationCompleteEvent e) {
		updateGraph(e.getBestAgent());
	}

	public void updateGraph(Agent bestAgent) {
		mainPanel.setGraphPoints(0, bestAgent.getFitness().expected.toPoints());
		mainPanel.setGraphPoints(1, bestAgent.getFitness().actual.toPoints());
		repaint();
	}
}
