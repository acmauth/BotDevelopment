package org.acm.auth.managers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.acm.auth.commands.*;
import org.acm.auth.config.ConfigFile;
import org.acm.auth.config.ConfigKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager extends ListenerAdapter  {
    private static final Logger LOGGER = LogManager.getLogger(CommandManager.class);

    private final String prefix;
    private final String devId;
    private final Map<String, Command> commands;

    public CommandManager(ConfigFile config) {
        this.prefix = config.getValue(ConfigKey.PREFIX);
        this.devId = config.getValue(ConfigKey.DEV_ID);
        this.commands = new HashMap<>();
        loadCommands(config);
    }

    private void loadCommands(ConfigFile config) {
        // register all possible cmds
        Command[] cmdArr = {
                new HiCommand(),
                new ByeCommand(),
                new LoggerCommand(),
                new GifCommand(config.getValue(ConfigKey.GIPHY_KEY)),
                new PollCommand(),
                new CointossCommand(),
        };

        // for each command
        for (Command cmd : cmdArr) {
            // add its label to the map
            String name = cmd.getName();
            LOGGER.info("Loading command {}", name);
            commands.put(name, cmd);

            // for each of the command's alias
            for (String alias : cmd.getAlias()) {
                // and it to the map too
                commands.put(alias, cmd);
            }
        }
    }

    private boolean isMissingPerms(Member member, Permission[] permissions, TextChannel tc) {
        return !Arrays.stream(permissions)
                .allMatch(p -> { // check if the following condition is TRUE for ALL items in the array
                    return p.isChannel()                    // is the permission a channel-level one?
                            ? member.hasPermission(tc, p)   // if so (e.g. MANAGE_MESSAGES), does the user have it in
                                                            //                               this exact text channel?
                            : member.hasPermission(p);      // if not (e.g. BAN_MEMBERS), does the user have it globally?
                });
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            // do not respond to bots
            return;
        }

        String message = event.getMessage().getContentRaw().toLowerCase();
        if (!message.startsWith(prefix)) {
            // message doesn't start with our prefix - ignore
            return;
        }

        String[] tokens = message.split("\\s+"); // split on any space char (whitespace, tab, newlines)
        String label = tokens[0].substring(prefix.length()); // the label is the first token if we remove the prefix
        Command cmd = commands.get(label); // find the corresponding command in the map

        if (cmd == null) {
            // unknown command - ignore
            return;
        }

        int argCount = tokens.length - 1;
        MessageChannel channel = event.getChannel();
        if (cmd.isGuildOnly() && !event.isFromGuild()) {
            // command is designated as guild-only
            // but this event didn't occur in a guild
            channel.sendMessage("This is a server-only command!").queue();
            return;
        }
        if (argCount < cmd.getMinArgs()) {
            // user supplied less arguments than required
            channel.sendMessage("Insufficient arguments!").queue();
            return;
        }
        if (argCount > cmd.getMaxArgs()) {
            // user supplied more arguments than allowed
            channel.sendMessage("Too many arguments!").queue();
            return;
        }
        if (cmd.isGuildOnly()) {
            // permissions could only be missing in guild events,
            // since the permissions in private message events
            // are always the same and known in advance
            if (isMissingPerms(event.getGuild().getSelfMember(), cmd.getBotPerms(), event.getTextChannel())) {
                // the bot (self-member) is missing at least one of the required bot perms
                channel.sendMessage("Bot is missing permissions!").queue();
                return;
            }
            if (isMissingPerms(event.getMember(), cmd.getUsrPerms(), event.getTextChannel())) {
                // the command author (member) is missing at least one of the required user perms
                channel.sendMessage("You are missing permissions!").queue();
                return;
            }
        }
        if (cmd.isDevOnly() && !event.getAuthor().getId().equals(this.devId)) {
            LOGGER.warn("{} tried to access {}", event.getAuthor().getAsTag(), cmd.getName());
            return;
        }

        // we don't need to supply the first token (which is the cmd label)
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        LOGGER.trace("Executing {} with args \"{}\"", cmd.getName(), String.join("", args));
        cmd.invoke(event, args); // execute the command!
    }
}
