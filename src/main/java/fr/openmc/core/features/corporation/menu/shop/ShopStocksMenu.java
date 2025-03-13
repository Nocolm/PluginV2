package fr.openmc.core.features.corporation.menu.shop;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.CompanyManager;
import fr.openmc.core.features.corporation.PlayerShopManager;
import fr.openmc.core.features.corporation.Shop;
import fr.openmc.core.features.corporation.ShopItem;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.menu.ConfirmMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopStocksMenu extends PaginatedMenu {

    private final CompanyManager companyManager;
    private final PlayerShopManager playerShopManager;
    private final Shop shop;
    private final int itemIndex;
    private ShopItem stock;

    public ShopStocksMenu(Player owner, CompanyManager companyManager, PlayerShopManager playerShopManager, Shop shop, int itemIndex) {
        super(owner);
        this.companyManager = companyManager;
        this.playerShopManager = playerShopManager;
        this.shop = shop;
        this.itemIndex = itemIndex;
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new java.util.ArrayList<>();
        for (ShopItem stock : shop.getItems()) {
            this.stock = stock;
            items.add(new ItemBuilder(this, stock.getItem().getType(), itemMeta -> {
                itemMeta.setDisplayName(ChatColor.YELLOW + ShopItem.getItemName(stock.getItem()));
                itemMeta.setLore(List.of(
                        ChatColor.GRAY + "■ Quantitée restante: " + EconomyManager.getInstance().getFormattedNumber(stock.getAmount()),
                        ChatColor.GRAY + "■ Prix de vente (par item) : " + EconomyManager.getInstance().getFormattedNumber(stock.getPricePerItem()),
                        ChatColor.GRAY + (stock.getAmount() > 0 ? "■ Click gauche pour récupérer le stock" : "■ Click gauche pour retirer l'item de la vente")
                ));
            }).setOnClick(inventoryClickEvent -> {
                if (stock.getAmount() > 0) {
                    ItemStack toGive = stock.getItem().clone();
                    toGive.setAmount(stock.getAmount());
                    getOwner().getInventory().addItem(toGive);
                    stock.setAmount(0);
                    open();
                }
                else {
                    new ConfirmMenu(getOwner(),this, this::accept, this::refuse,"§7Accepter", "§7Refuser" ).open();
                }
            }));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        buttons.put(49, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName(ChatColor.GRAY + "Fermer"))
                .setCloseButton());
        ItemBuilder nextPageButton = new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.GREEN + "Page suivante"));
        if ((getPage() == 0 && isLastPage()) || shop.getSales().isEmpty()) {
            buttons.put(48, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.setDisplayName(ChatColor.RED + "Retour"))
                    .setNextMenu(new ShopMenu(getOwner(), companyManager, playerShopManager, shop, itemIndex)));
            buttons.put(50, nextPageButton);
        } else {
            buttons.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName(ChatColor.RED + "Page précédente"))
                    .setPreviousPageButton());
            buttons.put(50, nextPageButton.setNextPageButton());
        }
        return buttons;
    }

    @Override
    public @NotNull String getName() {
        return "Stocks de " + shop.getName();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    private void accept() {
        shop.removeItem(stock);
        getOwner().sendMessage(ChatColor.GREEN + "L'item a bien été retiré du shop !");
        if (stock.getAmount() > 0) {
            ItemStack toGive = stock.getItem().clone();
            toGive.setAmount(stock.getAmount());
            getOwner().getInventory().addItem(toGive);
            getOwner().sendMessage(ChatColor.GOLD + "Vous avez récupéré le stock restant de cet item");
        }
        getOwner().closeInventory();
    }

    private void refuse() {
        getOwner().closeInventory();
    }
}
