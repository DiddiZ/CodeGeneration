package de.diddiz.codegeneration.codetree;

import java.util.Set;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.diddiz.codegeneration.generator.Context;

public abstract class Expression extends CodeElement
{
	public abstract boolean eval();

	@Override
	public abstract Expression mutate(Set<CodeElement> mutated, Context context);

	public static TypeAdapterFactory typeAdapterFactory() {
		return RuntimeTypeAdapterFactory.of(Expression.class)
				.registerSubtype(BinaryBoolOperator.class)
				.registerSubtype(Comparision.class)
				.registerSubtype(UnaryOperator.class);
	}
}