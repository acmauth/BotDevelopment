package org.acm.auth.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class PollCommand extends Command{

    // static object for empty perms to be used in the default constructor
    private static final Permission[] NO_PERMS = {};
    private static final String[] reactions = {
            "\uD83C\uDDE6", // A
            "\uD83C\uDDE7", // B
            "\uD83C\uDDE8", // C
            "\uD83C\uDDE9", // D
            "\ud83c\uddea", // E
            "\ud83c\uddeb", // F
            "\ud83c\uddec", // G
            "\ud83c\udded", // H
            "\ud83c\uddee", // I
            "\ud83c\uddef", // J
            "\ud83c\uddf0", // K
            "\ud83c\uddf1", // L
            "\ud83c\uddf2", // M
            "\ud83c\uddf3", // N
            "\ud83c\uddf4", // O
            "\ud83c\uddf5", // P
            "\ud83c\uddf6", // Q
            "\ud83c\uddf7", // R
            "\ud83c\uddf8", // S
            "\ud83c\uddf9", // T
            "\ud83c\uddfa", // U
            "\ud83c\uddfb", // V
            "\ud83c\uddfc", // W
            "\ud83c\uddfd", // X
            "\ud83c\uddfe", // Y
            "\ud83c\uddff", // Z



    };

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
        for (int i = 2; i < args.length; i++) {
            options += reactions[i-2] + "\t" + args[i] + "\n";
        }

        eb.setDescription(options);

        int length = args.length - 2;
        event.getChannel().sendMessage(eb.build()).queue(message -> {
            for (int i = 0; i < length; i++) {
                message.addReaction(reactions[i]).queue();
            }
        });


    }
}
