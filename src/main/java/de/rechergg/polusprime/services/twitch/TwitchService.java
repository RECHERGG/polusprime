package de.rechergg.polusprime.services.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.User;

import java.util.List;
import java.util.function.Consumer;

public interface TwitchService {

    /**
     * Get the Twitch client instance.
     * @return configured TwitchClient
     */
    TwitchClient client();

    /**
     * Register a callback for when a channel goes live.
     * @param channelName the channel to monitor
     * @param callback function to execute when the channel goes live
     */
    void onChannelGoLive(String channelName, Consumer<ChannelGoLiveEvent> callback);

    /**
     * Register a callback for when a channel goes offline.
     * @param channelName the channel to monitor
     * @param callback function to execute when the channel goes offline
     */
    void onChannelGoOffline(String channelName, Consumer<ChannelGoOfflineEvent> callback);

    /**
     * Get user information by username(s)
     * @param usernames list of usernames to get information
     * @return list of User objects
     */
    List<User> getUsersByUsername(List<String> usernames);

    /**
     * Get user information by user ID(s)
     * @param userIds list of user IDs to get information
     * @return list of User objects
     */
    List<User> getUsersById(List<String> userIds);

    /**
     * Start monitoring a channel for events
     * @param channelName the channel name to monitor
     */
    void startMonitoring(String channelName);

    /**
     * Stop monitoring a channel for events
     * @param channelName the channel name to stop monitoring
     */
    void stopMonitoring(String channelName);
}
