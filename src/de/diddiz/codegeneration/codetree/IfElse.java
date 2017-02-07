package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.EvaluationException;
import de.diddiz.utils.Utils;

public class IfElse extends Statement
{
	private final Expression condition;
	private final Block ifBlock, elseBlock;

	public IfElse(Expression condition, Block ifBlock) {
		this(condition, ifBlock, null);
	}

	public IfElse(Expression condition, Block ifBlock, Block elseBlock) {
		this.condition = condition;
		this.ifBlock = ifBlock;
		this.elseBlock = elseBlock;
	}

	@Override
	public Integer eval(EvaluationContext context) throws EvaluationException {
		if (condition.eval(context))
			return ifBlock.eval(context);
		return elseBlock != null ? elseBlock.eval(context) : null;
	}

	@Override
	public Statement mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateStatement(context);
		// Mutate children
		return new IfElse(condition.mutate(mutated, context), ifBlock.mutate(mutated, context), elseBlock != null ? elseBlock.mutate(mutated, context) : null);
	}

	@Override
	public boolean returns() {
		return ifBlock.returns() && elseBlock != null && elseBlock.returns();
	}

	@Override
	public String toCode() {
		String ret = "if (" + condition.toCode() + ") {" + Utils.NEWLINE + ifBlock.toCode() + Utils.NEWLINE + "}";
		if (elseBlock != null)
			ret += " else {" + Utils.NEWLINE + elseBlock.toCode() + Utils.NEWLINE + "}";
		return ret;
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		children.add(this);
		children.add(condition);
		condition.gatherChildren(children);
		children.add(ifBlock);
		ifBlock.gatherChildren(children);
		if (elseBlock != null) {
			children.add(elseBlock);
			elseBlock.gatherChildren(children);
		}
	}
}
