package de.rechergg.polusprime.services.discord.notification;

import de.rechergg.polusprime.services.twitch.StreamInfo;

import java.time.Duration;

public interface DiscordLiveNotification {

    void postLiveNotification(StreamInfo streamInfo);

    void updateLiveNotification(StreamInfo streamInfo);
    void updateLiveToVODNotification(StreamInfo streamInfo);
}
