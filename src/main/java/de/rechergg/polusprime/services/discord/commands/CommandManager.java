package de.rechergg.polusprime.services.discord.commands;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandManager {

    void registerCommand(@NotNull Command command);

    void invoke();

    List<Command> commands();
}
