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
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.RED + "[NewPlayer] Unloading NewPlayer...");
        this.saveConfig();
    }

    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String sound = this.getConfig().getString("sound");
        String message = this.getConfig().getString("message");
        String beeper = this.getConfig().getString("pc-beeper");
        String everyjoin = this.getConfig().getString("every-join");
        Player p = e.getPlayer();
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (p2.hasPermission("newplayer.notify")) {
                if (getConfig().get(beeper) == "true") {
                    if (!p.hasPlayedBefore()) {
                        if (cooldown.isEmpty()) {
                            cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                            p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                            p2.sendMessage(message);
                            Toolkit.getDefaultToolkit().beep();
                            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> cooldown.clear(), 40);
                        }
                        return;
                    } else {
                        if (getConfig().get(everyjoin) == "true") {
                            p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                            p2.sendMessage(message);
                        } else {
                            if (!p.hasPlayedBefore()) {
                                p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                                p2.sendMessage(message);
                            } else {
                                if (getConfig().get(everyjoin) == "true") {
                                    p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                                    p2.sendMessage(message);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}