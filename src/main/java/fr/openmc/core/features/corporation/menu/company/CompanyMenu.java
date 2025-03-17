package fr.openmc.core.features.corporation.menu.company;


import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corporation.Company;
import fr.openmc.core.features.corporation.data.MerchantData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CompanyMenu extends PaginatedMenu {

    private final Company company;
    private final boolean isBackButton;

    public CompanyMenu(Player owner, Company company, boolean isBackButton) {
        super(owner);
        this.company = company;
        this.isBackButton = isBackButton;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.combine(StaticSlots.STANDARD, List.of(12, 13, 14));
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        Set<UUID> merchants = company.getMerchants().keySet();
        List<ItemStack> items = new ArrayList<>();
        for (UUID merchant : merchants) {
            items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(merchant), itemMeta -> {
                itemMeta.setDisplayName("§e" + Bukkit.getOfflinePlayer(merchant).getName());
                MerchantData merchantData = company.getMerchants().get(merchant);
                itemMeta.setLore(List.of(
                        "§7■ A déposé §a" + merchantData.getAllDepositedItemsAmount() + " items",
                        "§7■ A gagné §a" + merchantData.getMoneyWon() + "€"
                ));
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();

        ItemBuilder closeButton = new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName("§7Fermer")).setCloseButton();
        ItemBuilder backButton = new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.setDisplayName("§7Retour")).setBackButton();

        buttons.put(49, isBackButton ? backButton : closeButton);

        buttons.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                .setPreviousPageButton());

        buttons.put(50, new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName("§aPage suivante"))
                .setNextPageButton());

        ItemStack ownerItem;

        if (company.getOwner().isPlayer()) {
            ownerItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                itemMeta.setDisplayName("§6§l" + Bukkit.getOfflinePlayer(company.getOwner().getPlayer()).getName());
                itemMeta.setLore(List.of(
                        "§7■ - Joueur -",
                        "§7■ Marchants : " + company.getMerchants().size()
                ));
            });
        } else {
            ownerItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                itemMeta.setDisplayName("§6§l" + company.getOwner().getCity().getName());
                itemMeta.setLore(List.of(
                        "§7■ - Team -",
                        "§7■ Marchants : " + company.getMerchants().size()
                ));
            });
        }

        buttons.put(4, ownerItem);

        ItemBuilder bankButton = new ItemBuilder(this, Material.GOLD_INGOT, itemMeta -> {
            itemMeta.setDisplayName("§6Banque d'entreprise");
            itemMeta.setLore(List.of(
                    "§7■ Solde: §a" + company.getBalance() + "€",
                    "§7■ Chiffre d'affaires: §a" + company.getTurnover() + "€",
                    "§7■ Cliquez pour voir les transactions"
            ));
        });

        ItemBuilder shopsButton = new ItemBuilder(this, Material.BARREL, itemMeta -> {
            itemMeta.setDisplayName("§6Shops");
            itemMeta.setLore(List.of(
                    "§7■ Nombre: §a" + company.getShops().size(),
                    "§7■ Cliquez pour voir les shops"
            ));
        });

        if (company.isIn(getOwner().getUniqueId())) {
            buttons.put(26, bankButton.setNextMenu(new CompanyBankTransactionsMenu(getOwner(), company)));
            buttons.put(35, shopsButton.setNextMenu(new ShopManageMenu(getOwner(), company)));
        } else {
            buttons.put(26, bankButton);
            buttons.put(35, shopsButton);
        }

        return buttons;
    }

    @Override
    public @NotNull String getName() {
        return company.getName();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
