package com.gkc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(GKCConfig.GROUP)
public interface GKCConfig extends Config
{
	String GROUP = "graardorkc";

	@ConfigItem(
			keyName = "showElapsed",
			name = "Show time elapsed",
			description = "Shows time inside General Graardor's room"
	)
	default boolean showElapsed() {
		return true;
	}

	@ConfigItem(
			keyName = "showMinionKC",
			name = "Show minion kill count",
			description = "Shows the number of minions killed"
	)
	default boolean showMinionKC() {
		return true;
	}

	default String greeting() {
		return "hello";
	}
}
