package fi.jokni.newplayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public final class NewPlayer extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        System.out.println(ChatColor.GREEN + "[NewPlayer] Loading NewPlayer...");
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getConfig();
        this.saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.RED + "[NewPlayer] Unloading NewPlayer...");
    }

    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String sound = this.getConfig().getString("sound");
        String message = this.getConfig().getString("notify-join");
        boolean beeper = this.getConfig().getBoolean("pc-beeper");
        boolean everyjoin = this.getConfig().getBoolean("every-join");
        Player p = e.getPlayer();
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (p2.hasPermission("newplayer.notify")) {
                if (beeper == true) {
                    if (!p.hasPlayedBefore()) {
                        if (cooldown.isEmpty()) {
                            cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                            p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                            p2.sendMessage(message);
                            Toolkit.getDefaultToolkit().beep();
                            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> cooldown.clear(), 40);
                        } else {
                            return;
                        }
                    } else {
                        if (everyjoin == true) {
                            p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                            p2.sendMessage(message);
                        }return;
                    }
                } else {
                    if (everyjoin == true) {
                        p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                        p2.sendMessage(message);
                    } else {
                        if (!p.hasPlayedBefore()) {
                            p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                            p2.sendMessage(message);
                        }
                    }
                }
            }
        }
    }
}