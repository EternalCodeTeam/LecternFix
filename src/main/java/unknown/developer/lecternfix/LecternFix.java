package unknown.developer.lecternfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class LecternFix extends JavaPlugin {

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.WINDOW_CLICK) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (isQuickMove(event) && isLectern(event)) {
                    event.setCancelled(true);
                    plugin.getLogger().info("Lectern quick move packet was cancelled.");
                }
            }
        });
    }

    @Override
    public void onDisable() {
        this.protocolManager.removePacketListeners(this);
    }

    private boolean isQuickMove(PacketEvent event) {
        try {
            for (Field field : event.getPacket().getModifier().getTarget().getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object obj = field.get(event.getPacket().getModifier().getTarget());

                if (obj.getClass().getSimpleName().equals("InventoryClickType") && obj.toString().equals("QUICK_MOVE")) {
                    return true;
                }
            }
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    private boolean isLectern(PacketEvent event) {
        Player player = event.getPlayer();

        return player.getOpenInventory().getType() == InventoryType.LECTERN;
    }

}
