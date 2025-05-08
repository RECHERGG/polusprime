package de.rechergg.polusprime.services.discord.notification;

import java.time.Duration;

public interface DiscordLiveNotification {

    void postLiveNotification(String titel, String channelName, String channelId, String channelProfileIcon, String uptime, String thumbnailUrl, String streamUrl, String gameName, Integer viewersCount);

    void updateLiveNotification(String titel, String channelName, String channelId, String channelProfileIcon, String uptime, String thumbnailUrl, String streamUrl, String gameName, Integer viewersCount);
    void updateLiveToVODNotification(String titel, String channelName, String thumbnailUrl, String streamUrl, String gameName, String gameUrl, String gameThumbnailUrl, String description, String messageId);
}
