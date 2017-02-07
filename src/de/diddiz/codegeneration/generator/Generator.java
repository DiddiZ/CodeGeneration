package de.diddiz.codegeneration.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import de.diddiz.codegeneration.codetree.BinaryBoolOperator;
import de.diddiz.codegeneration.codetree.BinaryBoolOperator.BoolOperators;
import de.diddiz.codegeneration.codetree.BinaryIntOperator;
import de.diddiz.codegeneration.codetree.BinaryIntOperator.IntOperators;
import de.diddiz.codegeneration.codetree.Block;
import de.diddiz.codegeneration.codetree.Comparision;
import de.diddiz.codegeneration.codetree.Comparision.ComparisionOperators;
import de.diddiz.codegeneration.codetree.Expression;
import de.diddiz.codegeneration.codetree.Function;
import de.diddiz.codegeneration.codetree.IfElse;
import de.diddiz.codegeneration.codetree.IntLiteral;
import de.diddiz.codegeneration.codetree.IntValue;
import de.diddiz.codegeneration.codetree.Return;
import de.diddiz.codegeneration.codetree.Statement;
import de.diddiz.codegeneration.codetree.Type;
import de.diddiz.codegeneration.codetree.UnaryOperator;
import de.diddiz.codegeneration.codetree.Variable;
import de.diddiz.codegeneration.codetree.VariableAssignment;
import de.diddiz.codegeneration.codetree.WhileLoop;

public final class Generator
{
	public static VariableAssignment generateAssignment(Context context) {
		final IntValue val = generateValue(context); // Generate val before createing var

		final Variable var;

		if (context.random.nextDouble() < 0.5) { // new Variable
			var = new Variable(context.freeVariableName(), Type.Int);

			context.add(var);
		} else
			var = randomVariable(context); // Choose random variable

		return new VariableAssignment(var, val);
	}

	public static Block generateBlock(Context parent, int length) {
		if (parent.getDepth() > 100)// Prevent nesting too deep
			return new Block(new Statement[]{}, new Variable[]{});

		final Context context = new Context(parent);

		final List<Statement> statements = new ArrayList<>();
		final int numOfStatements = numberOfStatementsPerBlock(length, context.random);
		for (int i = 0; i < numOfStatements; i++) {
			final Statement st = generateStatement(context);
			statements.add(st);
			if (st.returns())
				break;
		}

		if (context.getDepth() == 3 && (statements.isEmpty() || !statements.get(statements.size() - 1).returns())) // Add teturn at end if neccessary
			statements.add(generateReturn(context));

		return new Block(statements, context.getLocalVariables());
	}

	public static Expression generateExpression(Context context) {
		final double rnd = context.random.nextDouble();

		if (rnd < .5)
			return new Comparision(ComparisionOperators.getRandom(context.random), generateValue(context), generateValue(context));
		else if (rnd < .6)// 10% negation
			return new UnaryOperator("!", generateExpression(context));
		else { // 40% complex logic
			final BoolOperators[] ops = BoolOperators.values();
			return new BinaryBoolOperator(ops[context.random.nextInt(ops.length)], generateExpression(context), generateExpression(context));
		}
	}

	public static Function generateFunction(String name, Type type, Context parent, Variable... parameters) {
		final Context context = new Context(parent);
		for (final Variable v : parameters)
			context.add(v);

		return new Function(name, type, parameters, generateBlock(context, 10));
	}

	public static IntLiteral generateIntLiteral(Context context) {
		if (context.random.nextBoolean())
			return IntLiteral.create(1);
		return IntLiteral.create(context.random.nextInt(21 - 10));
	}

	public static Return generateReturn(Context context) {
		return new Return(randomVariable(context));
	}

	public static Statement generateStatement(Context context) {
		final double rnd = context.random.nextDouble();

		if (rnd < .1)// new while
			return new WhileLoop(generateExpression(context), generateBlock(context, 5));
		if (rnd < .2) // new if
			return new IfElse(generateExpression(context), generateBlock(context, 5), context.random.nextBoolean() ? generateBlock(context, 5) : null);
		else if (rnd < .3) // 10% Return
			return generateReturn(context);
		else // new assignment
			return generateAssignment(context);
	}

	public static IntValue generateValue(Context context) {
		final double rnd = context.random.nextDouble();

		if (rnd < .2)
			return generateIntLiteral(context);
		else if (rnd < .5) // 30% variable
			return randomVariable(context);
		else if (rnd < .9) { // 10% Addition
			final IntOperators[] ops = {IntOperators.PLUS, IntOperators.MINUS, IntOperators.MULTIPLY};
			return new BinaryIntOperator(ops[context.random.nextInt(ops.length)], generateValue(context), generateValue(context));
		} else // 10% Inkrement
			return new BinaryIntOperator(IntOperators.PLUS, randomVariable(context), IntLiteral.create(1));
	}

	private static int numberOfStatementsPerBlock(int length, Random random) {
		return random.nextInt(length) + 1;
	}

	private static Variable randomVariable(Context context) {
		final List<Variable> vars = new ArrayList<>(context.getVariables());
		return vars.get((int)(vars.size() * context.random.nextDouble()));
	}
}
