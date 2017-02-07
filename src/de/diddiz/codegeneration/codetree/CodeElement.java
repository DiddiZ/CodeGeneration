package de.diddiz.codegeneration.codetree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.generator.Context;

public abstract class CodeElement
{
	public List<CodeElement> getChildren() {
		final List<CodeElement> children = new ArrayList<>();
		gatherChildren(children);

		return children;
	}

	public abstract CodeElement mutate(Set<CodeElement> mutated, Context context);

	public abstract String toCode();

	protected abstract void gatherChildren(List<CodeElement> children);
}
