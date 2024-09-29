package de.frinshhd.logicsimplecommands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Inventory implements Listener {

    private final String id;

    private final org.bukkit.inventory.Inventory inv;

    public Inventory(Map<?, ?> map) {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

        this.id = (String) map.get("id");

        String title = (String) map.get("title");

        if (title == null) {
            title = "";
        }

        int rows;

        if (map.get("rows") == null) {
            rows = 6;
        } else {
            rows = (Integer) map.get("rows");;
        }

        inv = Bukkit.createInventory(null, 9 * rows, MessageFormat.build(title));
        putItems((String) map.getOrDefault("fillerMaterial", null), (List<Map<?, ?>>) map.getOrDefault("items", null));
    }

    public void putItems(String fillerMaterialRaw, List<Map<?, ?>> items) {
        if (fillerMaterialRaw != null) {
            Material fillerMaterial;

            if (Material.getMaterial(fillerMaterialRaw.toUpperCase()) == null) {
                fillerMaterial = null;
            } else {
                fillerMaterial = Material.getMaterial(fillerMaterialRaw.toUpperCase());
            }

            if (fillerMaterial != null) {
                for (int i = 0; i < inv.getSize(); i++) {
                    inv.setItem(i, fillerItem(fillerMaterial));
                }
            }
        }

        if (items == null) {
            return;
        }

        items.forEach(invItem -> {

            Material material;

            if (invItem.containsKey("material")) {
                if (Material.getMaterial(((String) invItem.get("material")).toUpperCase()) == null) {
                    return;
                } else {
                    material = Material.getMaterial(((String) invItem.get("material")).toUpperCase());
                }
            } else {
                material = Material.STONE;
            }

            ItemStack item = new ItemStack(material);

            if (invItem.containsKey("name")) {
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(MessageFormat.build((String) invItem.get("name")));
                item.setItemMeta(itemMeta);
            }

            if (invItem.containsKey("headValue")) {
                if (item.getType().equals(Material.PLAYER_HEAD)) {
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    if (skullMeta != null) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer("bbd0af50-1c26-4e4e-9171-615c844d4a87");
                        skullMeta.setOwningPlayer(player);

                        PlayerProfile playerProfile = Bukkit.createProfile(player.getUniqueId());
                        playerProfile.setProperty(new ProfileProperty("textures", (String) invItem.get("headValue")));
                        skullMeta.setPlayerProfile(playerProfile);

                        item.setItemMeta(skullMeta);
                    }
                }
            }

            if (invItem.containsKey("lore")) {
                ItemMeta itemMeta = item.getItemMeta();

                List<String> loreRaw = (List<String>) invItem.get("lore");
                ArrayList<String> lore = new ArrayList<>();

                loreRaw.forEach(line -> {
                    lore.add(MessageFormat.build(line));
                });

                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
            }

            if (invItem.containsKey("slot")) {
                int slot = (int) invItem.get("slot");

                if (slot >= 0 && slot < inv.getSize()) {
                    inv.setItem(slot, item);
                }
            }

        });
    }

    public void openInventory(Player player) {
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getInventory().equals(inv)) return;

        event.setCancelled(true);

        final ItemStack clickedItem = event.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player player = (Player) event.getWhoClicked();

        List<Map<?, ?>> inventories = Main.getInstance().getConfig().getMapList("inventories");


        for (Map<?, ?> inventory : inventories) {
            if (inventory.get("id") == null) {
                continue;
            }

            if (inventory.get("id").equals(id)) {
                if (inventory.get("items") == null) {
                    continue;
                }

                List<Map<?, ?>> items = (List<Map<?, ?>>) inventory.get("items");

                for (Map<?, ?> item : items) {
                    System.out.println(item);
                    if (item.get("slot") == null) {
                        continue;
                    }

                    if (((Integer) item.get("slot")) == event.getSlot()) {
                        if (item.get("actions") == null) {
                            break;
                        }

                        List<Map<?, ?>> actions = (List<Map<?, ?>>) item.get("actions");

                        actions.forEach(action -> {
                            if (action.containsKey("command")) {
                                player.performCommand((String) action.get("command"));
                            }

                            if (action.containsKey("message")) {
                                player.sendMessage(MessageFormat.build((String) action.get("message")));
                            }
                        });
                    }
                }

                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inv)) {
            event.setCancelled(true);
        }
    }

    public ItemStack fillerItem(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(" ");

        //itemMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
        itemMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);

        item.setItemMeta(itemMeta);

        return item;
    }

}
