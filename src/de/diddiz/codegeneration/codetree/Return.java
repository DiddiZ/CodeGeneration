package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;

public class Return extends Statement
{
	private final IntValue ret;

	public Return(IntValue ret) {
		this.ret = ret;
	}

	@Override
	public Integer eval() {
		return ret.eval();
	}

	@Override
	public Return mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateReturn(context);
		// Mutate children
		return new Return(ret.mutate(mutated, context));
	}

	@Override
	public boolean returns() {
		return true;
	}

	@Override
	public String toCode() {
		return "return " + ret.toCode() + ";";
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		children.add(ret);
		ret.gatherChildren(children);
	}
}
