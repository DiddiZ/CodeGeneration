package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import de.diddiz.codegeneration.exceptions.EvaluationException;
import de.diddiz.codegeneration.exceptions.NoReturnException;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;
import de.diddiz.utils.Utils;

public class Function extends CodeElement
{
	private final String name;
	private final Type type;
	private final Variable[] parameters;
	private final Block block;

	public Function(String name, Type type, Variable[] parameters, Block block) {
		this.name = name;
		this.type = type;
		this.parameters = parameters;
		this.block = block;
	}

	public Function cross(Function other, Context context) {
		return new Function(name, type, parameters, block.cross(other.block, context));
	}

	public int eval(int... values) throws EvaluationException {
		for (int i = 0; i < values.length; i++)
			parameters[i].setValue(values[i]);

		final Integer ret = block.eval();
		if (ret == null)
			throw new NoReturnException();
		return ret;
	}

	public Block getBlock() {
		return block;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	@Override
	public Function mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateFunction(name, type, context, parameters);

		context = new Context(context);
		for (final Variable v : parameters)
			context.add(v);

		// Mutate children
		return new Function(name, type, parameters, block.mutate(mutated, context));
	}

	@Override
	public String toCode() {
		String ret = "public static " + type.toCode() + " " + name + "(";
		if (parameters.length > 0) {
			ret += parameters[0].getType().toCode() + " " + parameters[0].getName();
			for (int i = 1; i < parameters.length; i++)
				ret += ", " + parameters[i].getType() + " " + parameters[i].getName();
		}
		return ret + ") {" + Utils.NEWLINE + block.toCode() + Utils.NEWLINE + "}";
	}

	public String toJson() {
		return Codetree.GSON.toJson(this);
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		children.add(block);
		block.gatherChildren(children);
	}
}
