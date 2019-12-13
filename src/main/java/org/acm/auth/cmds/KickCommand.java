package org.acm.auth.cmds;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class KickCommand extends Command {
    public KickCommand() {
        super(
                "kick",
                "Kicks a user",
                true,
                new Permission[] { Permission.KICK_MEMBERS },
                new Permission[] { Permission.KICK_MEMBERS }
        );
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Member author = event.getMember(); assert author != null;
        Member self = event.getGuild().getSelfMember();

        List<Member> members = event.getMessage().getMentionedMembers();
        if (members.size() != 1) {
            // For simplicity, we should only be able to kick 1 user at a time
            event.getChannel().sendMessage("Please specify exactly 1 user to kick!").queue();
            return;
        }

        Member target = members.get(0);
        if (!author.canInteract(target)) {
            // The user that called this command is lower in hierarchy than the target user,
            // which means that they probably shouldn't be allowed to kick them.
            event.getChannel().sendMessage("You cannot interact with that user!").queue();
        } else if (!self.canInteract(target)) {
            // The bot is lower in hierarchy than the target user; this makes us unable to interact with them.
            event.getChannel().sendMessage("The bot cannot interact with that user!").queue();
        } else {
            // Kick the target user with the reason being the command issuer's name for logging reasons
            target.kick(author.getUser().getAsTag()).queue(success -> {
                // Once the kick has gone through, we can react with a check mark emoji
                event.getMessage().addReaction("\uD83D\uDC4C").queue();
            });
        }
    }
}
