package org.acm.auth.managers;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.acm.auth.utils.ReactionRoles;
import org.jetbrains.annotations.NotNull;

public class ReactionManager extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;

        Role role = ReactionRoles.getReactionRole(event.getMessageId(), event.getReactionEmote().getEmoji());
        event.getGuild().addRoleToMember(event.getMember(), role).queue();

        event.getUser().openPrivateChannel()
                .flatMap(privateChannel -> privateChannel.sendMessage("You got role **" + role.getName() + "**"))
                .queue();
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        Role role = ReactionRoles.getReactionRole(event.getMessageId(), event.getReactionEmote().getEmoji());
        event.getGuild().removeRoleFromMember(event.getMember(), role).queue();

        event.getUser().openPrivateChannel()
                .flatMap(privateChannel -> privateChannel.sendMessage("You removed role **" + role.getName() + "**"))
                .queue();
    }
}
