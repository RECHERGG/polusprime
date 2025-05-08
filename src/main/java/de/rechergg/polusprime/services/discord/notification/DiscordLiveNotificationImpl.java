package de.rechergg.polusprime.services.discord.notification;

import de.rechergg.polusprime.services.discord.DiscordService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Instant;
import java.util.HashMap;

public class DiscordLiveNotificationImpl implements DiscordLiveNotification {

    private final DiscordService discordService;
    private final TextChannel textChannel;
    private final HashMap<String, String> messageIdMap = new HashMap<>();

    public DiscordLiveNotificationImpl(DiscordService discordService) {
        this.discordService = discordService;
        this.textChannel = this.discordService.bot().getGuildById("1077232612613034024").getTextChannelById("1363828207253455052");
    }

    @Override
    public void postLiveNotification(String titel, String channelName, String channelId, String channelProfileIcon, String uptime, String thumbnailUrl, String streamUrl, String gameName, Integer viewersCount) {
        this.textChannel.sendMessage("@everyone").addEmbeds(buildNotificationEmbed(
                channelName + " ist live auf Twitch!",
                channelProfileIcon,
                titel,
                "Online seit " + uptime + " | Zuletzt aktualisiert",
                streamUrl,
                thumbnailUrl,
                "Game", gameName,
                "Viewers", viewersCount.toString(),
                null,
                null
        )).queue(it -> this.messageIdMap.put(channelId, it.getId()));
    }

    @Override
    public void updateLiveNotification(String titel, String channelName, String channelId, String channelProfileIcon, String uptime, String thumbnailUrl, String streamUrl, String gameName, Integer viewersCount) {
        this.textChannel.editMessageEmbedsById(this.messageIdMap.get(channelId), buildNotificationEmbed(
                channelName + " ist live auf Twitch!",
                channelProfileIcon,
                titel,
                "Online seit " + uptime + " | Zuletzt aktualisiert",
                streamUrl,
                thumbnailUrl,
                "Game", gameName,
                "Viewers", viewersCount.toString(),
                null,
                null
        )).queue();
    }

    @Override
    public void updateLiveToVODNotification(String titel, String channelName, String thumbnailUrl, String streamUrl, String gameName, String gameUrl, String gameThumbnailUrl, String description, String messageId) {

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

        builder.setImage(thumbnailUrl);
        builder.setColor(0x9146FF);

        builder.setFooter(footer, "https://images.icon-icons.com/3041/PNG/512/twitch_logo_icon_189242.png");
        builder.setTimestamp(Instant.now());

        return builder.build();
    }
}
