package org.acm.auth.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.ReactionRoles;

public class ReactionRolesCommand extends Command {
    private static final Permission[] EMPTY_PERMS = {};

    public ReactionRolesCommand() {
        super("rr", "Creates a message for giving roles to users", false, false, new String[]{}, 1, 3, "", EMPTY_PERMS, EMPTY_PERMS);
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        //-rr $msg-id <role> <reaction>
        String messageId = args[0];
        if (messageId.equals("help")) {
            postHelp(event);
            return;
        }

        Role role = event.getGuild().getRoleById(args[1]);
        String emojiId = args[2];

        ReactionRoles.addReactionRole(messageId, emojiId, role);
        event.getChannel().addReactionById(messageId, emojiId).queue();
    }

    private void postHelp(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Reaction Roles Help")
                .addField("command format", "-rr <message-id> <role-id> <emoji-unicode>", false);
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
