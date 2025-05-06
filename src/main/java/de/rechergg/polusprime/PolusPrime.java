package de.rechergg.polusprime;

import de.rechergg.polusprime.services.discord.DiscordService;
import de.rechergg.polusprime.services.discord.DiscordServiceImpl;
import de.rechergg.polusprime.services.twitch.TwitchService;
import de.rechergg.polusprime.services.twitch.TwitchServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Accessors(fluent = true)
public class PolusPrime {

    private final TwitchService twitchService;
    private final DiscordService discordService;

    public PolusPrime() {
        log.info("Starting PolusPrime...");

        var dotenv = Dotenv.load();

        this.twitchService = new TwitchServiceImpl(dotenv);
        this.discordService = new DiscordServiceImpl(this, dotenv);

        log.info("PolusPrime started successfully.");
    }

    /**
     *         log.trace("Dies ist eine TRACE-Nachricht");
     *         log.debug("Dies ist eine DEBUG-Nachricht");
     *         log.info("Dies ist eine INFO-Nachricht");
     *         log.warn("Dies ist eine WARN-Nachricht");
     *         log.error("Dies ist eine ERROR-Nachricht");
     *         log.fatal("Dies ist eine FATAL-Nachricht");
     */

}
