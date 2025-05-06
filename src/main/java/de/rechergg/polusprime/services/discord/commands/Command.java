package de.rechergg.polusprime.services.discord.commands;

import de.rechergg.polusprime.PolusPrime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public abstract class Command {
    private final PolusPrime polusPrime;

    public abstract CommandData data();
    public abstract void handle(SlashCommandInteractionEvent context);
}
