package org.acm.auth.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.acm.auth.utils.ReactionRoles;

import java.time.Duration;
import java.util.function.Consumer;

public class ReactionRolesCommand extends Command {
    private static final Permission[] BOT_PERMS = {
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_MANAGE,
            Permission.MESSAGE_ADD_REACTION
    };

    private static final Permission[] USER_PERMS = {
            Permission.MANAGE_ROLES
    };

    private final String prefix;

    private static ReactionRoles.AddRRstatus addRRstatus;

    public ReactionRolesCommand(String prefix) {
        super(
                "rr",
                "Adds emoji to a msg for giving a role to users",
                false,
                false,
                new String[]{"reactionrole", "reactionroles"},
                1,
                3,
                "",
                BOT_PERMS,
                USER_PERMS);

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

        String roleId = args[1];
        String emojiUnicode = args[2];

        //delete user message
        event.getMessage().delete().queue();

        //checks for undefined role
        if (event.getGuild().getRoleById(roleId) == null) {
            System.out.println("unknown role");
            addRRstatus = ReactionRoles.AddRRstatus.ERROR;

            event.getChannel().sendMessage("unknown role").queue();

            return;
        }


        try {
            event.getChannel().addReactionById(messageId, emojiUnicode)
                    .queue(new SuccessHandler<>(event, messageId, emojiUnicode, roleId), new net.dv8tion.jda.api.exceptions.ErrorHandler()
                            .handle(ErrorResponse.UNKNOWN_EMOJI, new ErrorHandler<>(event, "emoji"))
                            .handle(ErrorResponse.UNKNOWN_MESSAGE, new ErrorHandler<>(event, "message"))); /*e -> {
                                event.getChannel().sendMessage("unkown message").queue();
                            }));*/
        } catch (IllegalArgumentException e) {
            System.out.println("illegal arguments");
            event.getChannel().sendMessage("illegal arguments").queue();
            //do nothing
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

    private void showOkMsg(MessageReceivedEvent event, String roleId, String emojiId) {
        String roleName = event.getGuild().getRoleById(roleId).getName();

        //send ok message and deletes it after while
        event.getChannel()
                .sendMessage(new EmbedBuilder()
                        .setDescription("Reaction Role **" + roleName + "** -> " + emojiId + " created.")
                        .build())
                .delay(Duration.ofSeconds(10))
                .flatMap(Message::delete)
                .queue();
    }

    private void showErrorMsg(MessageReceivedEvent event, String roleId, String emojiId, boolean roleExists) {
        String roleName = event.getGuild().getRoleById(roleId).getName();

        //send error message and deletes it after while
        event.getChannel()
                .sendMessage(new EmbedBuilder()
                        .setDescription("Reaction " + (roleExists ? ("Role **" + roleName + "**") : emojiId) +
                                " already exists. (**" + roleName + "** -> " + emojiId + ")")
                        .build())
                .delay(Duration.ofSeconds(10))
                .flatMap(Message::delete)
                .queue();
    }

    private static class ErrorHandler<T> implements Consumer <T> {
        MessageReceivedEvent event;
        String problem;

        ErrorHandler(MessageReceivedEvent event, String problem) {
            this.event = event;
            this.problem = problem;
        }

        @Override
        public void accept(T t) {
            System.out.println("unknown " + problem);
            addRRstatus = ReactionRoles.AddRRstatus.ERROR;

            event.getChannel().sendMessage("unknown " + problem).queue();
        }

    }

    /**
     * Handles successful run of addReactionById()
     */
    private class SuccessHandler<T> implements Consumer<T> {
        MessageReceivedEvent event;
        String messageId;
        String emojiUnicode;
        String roleId;

        SuccessHandler(MessageReceivedEvent event, String messageId, String emojiUnicode, String roleId) {
            this.event = event;
            this.messageId = messageId;
            this.emojiUnicode = emojiUnicode;
            this.roleId = roleId;
        }

        @Override
        public void accept(T t) {
            System.out.println("success");
            addRRstatus = ReactionRoles.addReactionRole(messageId, emojiUnicode, roleId);

            //checks reaction conflicts
            switch (addRRstatus) {
                case OK:
                    showOkMsg(event, roleId, emojiUnicode);
                    break;

                case ROLE_EXISTS:
                    showErrorMsg(event, roleId, ReactionRoles.getEmojiUnicode(messageId, roleId), true);
                    break;

                case REACTION_EXISTS:
                    showErrorMsg(event, ReactionRoles.getReactionRole(messageId, emojiUnicode), emojiUnicode, false);
            }
        }
    }
}
