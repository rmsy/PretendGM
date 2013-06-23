package com.github.rmsy.commands;

import com.github.rmsy.impl.SimpleBackend;
import com.github.rmsy.impl.SimpleMatch;
import com.github.rmsy.impl.SimplePlayer;
import com.github.rmsy.impl.SimpleTeam;
import com.github.rmsy.util.LiquidMetal;
import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.api.Match;
import tc.oc.api.MatchManager;
import tc.oc.api.PlayerManager;
import tc.oc.api.Team;
import tc.oc.api.backend.BackendManager;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Commands for handling teams.
 */
public final class TeamCommands {

    /*
     * -p - Marks the team as able to participate.
     * -P - Marks the team as participating.
     * -s - Marks the team as spectating.
     * -c - Specifies the team's color.
     */
    @Command(
            aliases = {"create", "new", "add"},
            desc = "Creates a new team for the current match.",
            anyFlags = false,
            flags = "pPsc:",
            help = "Use -p to mark the team as able to participate, -P to mark the team as participating, -c to specify the team's color, and -s to mark the team as spectating.",
            min = 1,
            max = -1,
            usage = "<name> [-p] [-P] [-s] [-c:color character]"
    )
    public static void createTeam(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) throws CommandException {
        Set<Character> flags = Preconditions.checkNotNull(arguments, "arguments").getFlags();
        Player sendingPlayer = (Player) Preconditions.checkNotNull(sender, "sender");
        Match match = MatchManager.getMatch(sendingPlayer.getWorld());
        boolean canParticipate = flags.contains('p');
        boolean isParticipating = flags.contains('P');
        boolean isSpectating = flags.contains('s');
        org.bukkit.ChatColor color = org.bukkit.ChatColor.getByChar(arguments.getFlag('c'));
        String name = arguments.getString(0);
        if (color == null) {
            throw new CommandUsageException("Invalid color code specified.", "/team create Red Team -c:c");
        } else if (isParticipating && !canParticipate) {
            throw new CommandUsageException("Team can not be participating when not able to participate.", "/team create Red Team -p -P -c:c");
        } else if (isParticipating && isSpectating) {
            throw new CommandUsageException("Team can not be participating when spectating.", "/team create Observers -s -c:b");
        } else if (match == null) {
            throw new WrappedCommandException(new IllegalStateException("Team can not be created when no match exists in current world."));
        } else {
            for (Team team : match.getTeams()) {
                if (team.getName().equalsIgnoreCase(name)) {
                    throw new WrappedCommandException(new IllegalStateException("Team with specified name already exists."));
                }
            }
            try {
                ((SimpleMatch) match).addTeam(new SimpleTeam(name, color, canParticipate, isParticipating, isSpectating));
            } catch (IllegalStateException exception) {
                throw new WrappedCommandException(exception);
            }
            sender.sendMessage(ChatColor.RED + "Team created.");
        }
    }

    @Command(
            aliases = {"edit"},
            desc = "Modifies an existing team.",
            flags = "pPsc:",
            help = "Use -p to mark the team as able to participate, -P to mark the team as participating, and -s to mark the team as spectating.",
            min = 1,
            max = -1,
            usage = "<team> [world] [color] [-p] [-P] [-s]"
    )
    @Console
    public static void editTeam(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) throws CommandException {
        Set<Character> flags = Preconditions.checkNotNull(arguments, "arguments").getFlags();

    }

    @Command(
            aliases = {"join", "play"},
            desc = "Joins yourself (or the specified target) to the specified team.",
            min = 1,
            max = 2,
            flags = "t:w:",
            usage = "<team> [-t:target] [-w:world]"
    )
    @Console
    public static void joinTeam(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) throws CommandException {
        if (Preconditions.checkNotNull(sender, "sender") instanceof Player) {

        } else {
            if (Preconditions.checkNotNull(arguments, "arguments").hasFlag('w') && arguments.hasFlag('t')) {
                String worldName = arguments.getFlag('w');
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Match match = MatchManager.getMatch(world);
                    if (match != null) {
                        String targetName = arguments.getFlag('t');
                        Player target = Bukkit.getPlayer(targetName);
                        if (target != null) {
                            if (PlayerManager.getPlayer(target) == null) {
                                String teamName = arguments.getString(0);
                                Team team = LiquidMetal.matchTeam(match, teamName);
                                if (team != null) {
                                    tc.oc.api.Player apiTarget = new SimplePlayer(target, team);
                                    ((SimpleTeam) team).addMember(apiTarget);
                                    ((SimpleBackend) BackendManager.getBackend()).mapPlayer(target, apiTarget);
                                    sender.sendMessage(ChatColor.YELLOW + "Player " + ChatColor.RESET + target.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " added to team " + ChatColor.RESET + team.getColoredName());
                                } else {
                                    throw new WrappedCommandException(new IllegalArgumentException("Team \"" + teamName + "\" not found."));
                                }
                            } else {
                                throw new WrappedCommandException(new IllegalStateException("Player " + target.getName() + " is already on a team."));
                            }
                        } else {
                            throw new WrappedCommandException(new IllegalArgumentException("Player \"" + targetName + "\" not found."));
                        }
                    } else {
                        throw new WrappedCommandException(new IllegalArgumentException("No match found in world \"" + worldName + "\"."));
                    }
                } else {
                    throw new WrappedCommandException(new IllegalArgumentException("World \"" + worldName + "\" not found."));
                }
            } else {
                throw new CommandUsageException("You must specify a target and a world.", "/team join <team> <-t:<target>> <-w:<world>>");
            }
        }
    }

    public static class TeamParentCommand {
        @Command(
                aliases = {"team"},
                desc = "Command for handling teams.",
                min = 1,
                max = -1,
                usage = "<create | remove | edit | join>"
        )
        @NestedCommand(value = TeamCommands.class, executeBody = false)
        public static void teamCommand(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) {
            //  Never executed
        }
    }
}
