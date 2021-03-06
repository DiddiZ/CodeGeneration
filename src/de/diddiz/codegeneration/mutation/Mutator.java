package de.diddiz.codegeneration.mutation;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import de.diddiz.codegeneration.Agent;
import de.diddiz.codegeneration.AgentOrigin;
import de.diddiz.codegeneration.codetree.CodeElement;
import de.diddiz.codegeneration.codetree.generator.Context;

public final class Mutator
{
	public static Agent cross(Agent a, Agent b, Random random) {
		return new Agent(a.getFunction().cross(b.getFunction(), new Context(random)), AgentOrigin.CROSS);
	}

	public static Agent mutate(Agent a, int rounds, Random random) {
		final List<CodeElement> children = a.getFunction().getChildren();
		final Set<CodeElement> mutated = new HashSet<>(rounds);

		// System.out.println("----------- Mutating:");
		for (int i = 0; i < rounds; i++) {
			final int selected = random.nextInt(children.size());
			mutated.add(children.get(selected));
			// System.out.println(children.get(selected));
		}
		// System.out.println("-----------");
		return new Agent(a.getFunction().mutate(mutated, new Context(random)), AgentOrigin.MUTATION);
	}
}
