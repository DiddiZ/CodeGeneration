package de.diddiz.codegeneration.codetree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Codetree
{
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapterFactory(Statement.typeAdapterFactory())
			.registerTypeAdapterFactory(IntValue.typeAdapterFactory())
			.registerTypeAdapterFactory(Expression.typeAdapterFactory())
			.create();
}
