package fr.openmc.core.features.corporation.menu.company;


import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.corporation.Company;
import fr.openmc.core.features.corporation.CompanyManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanySearchMenu extends PaginatedMenu {

    private final CompanyManager companyManager = CompanyManager.getInstance();

    public CompanySearchMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        if (companyManager.isInCompany(getOwner().getUniqueId())) {
            return StaticSlots.combine(StaticSlots.STANDARD, List.of(12, 13, 14));
        }
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Company company : companyManager.getCompanies()) {
            ItemStack companyItem;
            if (companyManager.isInCompany(getOwner().getUniqueId())) {
                companyItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                    itemMeta.setDisplayName("§e" + company.getName());
                    itemMeta.setLore(List.of(
                            "§7■ Chiffre d'affaires : §a"+ company.getTurnover() + "€",
                            "§7■ Marchants : §f" + company.getMerchants().size(),
                            "§7■ Cliquez pour voir les informations de l'enreprise"
                    ));
                }).setNextMenu(new CompanyMenu(getOwner(), company, true));
            } else {
                companyItem = new ItemBuilder(this, company.getHead(), itemMeta -> {
                    itemMeta.setDisplayName("§e" + company.getName());
                    itemMeta.setLore(List.of(
                            "§7■ Chiffre d'affaires : §a" + company.getTurnover() + "€",
                            "§7■ Marchants : §f" + company.getMerchants().size(),
                            "§7■ Candidatures : §f" + companyManager.getPendingApplications(company).size(),
                            "§7■ Cliquez pour postuler"
                    ));
                }).setOnClick((inventoryClickEvent) -> {
                    companyManager.applyToCompany(getOwner().getUniqueId(), company);
                    getOwner().sendMessage("§aVous avez postulé pour l'entreprise " + company.getName() + " !");
                    company.broadCastOwner("§a" + getOwner().getName() + " a postulé pour rejoindre l'entreprise !");
                });
            }
            items.add(new ItemBuilder(this, companyItem));
        }
        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, Material.BARRIER, itemMeta -> itemMeta.setDisplayName("§7Fermer"))
                .setCloseButton());
        map.put(48, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> itemMeta.setDisplayName("§cPage précédente"))
                .setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> itemMeta.setDisplayName("§aPage suivante"))
                .setNextPageButton());
        if (companyManager.isInCompany(getOwner().getUniqueId())) {
            map.put(4, new ItemBuilder(this, companyManager.getCompany(getOwner().getUniqueId()).getHead(), itemMeta -> {
                itemMeta.setDisplayName("§6§l" + companyManager.getCompany(getOwner().getUniqueId()).getName());
                itemMeta.setLore(List.of(
                        "§7■ - Entreprise -",
                        "§7■ Chiffre d'affaires : §a" + companyManager.getCompany(getOwner().getUniqueId()).getTurnover() + "€",
                        "§7■ Marchants : §f" + companyManager.getCompany(getOwner().getUniqueId()).getMerchants().size(),
                        "§7■ Cliquez pour voir les informations de l'entreprise"
                ));
            }).setNextMenu(new CompanyMenu(getOwner(), companyManager.getCompany(getOwner().getUniqueId()), true)));
        }
        return map;
    }

    @Override
    public @NotNull String getName() {
        return companyManager.isInCompany(getOwner().getUniqueId()) ? "Rechercher une entreprise" : "Pôle travail";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
