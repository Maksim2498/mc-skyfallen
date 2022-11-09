package space.moontalk.mc.skyfallen;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.val;

@Getter
public class Skyfallen extends    JavaPlugin
                       implements Listener {
    @Nullable World  world;
              double height;

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        val entity = event.getEntity();
        val world  = entity.getWorld();
        
        if (world.getEnvironment() == Environment.THE_END
         && event.getCause()       == DamageCause.VOID) {
            event.setCancelled(true);

            val scale       = world.getCoordinateScale();
            val oldLocation = entity.getLocation();
            val location    = new Location(
                this.world,
                scale * oldLocation.getX(),
                height,
                scale * oldLocation.getZ()
            );

            entity.teleport(location);
        }
    }

    @Override
    public void onEnable() {
        try {
            init();
        } catch (Exception exception) {
            val logger  = getLogger();
            val message = String.format("Config is invalid: %s\nDisabling plugin...", exception.getMessage());
            logger.info(message);

            val server  = getServer();
            val manager = server.getPluginManager();
            manager.disablePlugin(this);
        }

        register();
    }

    private void init() throws Exception {
        val server    = getServer();
        val config    = getConfig();

        val worldName = config.getString("world");
        val world     = server.getWorld(worldName);

        if (world == null) {
            val message = String.format("world %s not found", worldName);
            throw new Exception(message);
        }

        val height = config.getDouble("height");

        this.world  = world;
        this.height = height;
    }

    private void register() {
        val server  = getServer();
        val manager = server.getPluginManager();
        manager.registerEvents(this, this);
    }
}
