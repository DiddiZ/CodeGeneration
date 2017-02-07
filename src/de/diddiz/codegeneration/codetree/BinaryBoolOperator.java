package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.UndeclaredVariableException;

public class BinaryBoolOperator extends Expression
{
	private final BoolOperators op;
	private final Expression op1, op2;

	public BinaryBoolOperator(BoolOperators op, Expression op1, Expression op2) {
		this.op = op;
		this.op1 = op1;
		this.op2 = op2;
	}

	@Override
	public boolean eval(EvaluationContext context) throws UndeclaredVariableException {
		switch (op) {
			case AND:
				return op1.eval(context) && op2.eval(context);
			case EQUALS:
				return op1.eval(context) == op2.eval(context);
			case EQUALS_NOT:
				return op1.eval(context) != op2.eval(context);
			case OR:
				return op1.eval(context) || op2.eval(context);
			default:
				throw new AssertionError();
		}
	}

	@Override
	public Expression mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateExpression(context);
		// Mutate children
		return new BinaryBoolOperator(op, op1.mutate(mutated, context), op2.mutate(mutated, context));
	}

	@Override
	public String toCode() {
		return "(" + op1.toCode() + " " + op.sign + " " + op2.toCode() + ")";
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		children.add(op1);
		op1.gatherChildren(children);
		children.add(op2);
		op2.gatherChildren(children);
	}

	public enum BoolOperators {
		AND("&&"), OR("||"), EQUALS("=="), EQUALS_NOT("!=");
		private final String sign;

		private BoolOperators(String sign) {
			this.sign = sign;
		}
	}
}
