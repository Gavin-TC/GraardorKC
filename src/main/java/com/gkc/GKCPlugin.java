package com.gkc;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ActorDeath;
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
	private final WorldArea bandosRoom = new WorldArea(2864, 5351, 13, 20, 2);

	@Inject
	private Client client;
	@Inject
	private GKCConfig config;

	@Getter
	private int bossKC;
	@Getter
	private int rangeMinionKC;
	@Getter
	private int meleeMinionKC;
	@Getter
	private int mageMinionKC;

	@Getter
	private boolean canCount = false;  // Determines whether the plugin will start counting kills

	@Override
	protected void startUp() throws Exception
	{
		log.info("GKC started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		bossKC = 0;
		rangeMinionKC = 0;
		meleeMinionKC = 0;
		mageMinionKC = 0;
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "isInBandosRoom: " + isInBandosRoom(), "GKC");
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {
		Actor actor = actorDeath.getActor();
		if (isInBandosRoom() || true) {
			switch (actor.getName()) {
				case "General Graardor":
					bossKC++;
					break;
				case "Sergeant Grimspike":
					rangeMinionKC++;
					break;
				case "Sergeant Strongstack":
					meleeMinionKC++;
					break;
				case "Sergeant Steelwill":
					mageMinionKC++;
					break;
				default:
					log.info("Enemy name: " + actor.getName());
					break;
			}
		}
	}

	public boolean isInBandosRoom() {
		Player localPlayer = client.getLocalPlayer();
		return localPlayer != null && localPlayer.getWorldArea().intersectsWith(bandosRoom);
	}

	public int getMinionKC() {
		return rangeMinionKC + meleeMinionKC + mageMinionKC;
	}

	@Provides
	GKCConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GKCConfig.class);
	}
}
