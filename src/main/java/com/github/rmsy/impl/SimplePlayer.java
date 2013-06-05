package com.github.rmsy.impl;

import com.google.common.base.Preconditions;
import tc.oc.api.Player;
import tc.oc.api.Team;

import javax.annotation.Nonnull;

/**
 * Simple implementation of {@link Player}.
 */
public class SimplePlayer implements Player {

    /**
     * The player's corresponding Bukkit player.
     */
    @Nonnull
    private final org.bukkit.entity.Player bukkit;
    /**
     * The player's team.
     */
    @Nonnull
    private final Team team;

    private SimplePlayer() {
        this.bukkit = null;
        this.team = null;
    }

    /**
     * Creates a new SimplePlayer.
     *
     * @param bukkit The player's corresponding Bukkit player.
     * @param team   The player's team.
     */
    public SimplePlayer(@Nonnull final org.bukkit.entity.Player bukkit, @Nonnull final Team team) {
        this.bukkit = Preconditions.checkNotNull(bukkit, "bukkit player");
        this.team = Preconditions.checkNotNull(team, "team");
    }

    /**
     * Gets the player's corresponding Bukkit player.
     *
     * @return The player's corresponding Bukkit player.
     */
    @Nonnull
    @Override
    public org.bukkit.entity.Player getBukkit() {
        return this.bukkit;
    }

    /**
     * Gets the player's team.
     *
     * @return The player's team.
     */
    @Nonnull
    @Override
    public Team getTeam() {
        return this.team;
    }
}
