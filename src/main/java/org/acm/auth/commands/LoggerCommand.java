package org.acm.auth.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(LoggerCommand.class);
    private static final String[] AVAILABLE_LOGGING_LEVELS = new String[] {
            "trace", "debug", "info", "warn", "error", "fatal"
    };


    public LoggerCommand() {
        super("logger", // name
                "Sets the logger level", // description
                false,  // guildOnly
                true,  // devOnly
                new String[] {},  // alias
                0,  // minArgs
                1,  // maxArgs
                "(level)", // usage
                EMPTY_PERMS, // botPerms
                EMPTY_PERMS); // usrPerms
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        if(args.length == 0) {
            String level = LogManager.getRootLogger().getLevel().name();
            event.getChannel().sendMessage(String.format("The logger level is **%s**", level)).queue();
        } else {
            Level level = Level.getLevel(args[0].toUpperCase());
            String reaction;
            if(level == null) {
                reaction = "\u274c";
                event.getChannel().sendMessage("Available logging levels: " + String.join(" ", AVAILABLE_LOGGING_LEVELS)).queue();
            } else {
                LOGGER.info("Setting the logger level to {}", level.name());
                Configurator.setRootLevel(level);
                reaction = "\u2705";
            }
            event.getMessage().addReaction(reaction).queue();
        }
    }
}
