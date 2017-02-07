package de.diddiz.codegeneration.exceptions;

import de.diddiz.codegeneration.codetree.Variable;

public class DuplicateVariableException extends EvaluationException
{
	private final Variable var;

	public DuplicateVariableException(Variable var) {
		this.var = var;
	}

	public Variable getVar() {
		return var;
	}
}
