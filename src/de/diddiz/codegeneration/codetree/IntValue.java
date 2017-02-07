package de.diddiz.codegeneration.codetree;

import java.util.Set;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.diddiz.codegeneration.generator.Context;

public abstract class IntValue extends CodeElement
{
	public abstract int eval();

	@Override
	public abstract IntValue mutate(Set<CodeElement> mutated, Context context);

	public static TypeAdapterFactory typeAdapterFactory() {
		return RuntimeTypeAdapterFactory.of(IntValue.class)
				.registerSubtype(BinaryIntOperator.class)
				.registerSubtype(IntLiteral.class)
				.registerSubtype(Variable.class);
	}
}
