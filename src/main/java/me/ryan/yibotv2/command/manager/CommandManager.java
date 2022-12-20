package me.ryan.yibotv2.command.manager;

import me.ryan.yibotv2.utils.Services;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandManager extends ListenerAdapter {

    private final CommandRegistry COMMAND_REGISTRY = Services.load(CommandRegistry.class);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {

        String command = e.getName();
        CommandExecutor commandExecutor = COMMAND_REGISTRY.get(command);
        if (commandExecutor != null) commandExecutor.process(e);

    }

}
