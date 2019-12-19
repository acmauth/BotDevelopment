package org.acm.auth;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.acm.auth.cfg.Config;
import org.acm.auth.cmds.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the class that does all the heavy lifting for detecting and invoking the command that was called by a user.
 * We detect if the message we received through the {@link ListenerAdapter} starts with a prefix and a valid label,
 * and then call the {@link Command#execute(MessageReceivedEvent, String[])} method accordingly.
 */
public class CmdRegistry extends ListenerAdapter {
    private Config cfg; // The bot's configuration object
    private String prefix; // The bot's global prefix
    private Map<String, Command> commands; // A map of label -> command, used to invoke the proper command

    public CmdRegistry(Config cfg) {
        this.cfg = cfg;
        this.prefix = cfg.getPrefix();
        registerCommands();
    }

    /**
     * This is where we register all commands that the bot needs to listen to, by adding the proper entry to
     * the commands map, which is then that's queried in the {@link #onMessageReceived(MessageReceivedEvent)} method.
     */
    private void registerCommands() {
        commands = new HashMap<>();
        commands.put("hello", new HelloCommand());
        commands.put("dice", new DiceCommand());
        commands.put("kick", new KickCommand());
        commands.put("joke", new JokeCommand());
        commands.put("dog", new DogCommand());
        commands.put("urban", new UrbanCommand());
        commands.put("youtube", new YtCommand(cfg.getYtKey()));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            // We don't want to reply to other bots, or ourselves!
            return;
        }

        String msg = event.getMessage().getContentRaw();
        if (msg.startsWith(prefix) && !msg.equalsIgnoreCase(prefix)) {
            // The message starts with the prefix, so it might be a command
            String label = msg.substring(prefix.length()); // Drop the prefix from the message
            if (label.contains(" ")) {
                // If there is a space character in the message, get everything before its first occurrence.
                // Whatever is left is essentially the command's label, which identifies the command that was sent.
                label = label.substring(0, label.indexOf(" ")).trim();
            }
            Command cmd = commands.get(label.toLowerCase());
            if (cmd != null) {
                // If the label exists in the commands map, that means that the user entered a valid command.
                if (event.isFromGuild()) {
                    // For Guild events, we need to make sure we have the required permissions.
                    Member self = event.getGuild().getSelfMember();
                    Member member = event.getMember();
                    assert member != null;

                    if (!self.hasPermission(cmd.getBotPerms())) {
                        event.getChannel().sendMessage("Bot doesn't have the required permissions!").queue();
                        return;
                    } else if (!member.hasPermission(cmd.getUsrPerms())) {
                        event.getChannel().sendMessage("You don't have the required permissions!").queue();
                        return;
                    }
                } else {
                    // For non-Guild events, we need to make sure that the command supports private messages.
                    if (cmd.isGuildOnly()) {
                        event.getChannel().sendMessage("This is a guild-only command!").queue();
                        return;
                    }
                }

                String[] input = msg.split("\\s+"); // Split the message on all space characters into an array
                String[] args = Arrays.copyOfRange(input, 1, input.length); // Drop the first element (cmd label)
                cmd.execute(event, args); // Invoke the command's execute() method and let it handle the rest!
            }
        }
    }
}
