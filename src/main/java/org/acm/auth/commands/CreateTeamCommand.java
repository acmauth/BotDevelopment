package org.acm.auth.commands;

import com.mongodb.client.MongoClient;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.acm.auth.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class CreateTeamCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(CreateTeamCommand.class);

    public CreateTeamCommand() {
        super(
                "createTeam",
                "Create a team for Days Of Coding event",
                true,
                false,
                new String[] { "createTeam", "create" },
                4,
                10,
                "createTeam ABCDE team-name username#1234 1039183971238916",
                EMPTY_PERMS,
                EMPTY_PERMS
        );
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        StringBuilder log = new StringBuilder("Starting insertion process for document with code: ");
        log.append(args[0]);
        log.append(" team name: ");
        log.append(args[1]);
        log.append(" players: ");
        for(int i=2;i<args.length;i++)
            log.append(args[i]).append(" ");

        LOGGER.info(log);

        if(!event.getChannel().getId().equals(Dotenv.load().get("GRADING_CHANNEL_ID"))) {
            event.getMessage().reply("This command cannot be executed in this channel!").queue();
            return;
        }

        if(!validArguments(event, args)) {
            LOGGER.info("Invalid command arguments");
            return;
        }

        MongoClient client = DatabaseUtil.getMongoClientInstance();
        var db = client.getDatabase(DatabaseUtil.getDaysOfCodingDatabaseName());
        var collection = db.getCollection("teams");

        // validate uniqueness of team code
        var result = collection.countDocuments(eq("code", args[0]));
        if(result != 0) {
            event.getMessage().reply("Team with code: **" + args[0] + "** already exists").queue();
            return;
        }

        // validate uniqueness of team name
        result = collection.countDocuments(eq("name", args[1]));
        if(result != 0) {
            event.getMessage().reply("Team with name: **" + args[1] + "** already exists").queue();
            return;
        }

        var players = new java.util.ArrayList<>(List.of(new Document("name", args[2]).append("discordId", args[3])));

        var discordMembers = new ArrayList<Member>();   //List of discord member objects
        discordMembers.add(event.getGuild().retrieveMemberById(args[3]).complete());
        LOGGER.info(args[3] + " " + discordMembers.get(0));

        for(int i=4;i<args.length-1;i+=2) {
            players.add(new Document("name", args[i]).append("discordId", args[i+1]));
            discordMembers.add(event.getGuild().retrieveMemberById(args[i+1]).complete());
        }

        /*
         * Add the team to the database
         */
        var newTeam = new Document("code", args[0]).append("name", args[1]).append("ranking", 0).append("players", players);
        try {
            LOGGER.info("Executing insertion for document with code: " + args[0] + " team name: " + args[1]);
            collection.insertOne(newTeam);
        } catch (Exception e) {
            LOGGER.warn("Cannot save team to database: " + newTeam.toString() + " " + e.getMessage());
            event.getMessage().reply("Cannot save a new team right now! Please try again in a couple of seconds.").queue();
            return;
        }

        /*
         * Create a team channel, add its members and assign the correct roles.
         */
        try {
            var channel = event.getMessage().getCategory().createTextChannel(args[1]);
            channel.addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL));
            channel.setTopic(args[0]);

            var docRole = event.getGuild().getRoleById(Dotenv.load().get("PARTICIPANT_ROLE_ID"));

            for (Member member: discordMembers){
                channel.addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE), null);
                if (docRole!=null) {
                    event.getGuild().addRoleToMember(member, docRole).queue();
                }
            }
            channel.queue();
        }
        catch (Exception e){
            LOGGER.error(e.toString());
            return;
        }


        event.getMessage().reply("Team with code: **" + args[0] + "** was successfully created!").queue();
    }

    private boolean validArguments(MessageReceivedEvent event, String[] args) {
        if(args[0].length() != 5) { // validate team code
            event.getMessage().reply("Invalid team code!").queue();
            return false;
        }

        for(int i=2;i<args.length;i+=2) { // validate discord names
            if(!validDiscordName(args[i])) {
                event.getMessage().reply("Invalid Discord name for player **" + args[i] + "**!").queue();
                return false;
            }
        }

        for(int i=3;i<args.length;i+=2) { // validate discord id's
            if(!validDiscordId(args[i])) {
                event.getMessage().reply("Invalid Discord ID for player **" + args[i-1] + "**!").queue();
                return false;
            }
        }

        return true;
    }

    private boolean validDiscordName(String name) {
        Pattern pattern = Pattern.compile(".{2,32}#\\d{4}");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean validDiscordId(String id) {
        Pattern pattern = Pattern.compile("\\d{18}");
        Matcher matcher = pattern.matcher(id);
        return matcher.matches();
    }
}
