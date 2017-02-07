package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;

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
	public boolean eval() {
		switch (op) {
			case AND:
				return op1.eval() && op2.eval();
			case EQUALS:
				return op1.eval() == op2.eval();
			case EQUALS_NOT:
				return op1.eval() != op2.eval();
			case OR:
				return op1.eval() || op2.eval();
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
