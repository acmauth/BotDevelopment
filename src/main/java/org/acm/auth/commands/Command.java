package org.acm.auth.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {
    private final String name;              // the command's name/label
    private final String description;       // a description about what the command does
    private final boolean guildOnly;        // whether this command can only be executed in servers
    private final String[] alias;           // an array with possible alternate labels that invoke the command
    private final int minArgs;              // the minimum amount of arguments required
    private final int maxArgs;              // the maximum amount of arguments allowed
    private final String usage;             // a template for the command's usage, explaining what each parameter means
    private final Permission[] botPerms;    // an array with the required permission that the bot has to have
    private final Permission[] usrPerms;    // an array with the required permission that the user has to have

    // static object for empty perms to be used in the default constructor
    private static final Permission[] EMPTY_PERMS = {};

    public Command(String name, String description, boolean guildOnly, String[] alias) {
        this.name = name;
        this.description = description;
        this.guildOnly = guildOnly;
        this.alias = alias;
        this.minArgs = 0;                   // default: no minimum args
        this.maxArgs = Integer.MAX_VALUE;   // default: infinite maximum args
        this.usage = "";                    // default: no usage (this means the command doesn't need any parameters)
        this.botPerms = EMPTY_PERMS;        // default: no required permissions for the bot
        this.usrPerms = EMPTY_PERMS;        // default: no required permissions for the user
    }

    public Command(String name, String description, boolean guildOnly, String[] alias, int minArgs, int maxArgs, String usage, Permission[] botPerms, Permission[] usrPerms) {
        this.name = name;
        this.description = description;
        this.guildOnly = guildOnly;
        this.alias = alias;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.usage = usage;
        this.botPerms = botPerms;
        this.usrPerms = usrPerms;
    }

    public abstract void invoke(MessageReceivedEvent event, String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public String[] getAlias() {
        return alias;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public String getUsage() {
        return usage;
    }

    public Permission[] getBotPerms() {
        return botPerms;
    }

    public Permission[] getUsrPerms() {
        return usrPerms;
    }
}
