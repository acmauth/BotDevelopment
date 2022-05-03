package org.acm.auth.commands;

import com.mongodb.client.MongoClient;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Sorts.descending;

public class ShowRankingsCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(ShowRankingsCommand.class);
    private static final String[] emojis = new String[] {
            EmojiParser.parseToUnicode(":first_place:"),
            EmojiParser.parseToUnicode(":second_place:"),
            EmojiParser.parseToUnicode(":third_place:"),
    };

    public ShowRankingsCommand() {
        super(
                "showRankings",
                "Show the rankings of the teams for Days Of Coding event",
                true,
                false,
                new String[] { "showRankings", "show" },
                0,
                0,
                "showRankings",
                EMPTY_PERMS,
                EMPTY_PERMS
        );
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        LOGGER.info("Starting show rankings process");

        MongoClient client = DatabaseUtil.getMongoClientInstance();
        var db = client.getDatabase(DatabaseUtil.getDaysOfCodingDatabaseName());
        var collection = db.getCollection("teams");
        var teams = collection.find().sort(descending("ranking"));

        StringBuilder stringBuilder = new StringBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Days of Coding Team Rankings")
                .setFooter(
                        EmojiParser.parseToUnicode(":robot_face:") +
                        " Powered by ACM Auth Bot Development Team " +
                        EmojiParser.parseToUnicode(":robot_face:")
                )
                .setColor(Color.CYAN);


        int index = 0;
        for(var team : teams) {
            if(index < 3) {
                stringBuilder.append(emojis[index]);
            } else {
                stringBuilder.append(":medal:");
            }

            stringBuilder.append(" ");
            stringBuilder.append((index+1));
            stringBuilder.append(". ");
            stringBuilder.append(team.get("name"));
            stringBuilder.append("   ");
            stringBuilder.append(team.get("ranking"));
            stringBuilder.append(" ");

            if(index < 3) {
                stringBuilder.append(emojis[index]);
            }
            else {
                stringBuilder.append(":medal:");
            }

            ArrayList<Document> teamPlayers = team.get("players", ArrayList.class);
            embedBuilder.addField(
                    new MessageEmbed.Field(
                            stringBuilder.toString(),
                            teamPlayers.stream().map(player ->
                                    player.get("name").toString()).collect(Collectors.joining(" ")
                            ),
                            false));
            stringBuilder.setLength(0);
            index++;
        }

        LOGGER.info("Sending rankings info");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
