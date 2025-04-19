package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WalkQuests extends Quest implements Listener {
    public WalkQuests() {
        super("Le randonneur", "Marcher {target} blocs", Material.LEATHER_BOOTS, true);

        this.addTiers(
                new QuestTier(4000, new QuestMoneyReward(500)),
                new QuestTier(10000, new QuestMoneyReward(1200)),
                new QuestTier(30000, new QuestMoneyReward(3200)),
                new QuestTier(100000, new QuestMoneyReward(8000))
        );
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (
            (event.getFrom().getBlockX() != event.getTo().getBlockX()
            || event.getFrom().getBlockZ() != event.getTo().getBlockZ())
            && !event.getPlayer().isFlying()
            && !event.getPlayer().isGliding()
        ) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }
}
