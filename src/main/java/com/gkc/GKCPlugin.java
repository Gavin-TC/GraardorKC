package com.gkc;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Graardor KC",
	description = "A plugin to help keep track of Bandos kills during your trips.",
	tags = {"bandos", "general", "graardor", "gwd", "god wars"}
)
public class GKCPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private GKCConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("GKC started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("GKC stopped!");
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "GKC says " + config.greeting(), null);
		}
	}

	@Provides
	GKCConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GKCConfig.class);
	}
}
