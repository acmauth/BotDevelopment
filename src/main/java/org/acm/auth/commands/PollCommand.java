package org.acm.auth.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.Emoji;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollCommand extends Command{

    // static object for empty perms to be used in the default constructor
    private static final Permission[] NO_PERMS = {};
    private static final Permission[] BOT_PERMS = {Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};

    public PollCommand() {
        super(
                "poll",
                "Creates a poll",
                true,
                false,
                new String[] {"vote"},
                3,
                20, // Maximum Number of Reactions is 20, and it is kinda overkill to have more than 20 options
                "\"Question\" \"Option1\" \"Option2\" ... \"OptionN\"",
                BOT_PERMS,
                NO_PERMS
        );
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] msg_args) {
        String argsString = String.join(" ", msg_args);
        System.out.println(argsString);
        Pattern p = Pattern.compile("\"([^\"]+)\"");
        Matcher m = p.matcher(argsString);
        ArrayList<String> args = new ArrayList<String>();
        while (m.find()) {
            String tempArg = m.group();
            args.add(tempArg.substring(1, tempArg.length() - 1));
        }


        String question = args.get(0);
        String options = "";

        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(question, null);
        eb.setColor(Color.CYAN);

        // REGIONAL_INDICATORS[i-1] since we start the loop from 1 and not 0
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.size(); i++) {
            sb.append(Emoji.REGIONAL_INDICATORS[i-1]).append("\t").append(args.get(i)).append("\n").toString();
        }
        options = sb.toString();

        eb.setDescription(options);

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
