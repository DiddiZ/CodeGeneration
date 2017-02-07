package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.UndeclaredVariableException;

public class Return extends Statement
{
	private final IntValue ret;

	public Return(IntValue ret) {
		this.ret = ret;
	}

	@Override
	public Integer eval(EvaluationContext context) throws UndeclaredVariableException {
		return ret.eval(context);
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
