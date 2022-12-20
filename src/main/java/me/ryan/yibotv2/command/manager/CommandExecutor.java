package me.ryan.yibotv2.command.manager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface CommandExecutor {

    void process(SlashCommandInteractionEvent e);

    CommandData[] getCommandData();

}
