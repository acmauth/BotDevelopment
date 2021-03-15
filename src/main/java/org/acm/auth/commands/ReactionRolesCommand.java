package org.acm.auth.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.ReactionRoles;

import java.time.Duration;

public class ReactionRolesCommand extends Command {
    private static final Permission[] EMPTY_PERMS = {};

    public ReactionRolesCommand() {
        super("rr", "Adds emoji to a msg for giving a role to users", false, false, new String[]{}, 1, 3, "", EMPTY_PERMS, EMPTY_PERMS);
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        //-rr help
        //or
        //-rr <msg-id> <role> <reaction>

        String messageId = args[0];
        if (messageId.equals("help")) {
            showHelpMsg(event);
            return;
        }

        Role role = event.getGuild().getRoleById(args[1]);
        String emojiId = args[2];

        ReactionRoles.addReactionRole(messageId, emojiId, role);
        event.getChannel().addReactionById(messageId, emojiId).queue();
        event.getMessage().delete().queue();  //delete command message

        //send ok message and deletes it after while
        event.getChannel()
                .sendMessage(new EmbedBuilder()
                        .setDescription("Reaction Role **" + role.getName() + "** -> " + emojiId + " created.")
                        .build())
                .delay(Duration.ofSeconds(5))
                .flatMap(Message::delete)
                .queue();


    }

    private void showHelpMsg(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Reaction Roles Help")
                .addField("Command options",
                        "`-rr <message-id> <role-id> <emoji-unicode>` : add reaction role to msg\n" + "`-rr help` : print help",
                        false);
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
