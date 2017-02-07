package de.diddiz.codegeneration.codetree;

import java.util.Set;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.exceptions.EvaluationException;

public abstract class Statement extends CodeElement
{
	public abstract Integer eval(EvaluationContext context) throws EvaluationException;

	@Override
	public abstract Statement mutate(Set<CodeElement> mutated, Context context);

	public abstract boolean returns();

	public static TypeAdapterFactory typeAdapterFactory() {
		return RuntimeTypeAdapterFactory.of(Statement.class)
				.registerSubtype(IfElse.class)
				.registerSubtype(Return.class)
				.registerSubtype(VariableAssignment.class)
				.registerSubtype(WhileLoop.class);
	}
}
