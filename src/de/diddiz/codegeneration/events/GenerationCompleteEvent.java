package de.diddiz.codegeneration.events;

import java.util.List;
import de.diddiz.codegeneration.Agent;

public class GenerationCompleteEvent
{
	private final int generation;
	private final int stuck;
	private final List<Agent> agents;

	public GenerationCompleteEvent(int generation, int stuck, List<Agent> agents) {
		this.generation = generation;
		this.stuck = stuck;
		this.agents = agents;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public Agent getBestAgent() {
		return agents.get(0);
	}

	public int getGeneration() {
		return generation;
	}

	public int getStuck() {
		return stuck;
	}

	public boolean isStuck() {
		return stuck > 0;
	}
}
