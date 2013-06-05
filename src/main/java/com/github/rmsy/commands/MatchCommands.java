package com.github.rmsy.commands;

import com.github.rmsy.impl.SimpleBackend;
import com.github.rmsy.impl.SimpleMatch;
import com.github.rmsy.util.LiquidMetal;
import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.api.Match;
import tc.oc.api.MatchManager;
import tc.oc.api.Team;
import tc.oc.api.backend.BackendManager;

import javax.annotation.Nonnull;

/**
 * Commands for handling matches.
 */
public final class MatchCommands {
    @Command(
            aliases = {"create", "add", "new"},
            desc = "Creates a new match.",
            anyFlags = false,
            min = 0,
            max = 0
    )
    public static void createMatch(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) throws CommandException {
        World world = ((Player) Preconditions.checkNotNull(sender, "sender")).getWorld();
        if (MatchManager.getMatch(world) != null) {
            throw new WrappedCommandException(new IllegalStateException("Match can not be created in world where match is already present."));
        } else {
            ((SimpleBackend) BackendManager.getBackend()).mapMatch(world, new SimpleMatch(world));
            sender.sendMessage(ChatColor.RED + "Match created.");
        }
    }

    @Command(
            aliases = {"start", "begin", "play"},
            desc = "Starts the match.",
            anyFlags = false,
            min = 0,
            max = 0
    )
    public static void startMatch(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) throws CommandException {
        Match match = MatchManager.getMatch(((Player) Preconditions.checkNotNull(sender, "sender")).getWorld());
        if (match == null) {
            throw new WrappedCommandException(new IllegalStateException("Match can not be started in world where no match is present."));
        } else {
            try {
                if (!match.start()) {
                    throw new WrappedCommandException(new IllegalStateException("Match can not be started when not enough teams are ready."));
                }
            } catch (IllegalStateException exception) {
                throw new WrappedCommandException(exception);
            }
        }
    }

    @Command(
            aliases = {"end", "finish"},
            desc = "Ends the match.",
            anyFlags = false,
            min = 0,
            max = -1
    )
    public static void endMatch(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) throws CommandException {
        Team winningTeam = null;
        Match match = MatchManager.getMatch(((Player) Preconditions.checkNotNull(sender, "sender")).getWorld());
        if (Preconditions.checkNotNull(arguments, "arguments").argsLength() >= 1) {
            String userTeam = arguments.getJoinedStrings(0);
            double highestScore = 0.0;
            Team highestScoringTeam = null;
            for (Team team : match.getParticipatingTeams()) {
                double score = LiquidMetal.score(team.getName(), userTeam);
                if (score > highestScore && score >= LiquidMetal.SCORE_TRAILING_BUT_STARTED) {
                    highestScore = score;
                    highestScoringTeam = team;
                }
            }

            if (highestScoringTeam != null) {
                winningTeam = highestScoringTeam;
            } else {
                throw new WrappedCommandException(new IllegalArgumentException("Match can not be ended in favor of a non-existent team."));
            }
        }

        try {
            match.end(winningTeam);
        } catch (IllegalStateException exception) {
            throw new WrappedCommandException(exception);
        }
    }

    public static class MatchParentCommand {
        @Command(
                aliases = {"match"},
                desc = "Command for handling matches.",
                min = 1,
                max = -1,
                usage = "<create | start | end [team]>"
        )
        @NestedCommand(value = MatchCommands.class, executeBody = false)
        public static void matchCommands(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) {
            //  never executed
        }
    }
}
