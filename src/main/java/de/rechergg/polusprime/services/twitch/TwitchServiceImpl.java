package de.rechergg.polusprime.services.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.Video;
import de.rechergg.polusprime.PolusPrime;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Log4j2
public class TwitchServiceImpl implements TwitchService {

    private TwitchClient client;
    private final Map<String, String> channelIdMap = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<ChannelGoLiveEvent>>> liveListeners = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<ChannelGoOfflineEvent>>> offlineListeners = new ConcurrentHashMap<>();

    public TwitchServiceImpl(PolusPrime polusPrime, Dotenv dotenv) {
        log.info("Initializing Twitch service...");

        var clientId = dotenv.get("TWITCH_CLIENT_ID"); // TODO ony for testing purpose
        var clientSecret = dotenv.get("TWITCH_CLIENT_SECRET"); // TODO ony for testing purpose
        var accessToken = dotenv.get("TWITCH_ACCESS_TOKEN");

        if (clientId == null || clientSecret == null) {
            log.fatal("Twitch client ID or secret is not set.");
            System.exit(-1);
            return;
        }

        this.client = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withDefaultAuthToken(new OAuth2Credential(clientId, accessToken))
                .withClientSecret(clientSecret)
                .withEnableEventSocket(true)
                .withEnableChat(false)
                .withEnableHelix(true)
                .build();

        registerEventHandlers();
        log.info("Twitch service initialized successfully.");
    }

    private void registerEventHandlers() {
        // Register event handlers for go live events
        this.client.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            var channelName = event.getChannel().getName().toLowerCase();
            this.liveListeners.getOrDefault(channelName, List.of()).forEach(callback -> callback.accept(event));
        });

        // Register event handlers for go offline events
        this.client.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> {
            var channelName = event.getChannel().getName().toLowerCase();
            this.offlineListeners.getOrDefault(channelName, List.of()).forEach(callback -> callback.accept(event));
        });
    }

    @Override
    public TwitchClient client() {
        return this.client;
    }

    @Override
    public void onChannelGoLive(String channelName, Consumer<ChannelGoLiveEvent> callback) {
        this.liveListeners.computeIfAbsent(channelName.toLowerCase(), k -> new ArrayList<>()).add(callback);
        monitorChannel(channelName);
    }

    @Override
    public void onChannelGoOffline(String channelName, Consumer<ChannelGoOfflineEvent> callback) {
        this.offlineListeners.computeIfAbsent(channelName.toLowerCase(), k -> new ArrayList<>()).add(callback);
        monitorChannel(channelName);
    }

    private void monitorChannel(String channelName) {
        if (this.channelIdMap.containsKey(channelName)) return;
        var users = getUsersByUsername(Collections.singletonList(channelName));

        if (users.isEmpty()) {
            log.warn("Could not find user ID for channel: {}", channelName);
            return;
        }

        var channelId = users.getFirst().getId();
        this.channelIdMap.put(channelName, channelId);
        this.client.getClientHelper().enableStreamEventListener(channelName);
        log.info("Started monitoring channel: {}", channelName);
    }

    @Override
    public List<User> getUsersByUsername(List<String> usernames) {
        log.debug("Fetching users by username: {}", usernames);
        try {
            return this.client.getHelix().getUsers(null, null, usernames).execute().getUsers();
        } catch (Exception e) {
            log.error("Error fetching users by username", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<User> getUsersById(List<String> userIds) {
        try {
            return this.client.getHelix().getUsers(null, userIds, null).execute().getUsers();
        } catch (Exception e) {
            log.error("Error fetching users by ID", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void startMonitoring(String channelName) {
        monitorChannel(channelName);
    }

    @Override
    public void stopMonitoring(String channelName) {
        if (!this.channelIdMap.containsKey(channelName)) {
            return;
        }

        this.client.getClientHelper().disableStreamEventListener(channelName);
        this.channelIdMap.remove(channelName);
        log.info("Stopped monitoring channel: {}", channelName);
    }

    @Override
    public String fetchVOD(String channelId, Instant streamStartTime) {
        var videos = client().getHelix()
                .getVideos(null, null, channelId, null, null, null, null, Video.Type.ARCHIVE, 5, null, null)
                .execute()
                .getVideos();

        if (videos.isEmpty()) return null;

        for (var video : videos) {
            if (!"archive".equals(video.getType())) continue;

            var vodCreated = video.getCreatedAtInstant();
            long minutesDiff = Math.abs(Duration.between(streamStartTime, vodCreated).toMinutes());

            if (minutesDiff <= 10) return video.getUrl();
        }

        return null;
    }
}
