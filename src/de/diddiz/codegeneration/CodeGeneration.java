package de.diddiz.codegeneration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import de.diddiz.codegeneration.codetree.Function;
import de.diddiz.codegeneration.compiler.InMemoryCompiler;
import de.diddiz.codegeneration.compiler.InMemoryCompiler.IMCSourceCode;
import de.diddiz.codegeneration.events.GenerationCompleteEvent;
import de.diddiz.codegeneration.events.GeneratorStartEvent;
import de.diddiz.codegeneration.exceptions.EvaluationException;
import de.diddiz.codegeneration.mutation.Mutator;
import de.diddiz.codegeneration.visualization.GenerationStats;
import de.diddiz.codegeneration.visualization.GraphWindow;
import de.diddiz.codegeneration.webinterface.WebServer;
import de.diddiz.utils.Utils;
import de.diddiz.utils.factories.Factory;
import de.diddiz.utils.logging.Log;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class CodeGeneration
{
	private static final boolean DEBUG = false;

	private static final InMemoryCompiler compiler = new InMemoryCompiler();

	private final LinkedList<Agent> agents = new LinkedList<>(); // Agent pool

	private final DataPoints expected;

	private final ExecutorService executorService = Executors.newWorkStealingPool();
	private final EventBus eventBus = new EventBus();

	public CodeGeneration(ExactFunction f) {
		expected = f.exactValues();
	}

	public void generate(int popSize, Random random) throws InterruptedException, ExecutionException {
		// Fire start event to allow listeners to add agents
		final GeneratorStartEvent generatorStartEvent = new GeneratorStartEvent();
		eventBus.post(generatorStartEvent); // Fire event
		int gen = generatorStartEvent.getGeneration();

		{// Initial population
			final List<Callable<Agent>> tasks = new ArrayList<>();

			// Calc fitness of external agents
			for (final Factory<Agent> agentFactory : generatorStartEvent.getAgentFactories())
				tasks.add(createAgentTask(agentFactory));

			// Fill pool with random agents
			for (int i = tasks.size(); i < popSize; i++)
				tasks.add(createRandomAgentTask(new Random(random.nextLong())));
			// Create agents
			createAgents(tasks);
		}

		double bestScore = 0;
		Agent lastBest = null;
		int stuck = 0;
		for (; bestScore < 100; gen++) {
			System.out.println("Generation " + gen + " (" + agents.size() + " agents)");
			Collections.sort(agents, (a1, a2) -> a2.getFitness().compareTo(a1.getFitness()));

			final Agent best = agents.get(0);
			// Check if we're stuck
			if (best == lastBest)
				stuck++;
			else
				stuck = 0;
			lastBest = best;

			eventBus.post(new GenerationCompleteEvent(gen, stuck, Collections.unmodifiableList(agents)));

			bestScore = best.getFitness().score;
			final double median = agents.get(popSize / 2).getFitness().score;
			final double mean = agents.stream().mapToDouble(a -> a.getFitness().score).average().getAsDouble();
			System.out.println("Best: " + best.getName() + " with " + best.getFitness().score + " Avg: " + mean + " Median: " + median);
			// System.out.println(best.getFunction().toCode());

			System.out.println("Stuck " + stuck + (stuck >= 50 && stuck % 50 == 0 ? " Culling Time!" : ""));

			// Cull
			agents.removeIf(a -> !a.getFitness().success); // Remove unsuccessful

			final double cullRate = 0.1;
			while (agents.size() > popSize * (1 - cullRate))
				agents.removeLast();
			if (stuck >= 50 && stuck % 50 == 0)
				agents.removeIf(a -> a != best && random.nextBoolean());

			{// Repop
				final List<Callable<Agent>> tasks = new ArrayList<>();
				for (int i = agents.size(); i < popSize; i++) {
					final double rnd = random.nextDouble();

					if (rnd < 0.25) // Generate new
						tasks.add(createRandomAgentTask(new Random(random.nextLong())));
					else if (rnd < 0.75) { // Mutate
						final double selected = random.nextDouble();
						double sum = 0;
						final double n = agents.size();

						for (int k = 0; k < n; k++) {
							// sum += (n - k) * 2 / (n * n + n);
							sum += 6 * (n - k) * (n - k) / n / (n + 1) / (2 * n + 1);
							if (sum + 1E-8 >= selected) { // Found selected
								tasks.add(mutateAgentTask(
										agents.get(k),
										random.nextInt(5 + stuck / 10) + 1,
										new Random(random.nextLong())));
								break;
							}
						}
					} else // Cross
						tasks.add(crossAgentsTask(agents.get(random.nextInt(agents.size() / 5)),
								agents.get(random.nextInt(agents.size())),
								new Random(random.nextLong())));

				}
				// Create agents
				createAgents(tasks);
			}
		}

		System.out.println("Done!");
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	private void createAgents(List<Callable<Agent>> tasks) throws InterruptedException, ExecutionException {
		for (final Future<Agent> futureAgent : executorService.invokeAll(tasks)) {
			final Agent a = futureAgent.get();
			if (a != null)
				agents.add(a);
		}
	}

	private Callable<Agent> createAgentTask(Factory<Agent> agentFactory) {
		return () -> {
			final Agent a = agentFactory.create();
			if (a != null)
				a.setFitness(testFitness2(a, expected));
			return a;
		};
	}

	private Callable<Agent> createRandomAgentTask(Random random) {
		return () -> {
			final Agent a = Agent.createRandomAgent(random);
			a.setFitness(testFitness2(a, expected));
			return a;
		};
	}

	private Callable<Agent> crossAgentsTask(Agent parent1, Agent parent2, Random random) {
		return () -> {
			final Agent a = Mutator.cross(parent1, parent2, random);
			a.setFitness(testFitness2(a, expected));
			return a;
		};
	}

	private Callable<Agent> mutateAgentTask(Agent parent, int rounds, Random random) {
		return () -> {
			final Agent a = Mutator.mutate(parent, rounds, random);
			a.setFitness(testFitness2(a, expected));
			return a;
		};
	}

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		// CLI stuff
		final OptionParser parser = new OptionParser();
		parser.accepts("visualize");
		parser.accepts("server");
		final OptionSpec<Integer> poolSizeOption = parser.accepts("poolsize").withRequiredArg().ofType(Integer.class).required();
		final OptionSpec<String> populationOption = parser.accepts("population").withRequiredArg().ofType(String.class);

		final OptionSet options = parser.parse(args);

		// Create instance
		final CodeGeneration codeGeneration = new CodeGeneration(ExactFunction.COS);

		if (options.has("visualize"))
			codeGeneration.eventBus.register(new GraphWindow());

		if (options.has("server"))
			codeGeneration.eventBus.register(new WebServer(80));

		codeGeneration.eventBus.register(new GenerationStats());

		final int poolSize = options.valueOf(poolSizeOption);

		final File folder = new File("populations");
		final File popFolder = options.has(populationOption) ? new File(folder, options.valueOf(populationOption)) : generatePopulationFolder(folder);
		popFolder.mkdirs();
		codeGeneration.eventBus.register(new HeroPersistance(popFolder));

		// x -> (x - 50) * (x - 50) / 25 + 50;
		codeGeneration.generate(poolSize, new Random());
	}

	/**
	 * Compiles the agent and tries to asses it's fitness. Kills it after 100ms if it's not done.
	 */
	@SuppressWarnings("deprecation")
	public static void testFitness(Agent agent, DataPoints expected) {
		try {
			if (compileAgent(agent)) {
				final TestWorker worker = new TestWorker(agent.m);
				final Thread t = new Thread(worker);
				t.start();
				t.join(100);

				if (t.isAlive()) { // It's alive, kill it
					t.interrupt();
					t.stop();
					agent.setFitness(Fitness.FAILED);
					if (DEBUG)
						Log.warning("FAILED with timeout");
				} else if (worker.dataPoints == null) // Calculation failed
					agent.setFitness(Fitness.FAILED);
				else // Calculation successfull
					agent.setFitness(new Fitness(
							true,
							agent.getFunction().getChildren().size(),
							expected,
							new DataPoints(worker.dataPoints)));
			}
		} catch (final Exception ex) {
			agent.setFitness(Fitness.FAILED);
			if (DEBUG)
				Log.warning("FAILED with compilation error: ", ex);
		}
	}

	public static Fitness testFitness2(Agent agent, DataPoints expected) {
		try {
			final int[] datapoints = new int[100];
			final Function f = agent.getFunction();

			for (int i = 0; i < datapoints.length; i++)
				datapoints[i] = f.eval(i);

			// Calculation successfull
			return new Fitness(
					true,
					f.getChildren().size(),
					expected,
					new DataPoints(datapoints));
		} catch (final EvaluationException ex) {
			if (DEBUG)
				Log.warning("Agend failed: ", ex);
			return Fitness.FAILED;
		}
	}

	/**
	 * Compiles an agent.
	 *
	 * @return whether compilation was successful.
	 */
	private static boolean compileAgent(Agent agent) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		if (DEBUG)
			Log.info("Compiling " + agent.getName() + " (Origin " + agent.getOrigin() + ")");

		final String sourceCode = "package agents;" + Utils.NEWLINE
				+ "public class " + agent.getName() + "{" + Utils.NEWLINE
				+ agent.getFunction().toCode() + Utils.NEWLINE + "}";

		final IMCSourceCode imcSource = new IMCSourceCode("agents." + agent.getName(), sourceCode);

		if (compiler.compile(ImmutableList.of(imcSource))) {
			// Compiled successfully
			agent.m = compiler.getCompiledClass(imcSource.fullClassName).getMethod(agent.getFunction().getName(), agent.getFunction().getType().getTypeClass());
			return true;
		}

		// Compiling failed
		agent.setFitness(Fitness.FAILED);
		if (DEBUG)
			Log.warning("FAILED with compilation error: " + sourceCode);
		return false;
	}

	private static File generatePopulationFolder(File folder) {
		File popFolder;
		int pop = 1;
		while ((popFolder = new File(folder, "pop-" + pop + "/")).exists())
			pop++;
		return popFolder;
	}

	private static class TestWorker implements Runnable
	{
		private final Method method;
		int[] dataPoints = null;

		public TestWorker(Method method) {
			this.method = method;
		}

		@Override
		public void run() {
			try {
				final int[] calculated = new int[100];
				for (int i = 0; i < calculated.length; i++)
					calculated[i] = (Integer)method.invoke(null, i);

				dataPoints = calculated;
			} catch (final Exception ex) {
				if (DEBUG)
					Log.warning("FAILED with RuntimeError: ", ex);
			}
		}
	}
}
