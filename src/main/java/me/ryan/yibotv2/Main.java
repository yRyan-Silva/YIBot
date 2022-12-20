package me.ryan.yibotv2;

import lombok.Getter;
import me.ryan.yibotv2.command.manager.CommandExecutor;
import me.ryan.yibotv2.command.manager.CommandManager;
import me.ryan.yibotv2.command.manager.CommandRegistry;
import me.ryan.yibotv2.command.sound.CommandPlay;
import me.ryan.yibotv2.command.sound.CommandQueue;
import me.ryan.yibotv2.command.sound.CommandSkip;
import me.ryan.yibotv2.listeners.GuildVoiceLeaveListener;
import me.ryan.yibotv2.music.PlayerManager;
import me.ryan.yibotv2.music.PlayerRegistry;
import me.ryan.yibotv2.utils.Services;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.ArrayList;
import java.util.List;

public class Main {

    @Getter
    private static JDA jda;

    private static final String TOKEN = "TOKEN_BOT";

    public static void main(String[] args) {
        try {
            jda = JDABuilder.createDefault(TOKEN)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setup();
    }

    private static void setup() {
        registerService(PlayerRegistry.class, new PlayerRegistry());
        registerService(CommandRegistry.class, new CommandRegistry());
        registerService(PlayerManager.class, new PlayerManager());

        registerListeners(new CommandManager(), new GuildVoiceLeaveListener());

        registerCommand(new CommandPlay(), new CommandSkip(), new CommandQueue());
    }

    private static <T> void registerService(Class<T> clazz, T instance) {
        Services.register(clazz, instance);
    }

    private static void registerCommand(CommandExecutor... commands) {
        Services.load(CommandRegistry.class).register(commands);
        List<CommandData> commandDataList = new ArrayList<>();
        for (CommandExecutor executor : commands) {
            for (CommandData data : executor.getCommandData()) {
                commandDataList.add(data);
                System.out.println("Command '" + data.getName() + "' successfully registered.");
            }
        }
        jda.getGuilds().forEach(guild -> guild.updateCommands().addCommands(commandDataList).queue());
    }

    private static void registerListeners(ListenerAdapter... listenerAdapters) {
        for (ListenerAdapter listenerAdapter : listenerAdapters) jda.addEventListener(listenerAdapter);
    }

}
