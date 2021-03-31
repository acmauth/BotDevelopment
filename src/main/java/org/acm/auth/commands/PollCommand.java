package org.acm.auth.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.Emoji;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollCommand extends Command {

    // static objects for permissions to be used in the default constructor
    private static final Permission[] BOT_PERMS = {Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};
    private static final Permission[] USER_PERMS = {};

    public PollCommand() {
        super(
                "poll",
                "Creates a poll",
                true, // guildOnly
                false, // devOnly
                new String[]{"vote"},
                3,
                20, // Maximum Number of Reactions is 20, and it is kinda overkill to have more than 20 options
                "\"Question\" \"Option1\" \"Option2\" ... \"OptionN\"",
                BOT_PERMS,
                USER_PERMS
        );
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] msgArgs) {
        String argsString = String.join(" ", msgArgs);
        Pattern p = Pattern.compile("\"([^\"]+)\"");
        Matcher m = p.matcher(argsString);

        ArrayList<String> args = new ArrayList<>();
        while (m.find()) {
            // use the first capture group (text between quotes)
            args.add(m.group(1));
        }

        if (args.size() < 3) {
            event.getChannel().sendMessage("Insufficient arguments!").queue();
            return;
        }

        String question = args.get(0);

        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(question, null);
        eb.setColor(Color.CYAN);

        // REGIONAL_INDICATORS[i-1] since we start the loop from 1 and not 0
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.size(); i++) {
            sb.append(Emoji.REGIONAL_INDICATORS[i - 1]).append("\t").append(args.get(i)).append("\n");
        }

        eb.setDescription(sb.toString());

        // Delete the message that asked for the poll
        event.getMessage().delete().queue();

        // length = number of answers so we reduce 1 that corresponds to the question
        int length = args.size() - 1;
        event.getChannel().sendMessage(eb.build()).queue(message -> {
            for (int i = 0; i < length; i++) {
                message.addReaction(Emoji.REGIONAL_INDICATORS[i]).queue();
            }
        });
    }
}
