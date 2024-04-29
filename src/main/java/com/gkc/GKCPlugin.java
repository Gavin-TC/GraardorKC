package com.gkc;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Graardor KC",
	description = "A plugin to help keep track of Bandos kills during your trips.",
	tags = {"bandos", "general", "graardor", "gwd", "god wars"}
)
public class GKCPlugin extends Plugin
{
	private final int PET_GENERAL_GRAARDOR_ITEM_ID = 12650;
	private final int BONES_ITEM_ID = 526;
	private final WorldArea bandosRoom = new WorldArea(2864, 5351, 13, 20, 2);
	private boolean checkPlayer = true;

	// Ticks since player left Bandos room
	private int ticksSinceEnd;
	// How many ticks have to pass for the counters to hide
	// 100 = 1 minute
	private int ticksToHide = 100;

	@Inject
	private Client client;
	@Inject
	private GKCConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	private KillCounter bossCounter;
	private KillCounter minionCounter;

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
		log.info("Starting GKC plugin...");

		ticksSinceEnd = 0;
		resetKC();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Shutting down GKC plugin...");

		removeBossCounter();
		removeMinionCounter();
		resetKC();
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		// As soon as the player leaves the room, reset the kc
		// Don't check again until the player has been inside the Bandos room
		if (!isInBandosRoom()) {
			ticksSinceEnd++;
			if (checkPlayer) {
				checkPlayer = false;
				resetKC();
			}
		} else if (isInBandosRoom()) {
			checkPlayer = true;

			if (bossCounter == null)
				addBossCounter();
			if (minionCounter == null && config.showMinionKC())
				addMinionCounter();
		}

		// If `ticksToHide` ticks have gone by, 'hide' the counters
		// and reset the KC for later.
		if (ticksSinceEnd > ticksToHide) {
			removeBossCounter();
			removeMinionCounter();
			resetKC();
		}
		log.info("ticksSinceEnd: " + ticksSinceEnd);
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {
		Actor actor = actorDeath.getActor();
		if (isInBandosRoom()) {
			switch (actor.getName()) {
				case "General Graardor":
					bossKC++;
					break;
				case "Sergeant Grimspike":
				case "Sergeant Strongstack":
				case "Sergeant Steelwill":
					mageMinionKC++;
					break;
				default:
					log.info("Enemy name: " + actor.getName());
					break;
			}
			updateCounters();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (!configChanged.getGroup().equals("graardorkc")) return;

		log.info("showMinionKC() = " + config.showMinionKC());

		if (!config.showMinionKC()) {
			removeMinionCounter();
		} else {
			addMinionCounter();
		}
	}

	public void addBossCounter() {
		BufferedImage bossImage = itemManager.getImage(PET_GENERAL_GRAARDOR_ITEM_ID);
		bossCounter = new KillCounter(bossImage, this, bossKC);

		infoBoxManager.addInfoBox(bossCounter);
	}

	public void addMinionCounter() {
		BufferedImage rangeImage = itemManager.getImage(BONES_ITEM_ID);
		minionCounter = new KillCounter(rangeImage, this, getMinionKC());

		infoBoxManager.addInfoBox(minionCounter);
	}

	public void removeBossCounter() {
		if (bossCounter != null)
			infoBoxManager.removeInfoBox(bossCounter);
	}

	public void removeMinionCounter() {
		if (minionCounter != null)
			infoBoxManager.removeInfoBox(minionCounter);
	}

	public void updateCounters() {
		if (bossCounter != null) {
			bossCounter.setCount(bossKC);
		}

		if (config.showMinionKC() && minionCounter != null) {
			minionCounter.setCount(getMinionKC());
		}
	}

	public void resetKC() {
		bossKC = 0;
		rangeMinionKC = 0;
		meleeMinionKC = 0;
		mageMinionKC = 0;
		updateCounters();
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
