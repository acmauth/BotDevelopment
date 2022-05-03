package org.acm.auth.commands;

import com.mongodb.client.MongoClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class UpdateTeamRankingCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(UpdateTeamRankingCommand.class);

    public UpdateTeamRankingCommand() {
        super("updateTeam",
                "Update rankings for a team for Days Of Coding Event",
                true,
                false,
                new String[] { "updateTeam", "update" },
                2,
                2,
                "update ABCDE 25",
                EMPTY_PERMS,
                EMPTY_PERMS);
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        LOGGER.info("Starting updating process for document with code: " + args[0] + " ranking " + args[1]);

        MongoClient client = DatabaseUtil.getMongoClientInstance();
        var db = client.getDatabase(DatabaseUtil.getDaysOfCodingDatabaseName());
        var collection = db.getCollection("teams");

        if(args[0].length() != 5) {
            LOGGER.info("Invalid team code");
            event.getMessage().reply("Invalid team code!").queue();
            return;
        }

        // validate that team code exists
        var result = collection.countDocuments(eq("code", args[0]));
        if(result == 0) {
            event.getMessage().reply("Team with code: **" + args[0] + "** does not exist").queue();
            return;
        }

        try {
            int newRanking = Integer.parseInt(args[1]);
            collection.updateOne(eq("code", args[0]), set("ranking", newRanking));
            event.getMessage().reply("Ranking of team with code **" + args[0] + "** was successfully updated to " + args[1]).queue();
        } catch (NumberFormatException e) {
            LOGGER.info("Invalid ranking value");
            event.getMessage().reply("Invalid team ranking!").queue();
        } catch (Exception e) {
            LOGGER.warn("Cannot update ranking for team with code: " + args[0] + " " + e.getMessage());
            event.getMessage().reply("Cannot update team's ranking right now! Please try again in a couple of seconds.").queue();
        }
    }
}
