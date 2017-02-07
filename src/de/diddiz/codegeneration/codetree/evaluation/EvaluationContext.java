package de.diddiz.codegeneration.codetree.evaluation;

import java.util.HashMap;
import java.util.Map;
import de.diddiz.codegeneration.codetree.Variable;
import de.diddiz.codegeneration.exceptions.DuplicateVariableException;
import de.diddiz.codegeneration.exceptions.UndeclaredVariableException;

public class EvaluationContext
{
	private final EvaluationContext parent;

	private final Map<String, Integer> variables = new HashMap<>();

	public EvaluationContext(EvaluationContext parent) {
		this.parent = parent;
	}

	public boolean containsVariable(String name) {
		if (variables.containsKey(name))
			return true;

		if (parent != null)
			return parent.containsVariable(name);

		return false;
	}

	public void declareVarible(Variable var, int val) throws DuplicateVariableException {
		if (containsVariable(var.getName()))
			throw new DuplicateVariableException(var);
		variables.put(var.getName(), val);
	}

	public int getValue(Variable var) throws UndeclaredVariableException {
		final Integer val = variables.get(var.getName());
		if (val != null) // Variable contained in this context
			return val;

		if (parent != null)
			return parent.getValue(var);

		// No parent, variable is not declared
		throw new UndeclaredVariableException();
	}

	public void setValue(Variable var, int val) throws UndeclaredVariableException {
		if (variables.containsKey(var.getName()))
			variables.put(var.getName(), val);

		if (parent != null)
			parent.setValue(var, val);

		// No parent, variable is not declared
		throw new UndeclaredVariableException();
	}

}
