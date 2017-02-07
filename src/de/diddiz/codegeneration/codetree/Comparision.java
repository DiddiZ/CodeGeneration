package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Random;
import java.util.Set;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;

public class Comparision extends Expression
{
	private final ComparisionOperators op;
	private final IntValue op1, op2;

	public Comparision(ComparisionOperators op, IntValue op1, IntValue op2) {
		this.op = op;
		this.op1 = op1;
		this.op2 = op2;
	}

	@Override
	public boolean eval() {
		switch (op) {
			case EQUAL:
				return op1.eval() == op2.eval();
			case GREATER:
				return op1.eval() > op2.eval();
			case GREATER_EQUAL:
				return op1.eval() >= op2.eval();
			case LESER_EQUAL:
				return op1.eval() <= op2.eval();
			case LESSER:
				return op1.eval() < op2.eval();
			case NOT_EQUAL:
				return op1.eval() != op2.eval();
			default:
				throw new AssertionError();
		}
	}

	@Override
	public Expression mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateExpression(context);
		// Mutate children
		return new Comparision(op, op1.mutate(mutated, context), op2.mutate(mutated, context));
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

	public enum ComparisionOperators {
		LESSER("<"), GREATER(">"), EQUAL("=="), NOT_EQUAL("!="), LESER_EQUAL("<="), GREATER_EQUAL(">=");
		private final String sign;

		private ComparisionOperators(String sign) {
			this.sign = sign;
		}

		public static ComparisionOperators getRandom(Random random) {
			return values()[random.nextInt(values().length)];
		}
	}
}
