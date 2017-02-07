package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import com.google.gson.annotations.SerializedName;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.codegeneration.generator.Generator;

public class Variable extends IntValue
{
	@SerializedName("n")
	private final String name;
	@SerializedName("t")
	private final Type type;
	private int value;

	public Variable(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public int eval() {
		return value;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public int getValue() {
		return value;
	}

	@Override
	public IntValue mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateValue(context);
		// Mutate children
		return this; // Doesn't have children
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toCode() {
		return name;
	}

	@Override
	public String toString() {
		return type.toCode() + " " + name;
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		// No children
	}
}
