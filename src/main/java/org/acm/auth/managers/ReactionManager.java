package org.acm.auth.managers;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.priv.PrivateChannelCreateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
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

        //sends pm to user for added role
        event.getUser().openPrivateChannel()
                .flatMap(privateChannel -> privateChannel.sendMessage("You got role **" + role.getName() + "**"))
                .queue();

        ReactionRoles.addUser(event.getUserId(), event.getUser());
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        Role role = ReactionRoles.getReactionRole(event.getMessageId(), event.getReactionEmote().getEmoji());
        event.getGuild().removeRoleFromMember(event.getUserId(), role).queue();

        //sends pm to user for removed role
        ReactionRoles.getUser(event.getUserId()).openPrivateChannel()
                .flatMap(privateChannel -> privateChannel.sendMessage("You removed role **" + role.getName() + "**"))
                .queue();
    }
}
