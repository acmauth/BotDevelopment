package org.acm.auth.managers;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.acm.auth.utils.ReactionRoles;
import org.jetbrains.annotations.NotNull;

public class ReactionManager extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;

        String roleId = ReactionRoles.getReactionRole(event.getMessageId(), event.getReactionEmote().getEmoji());

        if (roleId == null) {  //role isn't registered
            if (ReactionRoles.msgExists(event.getMessageId())) {  //reaction refers to a reaction role msg
                event.getChannel().removeReactionById(event.getMessageId(), event.getReactionEmote().getEmoji(), event.getUser()).queue();
            }

            return;
        }

        Role role = event.getGuild().getRoleById(roleId);

        try {
            if (role == null) {
                return;
            }

            event.getGuild().addRoleToMember(event.getMember(), role).queue();

            //sends pm to user for added role
            event.getUser().openPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.sendMessage("You got role **" + role.getName() + "**"))
                    .queue();
        } catch (HierarchyException e) {
            //do nothing
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        String roleId = ReactionRoles.getReactionRole(event.getMessageId(), event.getReactionEmote().getEmoji());

        if (roleId == null) {  //role isn't registered
            return;
        }

        Role role = event.getGuild().getRoleById(roleId);

        try {
            if (role == null) {
                return;
            }

            event.getGuild().removeRoleFromMember(event.getUserId(), role).queue();

            //sends pm to user for removed role
            event.getJDA().openPrivateChannelById(event.getUserId())
                    .flatMap(privateChannel -> privateChannel.sendMessage("You removed role **" + role.getName() + "**"))
                    .queue();
        } catch (HierarchyException e) {
            //do nothing
        }
    }
}
