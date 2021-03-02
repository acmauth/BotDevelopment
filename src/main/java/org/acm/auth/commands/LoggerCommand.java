package org.acm.auth.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(LoggerCommand.class);
    private static final Permission[] EMPTY_PERMS = {};

    public LoggerCommand() {
        super("logger", "Sets the logger level", false, true, new String[]{}, 0, 1, "(level)", EMPTY_PERMS, EMPTY_PERMS);
    }

    @Override
    public void invoke(MessageReceivedEvent event, String[] args) {
        if (args.length == 0) {
            String level = LogManager.getRootLogger().getLevel().name();
            event.getChannel().sendMessage(String.format("The logger level is **%s**", level)).queue();
        } else {
            Level level = Level.getLevel(args[0].toUpperCase());
            String reaction;
            if (level == null) {
                reaction = "\u274c";
            } else {
                LOGGER.info("Setting the logger level to {}", level.name());
                Configurator.setRootLevel(level);
                reaction = "\u2705";
            }
            event.getMessage().addReaction(reaction).queue();
        }
    }
}
