package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Random;
import java.util.Set;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.UndeclaredVariableException;

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
	public boolean eval(EvaluationContext context) throws UndeclaredVariableException {
		switch (op) {
			case EQUAL:
				return op1.eval(context) == op2.eval(context);
			case GREATER:
				return op1.eval(context) > op2.eval(context);
			case GREATER_EQUAL:
				return op1.eval(context) >= op2.eval(context);
			case LESER_EQUAL:
				return op1.eval(context) <= op2.eval(context);
			case LESSER:
				return op1.eval(context) < op2.eval(context);
			case NOT_EQUAL:
				return op1.eval(context) != op2.eval(context);
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
