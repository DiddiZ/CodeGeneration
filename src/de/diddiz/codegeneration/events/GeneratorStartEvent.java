package de.diddiz.codegeneration.events;

import java.util.ArrayList;
import java.util.List;
import de.diddiz.codegeneration.Agent;
import de.diddiz.utils.factories.Factory;

public class GeneratorStartEvent
{
	private final List<Factory<Agent>> agentFactories = new ArrayList<>();
	private int generation = 0;

	public GeneratorStartEvent() {}

	public void addAgent(Factory<Agent> agentFactory) {
		agentFactories.add(agentFactory);
	}

	public List<Factory<Agent>> getAgentFactories() {
		return agentFactories;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}
}
