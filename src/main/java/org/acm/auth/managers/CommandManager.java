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

/**
 * Represents a command manager that acts as a command registry as well as an authorization service.
 */
public class CommandManager extends ListenerAdapter {
    private static final Logger LOGGER = LogManager.getLogger(CommandManager.class);

    private final String prefix;
    private final String devId;
    private final Map<String, Command> commands;

    /**
     * Constructs a command manager with the specified prefix
     * @param configFile the config file for the supported commands as {@link ConfigFile}
     */
    public CommandManager(ConfigFile configFile) {
        this.prefix = configFile.getValue(ConfigKey.PREFIX);
        this.devId = configFile.getValue(ConfigKey.DEV_ID);
        this.commands = new HashMap<>();
        loadCommands(configFile);
    }

    /**
     * Registers all possible commands the bot supports
     */
    private void loadCommands(ConfigFile configFile) {
        // register all commands
        Command[] commandsArray = {
                new HiCommand(),
                new ByeCommand(),
                new GifCommand(configFile.getValue(ConfigKey.GIPHY_TOKEN)),
                new LoggerCommand(),
                new SubmitFunctionCommand(),
        };


        for (Command command : commandsArray) {
            // add the label for each command and its aliases to the map
            String name = command.getName();
            LOGGER.info("Loading command {}", name);
            commands.put(name, command);

            for (String alias : command.getAlias()) {
                commands.put(alias, command);
            }
        }
    }

    /**
     * Returns whether the specified user is missing any of the specified permissions
     * @param member the user we want to check whether he misses permissions
     * @param permissions the permissions that the user needs to be authorized with
     * @param tc the text channel in case of non guild-level permissions
     * @return whether the specified user is missing any of the specified permissions as {@code boolean}
     */
    private boolean isMissingPerms(Member member, Permission[] permissions, TextChannel tc) {
        return !Arrays.stream(permissions).allMatch(p -> // check if the following condition is true for all items in the array
                p.isGuild()                              // is the permission a guild-level one?
                ? member.hasPermission(p)                // if so (e.g. BAN_MEMBERS), does the user have it globally?
                : member.hasPermission(tc, p));          // if so (e.g. BAN_MEMBERS), does the user have it globally?
    }

    /**
     * @see ListenerAdapter
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // do not respond to bots
        if(event.getAuthor().isBot())
            return;

        String message = event.getMessage().getContentRaw().toLowerCase();
        if(!message.startsWith(prefix))
            return; // ignore any message that does not start with the specified prefix

        String[] tokens = message.split("\\s+");        // split user's raw message on any whitespace character (space, tab, newline)
        String label = tokens[0].substring(prefix.length());  // command's label is the first token if the prefix gets removed
        Command command = commands.get(label);                // find the corresponding Command object in the map with the registered commands

        // ignore unknown commands
        if(command == null)
            return;

        MessageChannel channel = event.getChannel();

        // if the command was not executed at the right context ( e.g. the command was designed as guild-only ) return
        if(!isExecutedAtRightContext(event, command, channel))
            return;

        int argsCount = tokens.length - 1;
        // user supplied wrong amount of arguments
        if(!validNumberOfArgumentsProvided(argsCount, command, channel))
            return;

        // user or bot has not the required permissions
        if(!authorizedPermissions(event, command, channel))
            return;

        if(command.isDevOnly() && !event.getAuthor().getId().equals(this.devId)) {
            LOGGER.warn("{} tried to access {}", event.getAuthor().getAsTag(), command.getName());
            return;
        }

        // correct amount of arguments and authorized permissions, invoke the command
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        LOGGER.trace("Executing {} with args with \"{}\"", command.getName(), String.join("", args));
        command.invoke(event, args);
    }

    /**
     * Returns whether the command was executed in the specified channel with valid amount of arguments
     * @param argsCount the amount of arguments the user supplied
     * @param command the {@link Command} object that corresponds to the name of the command the user supplied
     * @param channel the channel in which the command was executed
     * @return whether the command was executed in the specified channel with valid amount of arguments
     */
    private boolean validNumberOfArgumentsProvided(int argsCount, Command command, MessageChannel channel) {
        // user supplied less arguments than required
        if(argsCount < command.getMinArgs()) {
            channel.sendMessage("Insufficient arguments!").queue();
            return false;
        }

        // user supplied more arguments than allowed
        if(argsCount > command.getMaxArgs()) {
            channel.sendMessage("Too many arguments!").queue();
            return false;
        }

        return true;
    }

    /**
     * Returns whether the bot and the user have the required permissions
     * @param event the {@link MessageReceivedEvent} object generated by JDA
     * @param command the {@link Command} object that corresponds to the name of the command the user supplied
     * @param channel the channel in which the command was executed
     * @return whether the bot and the user have the required permissions as {@code boolean}
     */
    private boolean authorizedPermissions(MessageReceivedEvent event, Command command, MessageChannel channel) {
        // permissions could only be missing in guild events,
        // since the permissions in private message events
        // are always the same and known in advance
        if(command.isGuildOnly()) {
            if(isMissingPerms(event.getGuild().getSelfMember(), command.getBotPerms(), event.getTextChannel())) {
                // the bot (self-member) is missing at least one of the required bot perms
                channel.sendMessage("Bot is missing permissions!").queue();
                return false;
            }

            if(isMissingPerms(event.getMember(), command.getUsrPerms(), event.getTextChannel())) {
                // the command author (member) is missing at least one of the required user perms
                channel.sendMessage("You are missing permissions!").queue();
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the command was executed at the right context
     * @param event the {@link MessageReceivedEvent} object generated by JDA
     * @param command the {@link Command} object that corresponds to the name of the command the user supplied
     * @param channel the channel in which the command was executed
     * @return whether the command was executed at the right context as {@code boolean}
     */
    private boolean isExecutedAtRightContext(MessageReceivedEvent event, Command command, MessageChannel channel) {
        if(command.isGuildOnly() && !event.isFromGuild()) {
            // command is designed as guild-only
            // but the specified event didn't occur in a guild
            channel.sendMessage("This is a server-only command!").queue();
            return false;
        }

        return true;
    }
}
