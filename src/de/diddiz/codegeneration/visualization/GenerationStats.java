package de.diddiz.codegeneration.visualization;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import com.google.common.eventbus.Subscribe;
import de.diddiz.codegeneration.Agent;
import de.diddiz.codegeneration.AgentOrigin;
import de.diddiz.codegeneration.events.GenerationCompleteEvent;
import de.diddiz.utils.logging.Log;

public class GenerationStats
{
	private final DecimalFormat decimalFormat = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));
	private final Group[] groups = new Group[AgentOrigin.values().length];

	public GenerationStats() {
		for (final AgentOrigin origin : AgentOrigin.values())
			groups[origin.ordinal()] = new Group();
	}

	@Subscribe
	public void onGenerationCompleted(GenerationCompleteEvent e) {
		for (final AgentOrigin origin : AgentOrigin.values())
			groups[origin.ordinal()].reset();
		;

		for (final Agent a : e.getAgents())
			groups[a.getOrigin().ordinal()].add(a);

		for (final AgentOrigin origin : AgentOrigin.values()) {
			final Group group = groups[origin.ordinal()];

			if (group.count > 0)
				Log.info("Group " + origin + " - Count: " + group.count + ", Avg: " + decimalFormat.format(group.scoreSum / group.count) + ", Failed: " + group.failed);
		}

	}

	private static class Group
	{
		private double scoreSum;
		private int count, failed;

		public void add(Agent a) {
			count++;
			scoreSum += a.getFitness().score;
			if (!a.getFitness().success)
				failed++;
		}

		public void reset() {
			scoreSum = 0;
			count = 0;
			failed = 0;
		}
	}
}
