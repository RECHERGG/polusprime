package de.rechergg.polusprime.services.discord.commands;

import de.rechergg.polusprime.PolusPrime;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManagerImpl implements CommandManager {

    private final PolusPrime polusPrime;
    private final List<Command> commands;

    public CommandManagerImpl(PolusPrime polusPrime) {
        this.polusPrime = polusPrime;
        this.commands = new ArrayList<>();
    }

    @Override
    public void registerCommand(@NotNull Command command) {
        this.commands.add(command);
    }

    @Override
    public void invoke() {
        var update = this.polusPrime.discordService().bot().updateCommands();
        for (Command command : this.commands) {
            update = update.addCommands(command.data());
        }

        update.queue();
    }

    @Override
    public List<Command> commands() {
        return List.of();
    }
}
