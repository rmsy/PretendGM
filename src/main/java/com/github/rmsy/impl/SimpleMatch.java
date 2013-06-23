package com.github.rmsy.impl;

import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.ChatColor;
import org.bukkit.World;
import tc.oc.api.Match;
import tc.oc.api.Player;
import tc.oc.api.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Simple implementation of {@link Match}.
 */
public class SimpleMatch implements Match {

    /**
     * The world in which the match is taking place.
     */
    @Nonnull
    private final World world;
    /**
     * The match's unique identifier.
     */
    @Nonnull
    private final UUID uuid;
    /**
     * All of the match's members.
     */
    @Nonnull
    private final Set<Player> members;
    /**
     * All of the match's teams.
     */
    private final Set<Team> teams;
    /**
     * Whether or not the match is currently running.
     */
    private boolean running;

    private SimpleMatch() {
        this.world = null;
        this.uuid = null;
        this.members = null;
        this.teams = null;
    }

    /**
     * Creates a new SimpleMatch.
     *
     * @param world The world in which the match is taking place.
     */
    public SimpleMatch(@Nonnull final World world) {
        this.world = Preconditions.checkNotNull(world, "world");
        this.uuid = UUID.randomUUID();
        this.running = false;
        this.members = new HashSet<Player>();
        this.teams = new HashSet<Team>();
    }

    /**
     * Gets the world in which the match is taking place.
     *
     * @return The world in which the match is taking place.
     */
    @Nonnull
    @Override
    public World getWorld() {
        return this.world;
    }

    /**
     * Gets the match's unique identifier.
     *
     * @return The match's unique identifier.
     */
    @Nonnull
    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Gets whether or not the match is running.
     *
     * @return Whether or not the match is running.
     */
    @Override
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Starts the match.
     *
     * @return Whether or not the match was successfully started.
     * @throws IllegalStateException If the match was already running.
     */
    @Override
    public boolean start() throws IllegalStateException {
        if (!this.running) {
            int totalParticipatingTeams = 0;
            int readyParticipatingTeams = 0;
            for (Team team : this.teams) {
                if (team.isParticipating()) {
                    totalParticipatingTeams++;
                    if (team.getMembers().size() >= 1) {
                        readyParticipatingTeams++;
                    }
                }
            }

            if (totalParticipatingTeams < 2 || readyParticipatingTeams != totalParticipatingTeams) {
                return false;
            } else {
                this.running = true;
                for (Player player : this.members) {
                    player.getBukkit().sendMessage(ChatColor.RED + "Match started.");
                }
                return true;
            }
        } else {
            throw new IllegalStateException("Match can not be started when already running.");
        }
    }

    /**
     * Ends the match without a winner.
     *
     * @return Whether or not the match was successfully ended.
     * @throws IllegalStateException If the match was not running.
     */
    @Override
    public boolean end() throws IllegalStateException {
        try {
            return this.end(null);
        } catch (IllegalStateException exception) {
            throw exception;
        }
    }

    /**
     * Ends the match in the specified team's favor.
     *
     * @param team The winning team.
     * @return Whether or not the match was successfully ended.
     * @throws IllegalStateException If the match was not running.
     */
    @Override
    public boolean end(@Nullable Team team) {
        if (this.running) {
            this.running = false;
            String message;
            if (team != null) {
                message = ChatColor.RED + "Match ended; " + team.getColoredName() + ChatColor.RESET + ChatColor.RED + " won.";
            } else {
                message = ChatColor.RED + "Match ended without a winner.";
            }
            for (Player player : this.members) {
                player.getBukkit().sendMessage(message);
            }
            return true;
        } else {
            throw new IllegalStateException("Match can not be ended when not running.");
        }
    }

    /**
     * Gets the members of the match.
     *
     * @return The members of the match.
     */
    @Nonnull
    @Override
    public Collection<Player> getPlayers() {
        return this.members;
    }

    /**
     * Gets the participating members of the match.
     *
     * @return The members of the match.
     */
    @Nonnull
    @Override
    public Set<Player> getParticipatingPlayers() {
        Set<Player> participatingPlayers = new HashSet<Player>();
        for (Player player : this.members) {
            if (player.getTeam().isParticipating()) {
                participatingPlayers.add(player);
            }
        }
        return participatingPlayers;
    }

    /**
     * Gets the spectating members of the match.
     *
     * @return The spectating members of the match.
     */
    @Nonnull
    @Override
    public Set<Player> getSpectatingPlayers() {
        Set<Player> spectatingPlayers = new HashSet<Player>();
        for (Player player : this.members) {
            if (player.getTeam().isSpectating()) {
                spectatingPlayers.add(player);
            }
        }
        return spectatingPlayers;
    }

    /**
     * Broadcasts a message to the entire match.
     *
     * @param s The message to be broadcast.
     */
    @Override
    public void broadcast(String s) {
        Preconditions.checkNotNull(s, "message");
        for (Player player : this.members) {
            player.getBukkit().sendMessage(s);
        }
    }

    /**
     * Gets the match's teams.
     *
     * @return The match's teams.
     */
    @Nonnull
    @Override
    public Set<Team> getTeams() {
        return this.teams;
    }

    /**
     * Gets the match's participating teams.
     *
     * @return The match's participating teams.
     */
    @Nonnull
    @Override
    public Set<Team> getParticipatingTeams() {
        Set<Team> participatingTeams = new HashSet<Team>();
        for (Team team : this.teams) {
            if (team.isParticipating()) {
                participatingTeams.add(team);
            }
        }
        return participatingTeams;
    }

    /**
     * Gets the match's spectating teams.
     *
     * @return The match's spectating teams.
     */
    @Nonnull
    @Override
    public Set<Team> getSpectatingTeams() {
        Set<Team> spectatingTeams = new HashSet<Team>();
        for (Team team : this.teams) {
            if (team.isSpectating()) {
                spectatingTeams.add(team);
            }
        }
        return spectatingTeams;
    }

    /**
     * Gets the first other team. Deprecated, because I'm not really sure what that means.
     *
     * @param team The team to exclude.
     * @return The first other team.
     */
    @Deprecated
    @Override
    public Team getFirstOther(Team team) {
        return null;
    }

    /**
     * Permanently adds the specified team to the match.
     *
     * @param team The team to be added.
     * @throws IllegalStateException If the match is in progress.
     */
    public void addTeam(@Nonnull final Team team) throws IllegalStateException {
        if (!this.running) {
            this.teams.add(Preconditions.checkNotNull(team));
        } else {
            throw new IllegalStateException("Team can not be created when match in current world is running.");
        }
    }
}
