package fi.jokni.newplayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public final class NewPlayer extends JavaPlugin implements Listener {
    public static HashMap<Player, Boolean> toggle = new HashMap<Player, Boolean>();
    String enable = this.getConfig().getString("notify-enable");
    String disable = this.getConfig().getString("notify-disable");
    String reloadmsg = this.getConfig().getString("reload-message");
    String sound = this.getConfig().getString("sound");
    String message = this.getConfig().getString("notify-join");
    String noperm = this.getConfig().getString("no-permission");
    boolean beeper = this.getConfig().getBoolean("pc-beeper");
    boolean everyjoin = this.getConfig().getBoolean("every-join");

    @Override
    public void onEnable() {
        System.out.println(ChatColor.GREEN + "[NewPlayer] Loading NewPlayer...");
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getConfig();
        this.saveDefaultConfig();
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> System.out.println(ChatColor.GREEN + "-------[NewPlayer]------\nPlease rate the plugin!\n------------------------"), 600);

    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.RED + "[NewPlayer] Unloading NewPlayer...");
    }

    private HashMap<UUID, Long> cooldown = new HashMap();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (p2.hasPermission("newplayer.notify")) {
                if (!toggle.isEmpty()) {
                    if (beeper) {
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
                            if (everyjoin) {
                                p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                                p2.sendMessage(message);
                                Toolkit.getDefaultToolkit().beep();
                            }
                            return;
                        }
                    } else {
                        if (everyjoin) {
                            p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                            p2.sendMessage(message);
                        } else {
                            if (!p.hasPlayedBefore()) {
                                p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                                p2.sendMessage(message);
                            }
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("newplayer")) {
            if (args.length == 1) {
                String s = args[0];
                Player p = (Player) sender;
                switch (s.toLowerCase()) {
                    case "reload":
                        if (sender.hasPermission("newplayer.reload")) {
                            sender.sendMessage(reloadmsg);
                            this.reloadConfig();
                            break;
                        } else {
                            sender.sendMessage(noperm);
                            break;
                        }
                    case "toggle":
                        if (sender.hasPermission("newplayer.toggle")) {
                            if (toggle.isEmpty()) {
                                toggle.put(p, true);
                                p.sendMessage(enable);
                                break;

                            } else {
                                toggle.clear();
                                p.sendMessage(disable);
                                break;
                            }
                        } else {
                            sender.sendMessage(noperm);
                            break;
                        }
                    default:
                        if (sender.hasPermission("newplayer.help")) {
                            sender.sendMessage("§a§lNewPlayer §8> §fUnknown argument! Please do §e/newplayer §ffor the correct arguments.");
                            break;
                        } else {
                            sender.sendMessage("§a§lNewPlayer §8| §f1.5");
                            sender.sendMessage("§fDownload for youself in SpigotMC: §ehttps://www.spigotmc.org/resources/newplayer.80011/");
                            sender.sendMessage("§fMade by Jokni");
                            sender.sendMessage("§fMade with love and care.");
                            break;
                        }
                }
            } else if (sender.hasPermission("newplayer.help")) {
                sender.sendMessage("§a§lNewPlayer");
                sender.sendMessage("§aCommands:");
                sender.sendMessage("§f/newplayer toggle §8- §7Toggle notifications on and off.");
                sender.sendMessage("§f/newplayer reload §8- §7Reload the config.");
            } else {
                sender.sendMessage("§a§lNewPlayer §8| §f1.5");
                sender.sendMessage("§fDownload for youself in SpigotMC: §ehttps://www.spigotmc.org/resources/newplayer.80011/");
                sender.sendMessage("§fMade by Jokni");
                sender.sendMessage("§fMade with love and care.");
            }
        }return false;
    }
}