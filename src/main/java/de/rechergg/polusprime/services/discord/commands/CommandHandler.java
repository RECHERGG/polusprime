package de.rechergg.polusprime.services.discord.commands;

import de.rechergg.polusprime.PolusPrime;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@AllArgsConstructor
public class CommandHandler extends ListenerAdapter {

    private final PolusPrime polusPrime;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        this.polusPrime
                .discordService()
                .commandManager()
                .commands()
                .stream()
                .filter(command -> command.data().getName().equalsIgnoreCase(event.getInteraction().getName()))
                .findFirst()
                .ifPresent(command -> command.handle(event));
    }
}
