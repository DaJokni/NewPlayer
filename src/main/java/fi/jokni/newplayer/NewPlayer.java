package fi.jokni.newplayer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
import java.util.Objects;
import java.util.UUID;

public final class NewPlayer extends JavaPlugin implements Listener {
    public static HashMap<Player, Boolean> toggle = new HashMap<>();
    Integer cooldownconfig = this.getConfig().getInt("cooldown");
    String enable = this.getConfig().getString("notify-enable");
    String disable = this.getConfig().getString("notify-disable");
    String reloadmsg = this.getConfig().getString("reload-message");
    String sound = this.getConfig().getString("sound");
    String noperm = this.getConfig().getString("no-permission");
    boolean beeper = this.getConfig().getBoolean("pc-beeper");
    boolean everyjoin = this.getConfig().getBoolean("every-join");
    boolean actionbarboolean = this.getConfig().getBoolean("notify-join-actionbar");
    boolean titleboolean = this.getConfig().getBoolean("notify-join-title");

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

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (p2.hasPermission("newplayer.notify")) {
                if (!(toggle.get(p2) == null)) {
                    String message = Objects.requireNonNull(this.getConfig().getString("notify-join")).replace("%player%", p.getName());
                    String actionbarmsg = Objects.requireNonNull(this.getConfig().getString("actionbar-message")).replace("%player%", p.getName());
                    String titlemsg = Objects.requireNonNull(this.getConfig().getString("title")).replace("%player%", p.getName());
                    String subtitlemsg = Objects.requireNonNull(this.getConfig().getString("subtitle")).replace("%player%", p.getName());
                    if (beeper) {
                        if (!p.hasPlayedBefore()) {

                            if (cooldown.isEmpty()) {
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                                p2.sendMessage(message);
                                Toolkit.getDefaultToolkit().beep();
                                if (titleboolean) {
                                    p2.sendTitle((titlemsg), (subtitlemsg), 1, 20, 1);
                                }
                                if (actionbarboolean) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg));
                                }
                                Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> cooldown.clear(), cooldownconfig);
                            } else {
                                return;
                            }
                        } else {
                            if (everyjoin) {
                                p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                                p2.sendMessage(message);
                                Toolkit.getDefaultToolkit().beep();
                                if (titleboolean) {
                                    p2.sendTitle((titlemsg), (subtitlemsg), 1, 20, 1);
                                }
                                if (actionbarboolean) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg));
                                }
                            }
                            return;
                        }
                    } else {
                        if (everyjoin) {
                            p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                            p2.sendMessage(message);
                            if (titleboolean) {
                                p2.sendTitle((titlemsg), (subtitlemsg), 1, 20, 1);
                            }
                            if (actionbarboolean) {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg));
                            }
                        } else {
                            if (!p.hasPlayedBefore()) {
                                p2.playSound(p2.getLocation(), Sound.valueOf(sound), 2F, 1F);
                                p2.sendMessage(message);
                                if (titleboolean) {
                                    p2.sendTitle((titlemsg), (subtitlemsg), 1, 20, 1);
                                }
                                if (actionbarboolean) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg));
                                }
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
                        } else {
                            sender.sendMessage(noperm);
                        }
                        break;
                    case "toggle":
                        if (sender.hasPermission("newplayer.toggle")) {
                            if (toggle.isEmpty()) {
                                toggle.put((Player) sender, true);
                                p.sendMessage(enable);

                            } else {
                                toggle.clear();
                                p.sendMessage(disable);
                            }
                        } else {
                            sender.sendMessage(noperm);
                        }
                        break;
                    default:
                        if (sender.hasPermission("newplayer.help")) {
                            sender.sendMessage("§a§lNewPlayer §8> §fUnknown argument! Please do §e/newplayer §ffor the correct arguments.");
                        } else {
                            sender.sendMessage("§a§lNewPlayer §8| §f1.9.2");
                            sender.sendMessage("§fDownload for youself in SpigotMC: §ehttps://www.spigotmc.org/resources/newplayer.80011/");
                            sender.sendMessage("§fMade by Jokni");
                            sender.sendMessage("§fMade with love and care.");
                        }
                        break;
                }
            } else if (sender.hasPermission("newplayer.help")) {
                sender.sendMessage("§a§lNewPlayer");
                sender.sendMessage("§aCommands:");
                sender.sendMessage("§f/newplayer toggle §8- §7Toggle notifications on and off.");
                sender.sendMessage("§f/newplayer reload §8- §7Reload the config.");
            } else {
                sender.sendMessage("§a§lNewPlayer §8| §f1.9.2");
                sender.sendMessage("§fDownload for youself in SpigotMC: §ehttps://www.spigotmc.org/resources/newplayer.80011/");
                sender.sendMessage("§fMade by Jokni");
                sender.sendMessage("§fMade with love and care.");
            }
        }return false;
    }
}