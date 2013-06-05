package com.github.rmsy.impl;

import com.google.common.base.Preconditions;
import org.bukkit.World;
import tc.oc.api.Match;
import tc.oc.api.Player;
import tc.oc.api.backend.Backend;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@link Backend}.
 */
public final class SimpleBackend implements Backend {

    /**
     * A map of Bukkit players to their PGM counterparts.
     */
    private final Map<org.bukkit.entity.Player, Player> playerMap;
    /**
     * A map of worlds to their corresponding matches.
     */
    private final Map<World, Match> matchMap;

    /**
     * Creates a new backend.
     */
    public SimpleBackend() {
        this.playerMap = new HashMap<org.bukkit.entity.Player, Player>();
        this.matchMap = new HashMap<World, Match>();
    }

    /**
     * Gets the PGM player that corresponds with the specified Bukkit player.
     *
     * @param player The player to look for.
     * @return The corresponding PGM player.
     */
    @Nullable
    @Override
    public Player getPlayer(@Nonnull org.bukkit.entity.Player player) {
        if (player == null) {
            return null;
        } else {
            return this.playerMap.get(player);
        }
    }

    /**
     * Gets all of the matches on the server.
     *
     * @return All of the matches on the server.
     */
    @Nonnull
    @Override
    public Collection<Match> getMatches() {
        return this.matchMap.values();
    }

    /**
     * Gets the match registered in a single-match environment. Deprecated, because I'm not entirely sure how to
     * implement.
     *
     * @return The match registered in a single-match environment.
     */
    @Nullable
    @Override
    @Deprecated
    public Match getMatch() {
        return null;
    }

    /**
     * Gets the match that corresponds with the specified world.
     *
     * @param world The world to look for.
     * @return The corresponding match.
     */
    @Nullable
    @Override
    public Match getMatch(@Nonnull World world) {
        if (world == null) {
            return null;
        } else {
            return this.matchMap.get(world);
        }
    }

    /**
     * Maps the specified Bukkit player to the specified PGM player.
     *
     * @param bukkit The Bukkit player to map.
     * @param player The PGM player to map.
     */
    public void mapPlayer(@Nonnull final org.bukkit.entity.Player bukkit, @Nonnull final Player player) {
        this.playerMap.put(Preconditions.checkNotNull(bukkit, "bukkit player"), Preconditions.checkNotNull(player, "PGM player"));
    }

    /**
     * Removes the specified Bukkit player from the map.
     *
     * @param bukkit The player to be removed.
     */
    public void removePlayer(@Nonnull final org.bukkit.entity.Player bukkit) {
        this.playerMap.remove(Preconditions.checkNotNull(bukkit));
    }

    /**
     * Maps the specified world to the specified match.
     *
     * @param world The world to be mapped.
     * @param match The match.
     */
    public void mapMatch(@Nonnull final World world, @Nonnull final Match match) {
        this.matchMap.put(Preconditions.checkNotNull(world, "world"), Preconditions.checkNotNull(match, "match"));
    }

    /**
     * Removes the specified world from the map.
     *
     * @param world The world in which the match to be removed is taking place.
     * @throws IllegalStateException    If the match is still running.
     * @throws IllegalArgumentException If no match for the specified world is found.
     */
    public void removeMatch(@Nonnull final World world) throws IllegalStateException, IllegalArgumentException {
        Match match = this.matchMap.get(Preconditions.checkNotNull(world, "world"));
        if (match != null) {
            if (!match.isRunning()) {
                this.matchMap.remove(world);
            } else {
                throw new IllegalStateException("Match can not be removed when running.");
            }
        } else {
            throw new IllegalArgumentException("No match for specified world found.");
        }
    }
}
