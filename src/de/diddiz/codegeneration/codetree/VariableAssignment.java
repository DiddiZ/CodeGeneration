package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.UndeclaredVariableException;

public class VariableAssignment extends Statement
{
	private final Variable var;
	private final IntValue val;

	public VariableAssignment(Variable var, IntValue val) {
		this.var = var;
		this.val = val;
	}

	@Override
	public Integer eval(EvaluationContext context) throws UndeclaredVariableException {
		context.setValue(var, val.eval(context));
		return null; // Does never return
	}

	public Variable getVariable() {
		return var;
	}

	@Override
	public Statement mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateStatement(context);
		// Mutate children
		return new VariableAssignment(var, val.mutate(mutated, context));
	}

	@Override
	public boolean returns() {
		return false;
	}

	@Override
	public String toCode() {
		return var.getName() + " = " + val.toCode() + ";";
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		children.add(val);
		val.gatherChildren(children);
	}
}
