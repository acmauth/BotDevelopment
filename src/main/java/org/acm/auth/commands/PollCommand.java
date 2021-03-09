package org.acm.auth.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.utils.Emoji;

import java.awt.*;

public class PollCommand extends Command{

    // static object for empty perms to be used in the default constructor
    private static final Permission[] NO_PERMS = {};

    public PollCommand() {
        super(
                "poll",
                "Creates a poll",
                true,
                false,
                new String[] {"vote"},
                3,
                20, // Maximum Number of Reactions is 20, and it is kinda overkill to have more than 20 options
                "-poll Question Option1 Option2 ... OptionN",
                NO_PERMS,
                NO_PERMS
        );
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        String argsString = String.join(" ", args);
        // adds an empty string as first and last element, it should be fixed in the future
        args = argsString.split("(\" )?\"");

        // When the above bug is fixed it should be changed to args[0]
        String question = args[1];
        String options = "";

        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(question, null);
        eb.setColor(Color.CYAN);

        // When the above bug is fixed it should be changed from 1 to length
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            options = sb.append(Emoji.REGIONAL_INDICATORS[i-2]).append("\t").append(args[i]).append("\n").toString();
        }

        eb.setDescription(options);

        int length = args.length - 2;
        event.getChannel().sendMessage(eb.build()).queue(message -> {
            for (int i = 0; i < length; i++) {
                message.addReaction(Emoji.REGIONAL_INDICATORS[i]).queue();
            }
        });


    }
}
