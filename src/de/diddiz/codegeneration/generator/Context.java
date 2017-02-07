package de.diddiz.codegeneration.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import de.diddiz.codegeneration.codetree.Variable;
import de.diddiz.utils.Utils;

public class Context
{
	private final Map<String, Variable> variables = new HashMap<>();
	private final Set<String> declared = new HashSet<>();
	private final Context parent;
	private final int depth;
	public final Random random;

	private final String[] variableNames = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

	public Context(Context parent) {
		this(parent, parent.random);
	}

	public Context(Random random) {
		this(null, random);
	}

	private Context(Context parent, Random random) {
		this.parent = parent;
		this.random = random;
		depth = parent != null ? parent.depth + 1 : 1;
	}

	public void add(Variable var) {
		variables.put(var.getName(), var);

		Context cur = this;
		while (cur != null) {
			cur.declared.add(var.getName());
			cur = cur.parent;
		}
	}

	public boolean contains(String variableName) {
		if (declared.contains(variableName))
			return true;

		if (parent != null)
			return parent.contains(variableName);

		return false;
	}

	public String freeVariableName() {
		for (final String variableName : variableNames)
			if (!contains(variableName))
				return variableName;

		String variableName;
		do
			variableName = Utils.alphaNum("abcdefghijklmnopqrstuvwxyz", 3);
		while (contains(variableName));
		return variableName;
	}

	public int getDepth() {
		return depth;
	}

	public Collection<Variable> getLocalVariables() {
		return variables.values();
	}

	public List<Variable> getVariables() {
		final List<Variable> vs = new ArrayList<>();

		Context p = this;
		while (p != null) {
			vs.addAll(p.variables.values());
			p = p.parent;
		}

		return vs;
	}
}
