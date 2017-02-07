package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.annotations.SerializedName;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;
import de.diddiz.codegeneration.exceptions.UndeclaredVariableException;

public class Variable extends IntValue
{
	private static final LoadingCache<String, Variable> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<String, Variable>() {
		@Override
		public Variable load(String name) throws Exception {
			return new Variable(name, Type.Int);
		}
	});

	@SerializedName("n")
	private final String name;
	@SerializedName("t")
	private final Type type;

	private Variable(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public int eval(EvaluationContext context) throws UndeclaredVariableException {
		return context.getValue(this);
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	@Override
	public IntValue mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this
			return Generator.generateValue(context);
		// Mutate children
		return this; // Doesn't have children
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

	public static Variable create(String name) {
		return CACHE.getUnchecked(name);
	}
}
