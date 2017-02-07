package de.diddiz.codegeneration.agents;

import java.util.Collections;
import com.google.common.collect.Lists;
import de.diddiz.codegeneration.codetree.BinaryBoolOperator;
import de.diddiz.codegeneration.codetree.BinaryBoolOperator.BoolOperators;
import de.diddiz.codegeneration.codetree.BinaryIntOperator;
import de.diddiz.codegeneration.codetree.BinaryIntOperator.IntOperators;
import de.diddiz.codegeneration.codetree.Block;
import de.diddiz.codegeneration.codetree.Comparision;
import de.diddiz.codegeneration.codetree.Comparision.ComparisionOperators;
import de.diddiz.codegeneration.codetree.Function;
import de.diddiz.codegeneration.codetree.IfElse;
import de.diddiz.codegeneration.codetree.IntLiteral;
import de.diddiz.codegeneration.codetree.Return;
import de.diddiz.codegeneration.codetree.Type;
import de.diddiz.codegeneration.codetree.Variable;

/**
 * Simple cosine approximation using 4 lines.
 */
public class AgentSmith
{
	public static Function create() {
		final Variable a = Variable.create("a");

		return new Function("cos", Type.Int, new Variable[]{a}, new Block(Lists.newArrayList(
				new IfElse(
						new BinaryBoolOperator(BoolOperators.OR,
								new Comparision(ComparisionOperators.LESSER, a, IntLiteral.create(10)),
								new Comparision(ComparisionOperators.GREATER_EQUAL, a, IntLiteral.create(90))),
						new Block(
								Lists.newArrayList(new Return(IntLiteral.create(190))),
								Collections.emptyList())),
				new IfElse(
						new Comparision(ComparisionOperators.LESSER, a, IntLiteral.create(40)),
						new Block(
								Lists.newArrayList(
										new Return(
												new BinaryIntOperator(IntOperators.MINUS,
														IntLiteral.create(325),
														new BinaryIntOperator(IntOperators.MULTIPLY,
																IntLiteral.create(13),
																a)))),
								Collections.emptyList())),
				new IfElse(
						new Comparision(ComparisionOperators.LESSER, a, IntLiteral.create(60)),
						new Block(
								Lists.newArrayList(
										new Return(IntLiteral.create(-190))),
								Collections.emptyList())),
				new Return(
						new BinaryIntOperator(IntOperators.MINUS,
								new BinaryIntOperator(IntOperators.MULTIPLY,
										IntLiteral.create(13),
										a),
								IntLiteral.create(975)))),
				Collections.emptyList()));
	}
}
