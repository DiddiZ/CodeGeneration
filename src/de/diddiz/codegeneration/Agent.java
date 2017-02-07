package de.diddiz.codegeneration;

import java.lang.reflect.Method;
import java.util.Random;
import de.diddiz.codegeneration.codetree.Codetree;
import de.diddiz.codegeneration.codetree.Function;
import de.diddiz.codegeneration.codetree.Type;
import de.diddiz.codegeneration.codetree.Variable;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;

public class Agent
{
	private static int nextId = 1;

	private final int id;
	private final Function f;

	private final AgentOrigin origin;

	private Fitness fitness;
	Method m;

	public Agent(Function f, AgentOrigin origin) {
		id = nextId();
		this.f = f;
		this.origin = origin;
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

	public static Agent createRandomAgent() {
		return new Agent(
				Generator.generateFunction(
						"func",
						Type.Int,
						new Context(new Random()),
						new Variable[]{Variable.create("a")}),
				AgentOrigin.RANDOM);
	}

	public static Agent loadAgent(String json, AgentOrigin origin) {
		return new Agent(Codetree.GSON.fromJson(json, Function.class), origin);
	}

	private static synchronized int nextId() {
		return nextId++;
	}
}
