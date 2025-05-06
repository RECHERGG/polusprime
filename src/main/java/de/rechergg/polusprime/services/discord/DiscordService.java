package de.rechergg.polusprime.services.discord;

import de.rechergg.polusprime.services.discord.commands.CommandManager;
import net.dv8tion.jda.api.JDA;

public interface DiscordService {

    void initialize();

    JDA bot();

    CommandManager commandManager();

}
