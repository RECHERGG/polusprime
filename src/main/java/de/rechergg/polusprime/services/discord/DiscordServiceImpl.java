package de.rechergg.polusprime.services.discord;

import de.rechergg.polusprime.PolusPrime;
import de.rechergg.polusprime.services.discord.commands.CommandHandler;
import de.rechergg.polusprime.services.discord.commands.CommandManager;
import de.rechergg.polusprime.services.discord.commands.CommandManagerImpl;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

@Log4j2
public class DiscordServiceImpl implements DiscordService {

    private JDA bot;
    private CommandManager commandManager;

    public DiscordServiceImpl(PolusPrime polusPrime, Dotenv dotenv) {
        log.info("Initializing Discord service...");

        var token = dotenv.get("DISCORD_TOKEN"); // TODO ony for testing purpose

        if (token == null) {
            log.fatal("Discord token is not set.");
            System.exit(-1);
            return;
        }

        var builder = JDABuilder.createDefault(token)
                .addEventListeners(new CommandHandler(polusPrime))
                .setBulkDeleteSplittingEnabled(false)
                .setAutoReconnect(true)
                .build();

        this.commandManager = new CommandManagerImpl(polusPrime);

        log.info("Discord service initialized successfully.");

        try {
            this.bot = builder.awaitReady();
            log.info("Discord bot is online.");
        } catch (InterruptedException e) {
            log.error("Failed to initialize Discord service.", e);
            System.exit(-1);
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public JDA bot() {
        return this.bot;
    }

    @Override
    public CommandManager commandManager() {
        return this.commandManager;
    }
}
