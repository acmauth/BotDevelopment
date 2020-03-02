package org.acm.auth.cmds;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * This is the abstract Command class which highlights the structure that all command implementations should have.
 * By extending this class, a new command can be defined, which consists of a set of fields and the execute() method.
 */
public abstract class Command {
    private String name;
    private String description;
    private boolean guildOnly;
    private boolean devOnly;
    private Permission[] botPerms;
    private Permission[] usrPerms;

    Command(String name, String description, boolean guildOnly) {
        this.name = name;
        this.description = description;
        this.guildOnly = guildOnly;
        this.devOnly = false;
        this.botPerms = new Permission[]{};
        this.usrPerms = new Permission[]{};
    }

    Command(String name, String description, boolean guildOnly, boolean devOnly) {
        this.name = name;
        this.description = description;
        this.guildOnly = guildOnly;
        this.devOnly = devOnly;
        this.botPerms = new Permission[]{};
        this.usrPerms = new Permission[]{};
    }

    Command(String name, String description, boolean guildOnly, Permission[] botPerms, Permission[] usrPerms) {
        this.name = name;
        this.description = description;
        this.guildOnly = guildOnly;
        this.devOnly = false;
        this.botPerms = botPerms;
        this.usrPerms = usrPerms;
    }

    /**
     * Returns the name that will show up in help and usage messages.
     * @return the command's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the extensive description to be shown in help messages.
     * @return the command's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this command should only be used in guilds and not in private.
     * @return  true    if the command should only be called in guild channels,
     *          false   if the command can be used both in guild and private channels
     */
    public boolean isGuildOnly() {
        return guildOnly;
    }

    /**
     * Returns whether this command should only be executed by the bot's developer.
     * @return  true    if the command is exclusive to the bot's developer,
     *          false   if the command can be used by anyone
     */
    public boolean isDevOnly() {
        return devOnly;
    }

    /**
     * Returns a list of permissions needed by the bot in order to execute the command.
     * @return an array with the permissions required by the bot
     */
    public Permission[] getBotPerms() {
        return botPerms;
    }

    /**
     * Returns a list of permissions needed by the command issuer in order to execute the command.
     * @return an array with the permissions required by the command issuer
     */
    public Permission[] getUsrPerms() {
        return usrPerms;
    }

    /**
     * This method gets called when the {@link org.acm.auth.CmdRegistry CmdRegistry}
     * detects that the command has been called by a user.
     * @param event The {@link MessageReceivedEvent} event that was fired when this command was called
     * @param args The user's arguments that accompanied this command after its label invocation
     */
    public abstract void execute(MessageReceivedEvent event, String[] args);
}
