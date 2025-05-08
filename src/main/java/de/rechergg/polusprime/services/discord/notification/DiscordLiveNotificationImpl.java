package de.rechergg.polusprime.services.discord.notification;

import de.rechergg.polusprime.PolusPrime;
import de.rechergg.polusprime.services.discord.DiscordService;
import de.rechergg.polusprime.services.twitch.StreamInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Instant;
import java.util.HashMap;

public class DiscordLiveNotificationImpl implements DiscordLiveNotification {

    private final PolusPrime polusPrime;
    private final TextChannel textChannel;
    private final HashMap<String, String> messageIdMap = new HashMap<>();

    public DiscordLiveNotificationImpl(PolusPrime polusPrime, DiscordService discordService) {
        this.polusPrime = polusPrime;
        this.textChannel = discordService.bot().getGuildById("1033676465314738196").getTextChannelById("1033687781072977940");
    }

    @Override
    public void postLiveNotification(StreamInfo streamInfo) {
        this.textChannel.sendMessage("@everyone").addEmbeds(buildNotificationEmbed(
                streamInfo.channelName() + " ist live auf Twitch!",
                streamInfo.channelProfileIcon(),
                streamInfo.titel(),
                "Online seit " + streamInfo.uptime() + " | Zuletzt aktualisiert",
                streamInfo.streamUrl(),
                streamInfo.thumbnailUrl(),
                "🎮 Game", streamInfo.gameName(),
                "👀 Viewers", streamInfo.viewersCount().toString(),
                null,
                null
        )).queue(it -> this.messageIdMap.put(streamInfo.channelId(), it.getId()));
    }

    @Override
    public void updateLiveNotification(StreamInfo streamInfo) {
        this.textChannel.editMessageEmbedsById(this.messageIdMap.get(streamInfo.channelId()), buildNotificationEmbed(
                streamInfo.channelName() + " ist live auf Twitch!",
                streamInfo.channelProfileIcon(),
                streamInfo.titel(),
                "Online seit " + streamInfo.uptime() + " | Zuletzt aktualisiert",
                streamInfo.streamUrl(),
                streamInfo.thumbnailUrl(),
                "🎮 Game", streamInfo.gameName(),
                "👀 Viewers", streamInfo.viewersCount().toString(),
                null,
                null
        )).queue();
    }

    @Override
    public void updateLiveToVODNotification(StreamInfo streamInfo) {
        var vod = this.polusPrime.twitchService().fetchVOD(streamInfo.channelId(), streamInfo.streamStartTime());

        this.textChannel.editMessageEmbedsById(this.messageIdMap.get(streamInfo.channelId()), buildNotificationEmbed(
                streamInfo.channelName() + " war live auf Twitch!",
                streamInfo.channelProfileIcon(),
                streamInfo.titel(),
                "Online für " + streamInfo.uptime() + " | Offline gegangen",
                streamInfo.streamUrl(),
                null,
                "🎮 Game", streamInfo.gameName(),
                vod == null ? null : "📺 Verpasst? Wiederholung:",
                vod == null ? null : "**[Klick](" + vod + ")**",
                "Nächstes mal dabei?",
                "Folge [hier](https://twitch.tv/" + streamInfo.channelName() + ")"
        )).queue();
    }

    private MessageEmbed buildNotificationEmbed(String author, String channelProfileIcon, String titel, String footer, String streamUrl, String thumbnailUrl, String fieldOne, String fieldOneValue, String fieldTwo, String fieldTwoValue, String fieldThree, String fieldThreeValue) {
        var builder = new EmbedBuilder();

        builder.setAuthor(author, null, channelProfileIcon);
        builder.setTitle(titel, streamUrl);

        builder.addField(fieldOne, fieldOneValue, true);
        builder.addField(fieldTwo, fieldTwoValue, true);

        if (fieldThree != null && fieldThreeValue != null) {
            builder.addField(fieldThree, fieldThreeValue, true);
        }

        if (thumbnailUrl != null) {
            builder.setImage(thumbnailUrl);
        }

        builder.setColor(0x9146FF);

        builder.setFooter(footer, "https://images.icon-icons.com/3041/PNG/512/twitch_logo_icon_189242.png");
        builder.setTimestamp(Instant.now());

        return builder.build();
    }
}
