package de.diddiz.codegeneration.events;

import java.util.List;
import de.diddiz.codegeneration.Agent;

public class GeneratorStartEvent
{
	private final List<Agent> agents;
	private int generation = 0;

	public GeneratorStartEvent(List<Agent> agents) {
		this.agents = agents;
	}

	public void addAgent(Agent a) {
		agents.add(a);
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}
}
