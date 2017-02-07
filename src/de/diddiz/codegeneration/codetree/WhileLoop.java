package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.EvaluationException;
import de.diddiz.codegeneration.exceptions.InfiniteLoopException;
import de.diddiz.utils.Utils;

public class WhileLoop extends Statement
{
	private final Expression condition;
	private final Block block;

	public WhileLoop(Expression condition, Block block) {
		this.condition = condition;
		this.block = block;
	}

	@Override
	public Integer eval(EvaluationContext context) throws EvaluationException {
		int loopCounter = 0; // TODO Replace by monitoring variable changes
		while (condition.eval(context)) {
			final Integer ret = block.eval(context);
			if (ret != null) // Block returned
				return ret;
			loopCounter++;
			if (loopCounter > 10000)
				throw new InfiniteLoopException();
		}
		return null;
	}

	@Override
	public Statement mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateStatement(context);
		// Mutate children
		return new WhileLoop(condition.mutate(mutated, context), block.mutate(mutated, context));
	}

	@Override
	public boolean returns() {
		return false;
	}

	@Override
	public String toCode() {
		return "while (" + condition.toCode() + ") {" + Utils.NEWLINE + block.toCode() + Utils.NEWLINE + "}";
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		children.add(condition);
		condition.gatherChildren(children);
		children.add(block);
		block.gatherChildren(children);
	}
}
