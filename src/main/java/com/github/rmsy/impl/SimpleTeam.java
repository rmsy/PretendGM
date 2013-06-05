package com.github.rmsy.impl;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import tc.oc.api.Player;
import tc.oc.api.Team;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple implementation of {@link Team}.
 */
public class SimpleTeam implements Team {

    /**
     * Whether or not the team is capable fo participating in its match.
     */
    private final boolean canParticipate;
    /**
     * Whether or not the team is participating in its match.
     */
    private final boolean isParticipating;
    /**
     * Whether or not the team is spectating in its match.
     */
    private final boolean isSpectating;
    /**
     * The initial color, as defined by the map.
     */
    @Nonnull
    private final ChatColor initialColor;
    /**
     * The initial name, as defined by the map.
     */
    @Nonnull
    private final String initialName;
    /**
     * The team's members.
     */
    @Nonnull
    private final Set<Player> members;
    /**
     * The color.
     */
    @Nonnull
    private ChatColor color;
    /**
     * The name.
     */
    @Nonnull
    private String name;

    private SimpleTeam() {
        this.initialName = null;
        this.initialColor = null;
        this.canParticipate = false;
        this.isParticipating = false;
        this.isSpectating = false;
        this.members = null;
    }

    /**
     * Creates a new SimpleTeam.
     *
     * @param name            The name of the team.
     * @param color           The team's color.
     * @param canParticipate  Whether or not the team is capable of participating in its match.
     * @param isParticipating Whether or not the team is participating in its match.
     * @param isSpectating    Whether or not the team is spectating in its match.
     * @throws IllegalArgumentException If the team is participating but is not capable of participating.
     */
    public SimpleTeam(@Nonnull String name, @Nonnull ChatColor color, final boolean canParticipate, final boolean isParticipating, final boolean isSpectating) throws IllegalArgumentException {
        this.initialName = Preconditions.checkNotNull(name, "name");
        this.setName(name);
        this.initialColor = Preconditions.checkNotNull(color, "color");
        this.setColor(color);
        Preconditions.checkArgument(!(!canParticipate && isParticipating), "Team is participating but is not capable of participating.");
        this.canParticipate = canParticipate;
        this.isParticipating = isParticipating;
        this.isSpectating = isSpectating;
        this.members = new HashSet<Player>();
    }

    /**
     * Gets the team's members.
     *
     * @return The team's members.
     */
    @Nonnull
    @Override
    public Set<Player> getMembers() {
        return this.members;
    }

    /**
     * Gets whether or not the team is capable of participating in its match.
     *
     * @return Whether or not the team is capable of participating in its match.
     */
    @Override
    public boolean canParticipate() {
        return this.canParticipate;
    }

    /**
     * Gets whether or not the team is participating in its match.
     *
     * @return Whether or not the team is participating in its match.
     */
    @Override
    public boolean isParticipating() {
        return this.isParticipating;
    }

    /**
     * Gets whether or not the team is spectating the match.
     *
     * @return Whether or not the team is spectating its match.
     */
    @Override
    public boolean isSpectating() {
        return this.isSpectating;
    }

    /**
     * Gets the team's name.
     *
     * @return The team's name.
     */
    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the team's name.
     *
     * @param s The team's name.
     */
    @Override
    public void setName(@Nonnull String s) {
        this.name = Preconditions.checkNotNull(s, "name");
    }

    /**
     * Gets the team's chat display color.
     *
     * @return The team's chat display color.
     */
    @Nonnull
    @Override
    public ChatColor getColor() {
        return this.color;
    }

    /**
     * Sets the team's chat display color.
     *
     * @param chatColor The team's chat display color.
     */
    @Override
    public void setColor(@Nonnull ChatColor chatColor) {
        this.color = Preconditions.checkNotNull(chatColor, "chat color");
    }

    /**
     * Broadcasts a message to the team.
     *
     * @param s The message to be broadcast.
     */
    @Override
    public void broadcastMessage(@Nonnull String s) {
        Preconditions.checkNotNull(s, "message");
        for (Player player : this.members) {
            player.getBukkit().sendMessage(s);
        }
    }

    /**
     * Gets the team's initial chat display color, as defined by the map.
     *
     * @return The team's initial chat display color, as defined by the map.
     */
    @Nonnull
    @Override
    public ChatColor getInitialColor() {
        return this.initialColor;
    }

    /**
     * Gets the team's initial name, as defined by the map.
     *
     * @return The team's initial name, as defined by the map.
     */
    @Nonnull
    @Override
    public String getInitialName() {
        return this.initialName;
    }

    /**
     * Gets the team's colored name.
     *
     * @return The team's colored name.
     */
    @Nonnull
    @Override
    public String getColoredName() {
        return this.color + this.name;
    }
}
