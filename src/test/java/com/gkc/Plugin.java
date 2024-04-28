package com.gkc;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class Plugin
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GKCPlugin.class);
		RuneLite.main(args);
	}
}