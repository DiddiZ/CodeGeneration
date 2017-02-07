package de.diddiz.codegeneration.codetree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Preconditions;
import de.diddiz.codegeneration.exceptions.InfiniteLoopException;
import de.diddiz.codegeneration.generator.Context;
import de.diddiz.utils.Utils;

public class Block extends CodeElement
{
	private final Statement[] statements;

	private final Variable[] declaredVariables;

	public Block(Collection<Statement> statements, Collection<Variable> declaredVariables) {
		this(statements.toArray(new Statement[statements.size()]), declaredVariables.toArray(new Variable[declaredVariables.size()]));
	}

	public Block(Statement[] statements, Variable[] declaredVariables) {
		this.statements = statements;
		Preconditions.checkNotNull(declaredVariables);
		this.declaredVariables = declaredVariables;
	}

	public Block cross(Block other, Context context) {
		final List<Statement> combi = new ArrayList<>();

		final int replaced = context.random.nextInt(statements.length),
				copied = context.random.nextInt(other.statements.length);
		final int readStart = context.random.nextInt(other.statements.length - copied);
		final int writeStart = context.random.nextInt(statements.length - replaced);

		for (int i = 0; i < writeStart; i++)
			combi.add(statements[i]);
		for (int i = readStart; i < readStart + copied; i++)
			combi.add(other.statements[i]);
		for (int i = writeStart + replaced; i < statements.length; i++)
			combi.add(statements[i]);

		final Map<String, Variable> variables = new HashMap<>();
		for (final Statement st : combi)
			if (st instanceof VariableAssignment) {
				final Variable var = ((VariableAssignment)st).getVariable();
				if (!context.contains(var.getName()))
					variables.put(var.getName(), var);
			}

		return new Block(combi.toArray(new Statement[combi.size()]),
				variables.values().toArray(new Variable[variables.size()]));
	}

	public Integer eval() throws InfiniteLoopException {
		for (final Variable var : declaredVariables)
			var.setValue(0);
		for (final Statement st : statements) {
			final Integer ret = st.eval();
			if (ret != null)
				return ret;
		}
		return null;
	}

	@Override
	public Block mutate(Set<CodeElement> mutated, Context context) {
		if (mutated.contains(this)) // Mutate this, can't mutate
			return this;
		// Mutate children
		final List<Statement> cloneStatements = new ArrayList<>();
		context = new Context(context);
		for (final Variable var : declaredVariables)
			context.add(var);
		for (int i = 0; i < statements.length; i++)
			cloneStatements.add(statements[i].mutate(mutated, context));

		return new Block(cloneStatements, context.getLocalVariables());
	}

	public boolean returns() {
		return statements.length > 0 && statements[statements.length - 1].returns();
	}

	@Override
	public String toCode() {
		String ret = "";

		// Declare and initialize all variables at block start
		if (declaredVariables != null && declaredVariables.length > 0) {
			ret += declaredVariables[0].getType().toCode() + " " + declaredVariables[0].getName() + " = 0"; // TODO group by type
			for (int i = 1; i < declaredVariables.length; i++)
				ret += ", " + declaredVariables[i].getName() + " = 0";
			ret += ";";
		}

		if (statements != null)
			for (final Statement statement : statements) {
				if (ret.length() > 0)
					ret += Utils.NEWLINE;
				ret += statement.toCode();
			}

		return ret;
	}

	@Override
	protected void gatherChildren(List<CodeElement> children) {
		for (final Statement st : statements) {
			children.add(st);
			st.gatherChildren(children);
		}
	}
}
