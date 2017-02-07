package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.UndeclaredVariableException;

public class UnaryOperator extends Expression
{
	private final String op;
	private final Expression val;

	public UnaryOperator(String op, Expression val) {
		this.op = op;
		this.val = val;
	}

	@Override
	public boolean eval(EvaluationContext context) throws UndeclaredVariableException {
		switch (op) {
			case "!":
				return !val.eval(context);
			default:
				throw new AssertionError();
		}
	}

	@Override
	public Expression mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateExpression(context);
		// Mutate children
		return new UnaryOperator(op, val.mutate(mutated, context));
	}

	@Override
	public String toCode() {
		return "(" + op + " " + val.toCode() + ")";
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		children.add(val);
		val.gatherChildren(children);
	}

}
