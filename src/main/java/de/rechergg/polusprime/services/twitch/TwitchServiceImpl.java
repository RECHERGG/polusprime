package de.rechergg.polusprime.services.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TwitchServiceImpl implements TwitchService {

    private TwitchClient client;

    public TwitchServiceImpl(Dotenv dotenv) {
        log.info("Initializing TwitchService...");

        var clientId = dotenv.get("TWITCH_CLIENT_ID"); // TODO ony for testing purpose
        var clientSecret = dotenv.get("TWITCH_CLIENT_SECRET"); // TODO ony for testing purpose

        if (clientId == null || clientSecret == null) {
            log.fatal("Twitch client ID or secret is not set.");
            System.exit(-1);
            return;
        }

        this.client = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableHelix(true)
                .build();

        checkClientInitialized();
    }

    private void checkClientInitialized() {
        if (this.client != null && this.client.getHelix() != null) {
            log.info("TwitchService initialized successfully.");
            return;
        }

        log.error("Failed to initialize TwitchService.");
    }

    @Override
    public TwitchClient client() {
        return this.client;
    }
}
