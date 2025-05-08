package de.rechergg.polusprime.services.twitch;

import com.github.twitch4j.helix.domain.Stream;
import de.rechergg.polusprime.PolusPrime;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ChannelLiveListener {

    private final PolusPrime polusPrime;
    private final HashMap<String, StreamInfo> streamInfoMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public ChannelLiveListener(PolusPrime polusPrime) {
        this.polusPrime = polusPrime;

        onChannelGoLive();
        onChannelGoOffline();
    }

    public void onChannelGoLive() {
        this.polusPrime.twitchService().onChannelGoLive("httxmarco", event -> {
            var user = this.polusPrime.twitchService().getUsersById(List.of(event.getChannel().getId()));
            var stream = event.getStream();
            var titel = stream.getTitle();
            var channelName = stream.getUserName();
            var channelId = user.getFirst().getId();
            var profileIcon = user.getFirst().getProfileImageUrl();
            var thumbnail = stream.getThumbnailUrl(1280, 720);
            var streamUrl = "https://www.twitch.tv/" + stream.getUserLogin();
            var gameName = stream.getGameName();
            var viewerCount = stream.getViewerCount();
            var startTime = stream.getStartedAtInstant();

            var uptimeFormatted = formattedUptime(stream.getUptime());

            var streamInfo = new StreamInfo(
                    titel,
                    channelName,
                    channelId,
                    profileIcon,
                    uptimeFormatted,
                    thumbnail,
                    streamUrl,
                    gameName,
                    viewerCount,
                    startTime
            );


            this.polusPrime.discordService().liveNotification().postLiveNotification(streamInfo);

            var task = this.scheduler.scheduleAtFixedRate(() -> {
                var fetchedStream = fetchCurrentStream(channelId);
                if (fetchedStream == null) return;

                var schedulerStreamInfo = new StreamInfo(
                        fetchedStream.getTitle(),
                        fetchedStream.getUserName(),
                        user.getFirst().getId(),
                        user.getFirst().getProfileImageUrl(),
                        formattedUptime(fetchedStream.getUptime()),
                        fetchedStream.getThumbnailUrl(1280, 720),
                        streamUrl,
                        fetchedStream.getGameName(),
                        fetchedStream.getViewerCount(),
                        fetchedStream.getStartedAtInstant()
                );

                this.streamInfoMap.put(user.getFirst().getId(), schedulerStreamInfo);
                this.polusPrime.discordService().liveNotification().updateLiveNotification(schedulerStreamInfo);
            },1, 3, TimeUnit.MINUTES);

            this.scheduledTasks.put(channelId, task);
        });
    }

    public void onChannelGoOffline() {
        this.polusPrime.twitchService().onChannelGoOffline("httxmarco", event -> {
            var user = this.polusPrime.twitchService().getUsersById(List.of(event.getChannel().getId()));
            this.polusPrime.discordService().liveNotification().updateLiveToVODNotification(this.streamInfoMap.get(user.getFirst().getId()));
            this.streamInfoMap.remove(user.getFirst().getId());

            var task = this.scheduledTasks.remove(user.getFirst().getId());
            if (task != null) {
                task.cancel(false);
            }
        });
    }

    private Stream fetchCurrentStream(String channelId) {
        var streams = this.polusPrime.twitchService().client().getHelix()
                .getStreams(null, null, null, null, null, null, null, List.of(channelId), null)
                .execute().getStreams();
        return streams.isEmpty() ? null : streams.getFirst();
    }

    private @NotNull String formattedUptime(Duration uptime) {
        long hours = uptime.toHours();
        long minutes = uptime.toMinutes() % 60;

        if (hours > 0) {
            return String.format("%dh %dmin", hours, minutes);
        }

        if (minutes <= 1) {
            return "einer Minute";
        }

        return String.format("%d Minuten", minutes);
    }
}
