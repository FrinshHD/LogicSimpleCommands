package de.frinshhd.logicsimplecommands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LinkInventory implements Listener {

    private final Inventory inv;

    public LinkInventory() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

        String title = Objects.requireNonNull(Main.getInstance().getConfig().getConfigurationSection("linkInventory")).getString("title");

        if (title == null) {
            title = "§bʟɪɴᴋs";
        }

        inv = Bukkit.createInventory(null, 9 * 3, MessageFormat.build(title));
        putItems();
    }

    public void putItems() {

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, fillerItem());
        }

        List<Map<?, ?>> commands = Main.getInstance().getConfig().getMapList("commands");

        commands.forEach(command -> {

            if (!command.containsKey("linkInventory") || !((Boolean) command.get("linkInventory"))) {
                return;
            }

            String materialRaw;

            if (command.containsKey("material")) {
                materialRaw = ((String) command.get("material")).toUpperCase();
            } else {
                materialRaw = "STONE";
            }

            Material material = Material.getMaterial(materialRaw);

            if (material == null) {
                material = Material.STONE;
            }

            ItemStack item = new ItemStack(material);

            if (command.containsKey("name")) {
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(MessageFormat.build((String) command.get("name")));
                item.setItemMeta(itemMeta);
            }

            if (command.containsKey("headValue")) {
                if (item.getType().equals(Material.PLAYER_HEAD)) {
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("bbd0af50-1c26-4e4e-9171-615c844d4a87"));
                    PlayerProfile playerProfile = skullMeta.getPlayerProfile();
                    assert playerProfile != null;
                    playerProfile.setProperty(new ProfileProperty("textures", (String) command.get("headValue")));
                    skullMeta.setPlayerProfile(playerProfile);

                    item.setItemMeta(skullMeta);
                }
            }

            if (command.containsKey("slot")) {
                int slot = (int) command.get("slot");

                if (slot >= 0 && slot < 27) {
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

        List<Map<?, ?>> commands = Main.getInstance().getConfig().getMapList("commands");

        for (Map<?, ?> command : commands) {
            if (command.containsKey("linkInventory") && (Boolean) command.get("linkInventory")) {
                if (command.containsKey("slot") && ((int) command.get("slot")) == event.getSlot()) {
                    player.performCommand((String) command.get("command"));
                    player.closeInventory();
                }
            }
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inv)) {
            event.setCancelled(true);
        }
    }

    public ItemStack fillerItem() {
        String materialRaw = Main.getInstance().getConfig().getConfigurationSection("linkInventory").getString("fillerMaterial");
        Material material;

        if (materialRaw == null) {
            material = Material.GRAY_STAINED_GLASS_PANE;
        } else {
            if (Material.getMaterial(materialRaw.toUpperCase()) == null) {
                material = Material.GRAY_STAINED_GLASS_PANE;
            } else {
                material = Material.getMaterial(materialRaw.toUpperCase());
            }
        }

        assert material != null;

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
