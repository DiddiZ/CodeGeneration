package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;

public class BinaryIntOperator extends IntValue
{
	private final IntOperators op;
	private final IntValue op1, op2;

	public BinaryIntOperator(IntOperators op, IntValue op1, IntValue op2) {
		this.op = op;
		this.op1 = op1;
		this.op2 = op2;
	}

	@Override
	public int eval() {
		switch (op) {
			case PLUS:
				return op1.eval() + op2.eval();
			case MINUS:
				return op1.eval() - op2.eval();
			case MULTIPLY:
				return op1.eval() * op2.eval();
			default:
				throw new AssertionError();
		}
	}

	@Override
	public IntValue mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateValue(context);
		// Mutate children
		return new BinaryIntOperator(op, op1.mutate(mutated, context), op2.mutate(mutated, context));
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

	public enum IntOperators {
		PLUS("+"), MINUS("-"), MULTIPLY("*");
		private final String sign;

		private IntOperators(String sign) {
			this.sign = sign;
		}
	}
}
