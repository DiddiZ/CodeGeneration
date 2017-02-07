package de.diddiz.codegeneration.webinterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import com.google.common.eventbus.Subscribe;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.diddiz.codegeneration.Agent;
import de.diddiz.codegeneration.codetree.Codetree;
import de.diddiz.codegeneration.events.GenerationCompleteEvent;
import de.diddiz.utils.Utils;

public class WebServer implements HttpHandler
{
	private Agent best;
	private final String template = Utils.read(new File("web/graph.html"));

	public WebServer(int port) throws IOException {
		final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/best", this);
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		String response;

		if (best != null) {
			response = template.replace("%%DATA_EXPECTED%%", Codetree.GSON.toJson(best.getFitness().expected.toPoints()));
			response = response.replace("%%DATA_ACTUAL%%", Codetree.GSON.toJson(best.getFitness().actual.toPoints()));
			response = response.replace("%%CODE%%", best.getFunction().toCode());
			response = response.replace("%%CODE_TREE%%", best.getFunction().toJson());
			response = response.replace("%%SCORE%%", String.valueOf(best.getFitness().score));
		} else
			response = "not ready";

		t.sendResponseHeaders(200, response.length());
		try (OutputStreamWriter writer = new OutputStreamWriter(t.getResponseBody())) {
			writer.write(response);
		}
	}

	@Subscribe
	public void onGenerationCompleted(GenerationCompleteEvent e) {
		best = e.getBestAgent();
	}
}
