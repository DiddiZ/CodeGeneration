package de.diddiz.codegeneration;

import java.io.File;
import java.io.IOException;
import com.google.common.eventbus.Subscribe;
import de.diddiz.codegeneration.events.GenerationCompleteEvent;
import de.diddiz.codegeneration.events.GeneratorStartEvent;
import de.diddiz.utils.Utils;
import de.diddiz.utils.logging.Log;

/**
 * Dumps all heroes into a folder.
 * <p>
 * Load all agents in that folder into agent pool at start up
 */
public class HeroPersistance
{
	private static final String AGENT_PREFIX = "gen-";
	private final File popFolder;
	boolean ignoreFirst = false;

	public HeroPersistance(File popFolder) {
		this.popFolder = popFolder;
	}

	@Subscribe
	public void onGenerationCompleted(GenerationCompleteEvent e) {
		if (ignoreFirst) {
			ignoreFirst = false;
			return;
		}
		if (!e.isStuck())
			try {
				Utils.write(new File(popFolder, AGENT_PREFIX + e.getGeneration() + ".json"), e.getBestAgent().getFunction().toJson());
			} catch (final IOException ex) {
				Log.severe("Failed to write agent dump: ", ex);
			}
	}

	@Subscribe
	public void onGeneratorStart(GeneratorStartEvent e) {
		for (final File file : popFolder.listFiles())
			try {
				e.addAgent(Agent.loadAgent(Utils.read(file), AgentOrigin.ANCIENT));
				if (file.getName().startsWith(AGENT_PREFIX)) {
					// Make generation start some generations later
					final int gen = Integer.parseInt(file.getName().substring(AGENT_PREFIX.length(), file.getName().length() - 5));
					if (gen > e.getGeneration())
						e.setGeneration(gen);
				}
				ignoreFirst = true;
			} catch (final IOException ex) {
				Log.severe("Failed to load agent: ", ex);
			}
	}
}
