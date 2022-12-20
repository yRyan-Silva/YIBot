package me.ryan.yibotv2.command.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.ryan.yibotv2.command.manager.CommandExecutor;
import me.ryan.yibotv2.music.PlayerManager;
import me.ryan.yibotv2.music.TrackScheduler;
import me.ryan.yibotv2.utils.EmbedApi;
import me.ryan.yibotv2.utils.Services;
import me.ryan.yibotv2.utils.TimersApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandQueue implements CommandExecutor {

    private final PlayerManager PLAYER_MANAGER = Services.load(PlayerManager.class);

    @Override
    public void process(SlashCommandInteractionEvent e) {

        Guild guild = e.getGuild();
        Member jdaMember = guild.getMemberById(e.getJDA().getSelfUser().getId());
        TrackScheduler trackScheduler = PLAYER_MANAGER.getTrackScheduler(guild);
        AudioPlayer player = trackScheduler.getPlayer();
        AudioTrack firstTrack = player.getPlayingTrack();

        if (jdaMember.getVoiceState().inAudioChannel() && player.getPlayingTrack() != null) {
            StringBuilder description = new StringBuilder("__Tocando agora__: \n");
            description.append("[**" + firstTrack.getInfo().title + "**](" + firstTrack.getInfo().uri + ") | " +
                    TimersApi.convertMillis(firstTrack.getPosition()) + "/" +
                    TimersApi.convertMillis(firstTrack.getDuration()) + ".\n");

            e.replyEmbeds(EmbedApi.SUCCESS.getMessage(guild.getName(), description.toString())).queue();
        } else
            e.replyEmbeds(EmbedApi.ERROR.getMessage("Eu não estou tocando nenhuma música neste momento.")).queue();

    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[]{Commands.slash("queue", "Ver a lista de músicas."),
                Commands.slash("lista", "Ver a lista de músicas.")};
    }

}
