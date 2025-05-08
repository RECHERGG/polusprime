package de.rechergg.polusprime.services.twitch;

import java.time.Instant;

public record StreamInfo(String titel, String channelName, String channelId, String channelProfileIcon, String uptime, String thumbnailUrl, String streamUrl, String gameName, Integer viewersCount, Instant streamStartTime) {

}
