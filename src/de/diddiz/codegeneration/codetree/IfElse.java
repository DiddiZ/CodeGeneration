package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.exceptions.InfiniteLoopException;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;
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
	public Integer eval() throws InfiniteLoopException {
		if (condition.eval())
			return ifBlock.eval();
		return elseBlock != null ? elseBlock.eval() : null;
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
