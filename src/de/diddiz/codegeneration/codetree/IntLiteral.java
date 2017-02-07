package de.diddiz.codegeneration.codetree;

import java.util.List;
import java.util.Set;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.diddiz.codegeneration.codetree.evaluation.EvaluationContext;
import de.diddiz.codegeneration.codetree.generator.Context;
import de.diddiz.codegeneration.codetree.generator.Generator;

public class IntLiteral extends IntValue
{
	private static final LoadingCache<Integer, IntLiteral> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Integer, IntLiteral>() {
		@Override
		public IntLiteral load(Integer i) throws Exception {
			return new IntLiteral(i);
		}
	});

	private final int val;

	private IntLiteral(int val) {
		this.val = val;
	}

	@Override
	public int eval(EvaluationContext context) {
		return val;
	}

	@Override
	public IntValue mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) { // Mutate this
			if (context.random.nextDouble() < 0.75) // Alter value
				return Generator.generateIntLiteral(context);
			return Generator.generateValue(context); // Create completely new value
		}
		// Mutate children, has no children
		return this;
	}

	@Override
	public String toCode() {
		return String.valueOf(val);
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		// No children
	}

	public static IntLiteral create(int val) {
		return CACHE.getUnchecked(val);
	}
}
