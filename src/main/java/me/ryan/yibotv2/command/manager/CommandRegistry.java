package me.ryan.yibotv2.command.manager;

import me.ryan.yibotv2.utils.Registry;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandRegistry extends Registry<CommandExecutor> {

    public CommandExecutor get(String command) {
        return get($ -> {
            for (CommandData data : $.getCommandData())
                if (data.getName().equalsIgnoreCase(command))
                    return true;
            return false;
        });
    }

}
