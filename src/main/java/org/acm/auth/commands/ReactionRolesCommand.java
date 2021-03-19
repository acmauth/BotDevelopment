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

    private final String prefix;

    public ReactionRolesCommand(String prefix) {
        super("rr", "Adds emoji to a msg for giving a role to users", false, false, new String[]{"reactionrole", "reactionroles"}, 1, 3, "", EMPTY_PERMS, EMPTY_PERMS);

        this.prefix = prefix;
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        //-rr help
        //or
        //-rr <msg-id> <role> <reaction>

        String messageId = args[0];
        if (messageId.equalsIgnoreCase("help")) {
            showHelpMsg(event);
            return;
        }

        Role role = event.getGuild().getRoleById(args[1]);
        String emojiUnicode = args[2];

        if (ReactionRoles.addReactionRole(messageId, emojiUnicode, role)) {
            event.getChannel().addReactionById(messageId, emojiUnicode).queue();
            event.getMessage().delete().queue();  //delete command message
            showOkMsg(event, role, emojiUnicode);
        } else {
            event.getMessage().delete().queue();  //delete command message
            showErrorMsg(event, role, emojiUnicode);
        }

    }

    private void showHelpMsg(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Reaction Roles Help")
                .addField("Command options",
                        "`" + prefix + this.getName() + " <message-id> <role-id> <emoji-unicode>` : add reaction role to msg\n"
                                + "`" + prefix + this.getName() + " help` : print help",
                        false);
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void showOkMsg(MessageReceivedEvent event, Role role, String emojiId) {
        //send ok message and deletes it after while
        event.getChannel()
                .sendMessage(new EmbedBuilder()
                        .setDescription("Reaction Role **" + role.getName() + "** -> " + emojiId + " created.")
                        .build())
                .delay(Duration.ofSeconds(10))
                .flatMap(Message::delete)
                .queue();
    }

    private void showErrorMsg(MessageReceivedEvent event, Role role, String emojiId) {
        //send error message and deletes it after while
        event.getChannel()
                .sendMessage(new EmbedBuilder()
                        .setDescription("Reaction Role **" + role.getName() + "** already exists. (**" + role.getName() + "** -> " + emojiId + ")")
                        .build())
                .delay(Duration.ofSeconds(10))
                .flatMap(Message::delete)
                .queue();
    }
}
