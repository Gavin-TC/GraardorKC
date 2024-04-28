package com.gkc;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.eventbus.Subscribe;

public class GKC {
    private Client client;

    public GKC(Client client) {
        this.client = client;
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
        String actorName = actorDeath.getActor().getName();

        if (actorName.equals("")) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "GKC", "General Graardor has died!", null);
        }
    }
}
