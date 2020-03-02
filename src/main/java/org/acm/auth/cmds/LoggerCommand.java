package org.acm.auth.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerCommand extends Command {
    public static final Logger LOG = LogManager.getLogger();

    public LoggerCommand() {
        super("logger", "Sets the global logger level", false, true);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length != 1) {
            // We need exactly argument to specify the root logger level
            event.getChannel().sendMessage("**Invalid arguments!**").queue();
            return;
        }
        Level lvl = Level.getLevel(args[0].toUpperCase());
        if (lvl == null) {
            // User specified a level literal that doesn't exist
            event.getChannel().sendMessage("**Invalid level!**").queue();
            return;
        }
        Configurator.setRootLevel(lvl);
        LOG.info("Set the logger level to {}", lvl.name());
        event.getMessage().addReaction("\u2705").queue(); // React to indicate success
    }
}
