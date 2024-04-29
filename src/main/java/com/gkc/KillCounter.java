package com.gkc;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.image.BufferedImage;

class KillCounter extends Counter {
    KillCounter(BufferedImage img, Plugin plugin, int amount) {
        super(img, plugin, amount);
    }
}
