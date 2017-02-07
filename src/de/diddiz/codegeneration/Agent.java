package de.diddiz.codegeneration;

import java.lang.reflect.Method;
import java.util.Random;
import de.diddiz.codegeneration.codetree.Codetree;
import de.diddiz.codegeneration.codetree.Function;
import de.diddiz.codegeneration.codetree.Type;
import de.diddiz.codegeneration.codetree.Variable;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;

public class Agent
{
	private static int nextId = 1;

	private final int id;
	private final Function f;

	private final AgentOrigin origin;

	private Fitness fitness;
	int complexity = -1; // Number of elements in code tree
	Method m;

	public Agent(Function f, AgentOrigin origin) {
		id = nextId();
		this.f = f;
		this.origin = origin;
	}

	public String getDisplayName() {
		final String name = getName();
		// if (parents.length == 1)
		// name += "<-" + parents[0].getDisplayName();
		// else if (parents.length > 1) {
		// name += "<-(" + parents[0].getDisplayName();
		//
		// for (int i = 1; i < parents.length; i++)
		// name += "+" + parents[1].getDisplayName();
		// name += ")";
		// }
		return name;
	}

	public Fitness getFitness() {
		return fitness;
	}

	public Function getFunction() {
		return f;
	}

	public int getId() {
		return id;
	}

	public Method getMethod() {
		return m;
	}

	public String getName() {
		return String.format("Agent_%012d", id);
	}

	public AgentOrigin getOrigin() {
		return origin;
	}

	void setFitness(Fitness fitness) {
		this.fitness = fitness;
	}

	// /**
	// * @return whether two agents have a common predecessor
	// */
	// public static boolean related(Agent a1, Agent a2) {
	// while (a1 != null) {
	// Agent tmp = a2;
	// while (tmp != null) {
	// if (tmp == a1)
	// return true;
	// tmp = tmp.parent;
	// }
	// a1 = a1.parent;
	// }
	// return false;
	// }

	public static Agent createRandomAgent() {
		return new Agent(
				Generator.generateFunction(
						"func",
						Type.Int,
						new Context(new Random()),
						new Variable[]{new Variable("a", Type.Int)}),
				AgentOrigin.RANDOM);
	}

	public static Agent loadAgent(String json, AgentOrigin origin) {
		return new Agent(Codetree.GSON.fromJson(json, Function.class), origin);
	}

	private static synchronized int nextId() {
		return nextId++;
	}
}
