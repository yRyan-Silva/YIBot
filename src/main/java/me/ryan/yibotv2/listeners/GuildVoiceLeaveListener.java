package me.ryan.yibotv2.listeners;

import me.ryan.yibotv2.music.PlayerManager;
import me.ryan.yibotv2.music.TrackScheduler;
import me.ryan.yibotv2.utils.Services;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildVoiceLeaveListener extends ListenerAdapter {

    private final PlayerManager PLAYER_MANAGER = Services.load(PlayerManager.class);

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent e) {

        Member jdaMember = e.getGuild().getMemberById(e.getJDA().getSelfUser().getId());
        TrackScheduler trackScheduler = PLAYER_MANAGER.getTrackScheduler(e.getGuild());
        if (e.getChannelJoined() == null && e.getChannelLeft() != null) {

            if (jdaMember.equals(e.getMember()))
                PLAYER_MANAGER.getGuildMusicManager(e.getGuild()).getPlayer().destroy();
            else {
                AudioChannelUnion channel = jdaMember.getVoiceState().getChannel();
                if (e.getChannelLeft().equals(channel) && channel.getMembers().size() <= 1)
                    PLAYER_MANAGER.getTrackScheduler(e.getGuild()).startDisconnect();
            }

        } else if (e.getChannelJoined() != null && !e.getMember().getUser().isBot() &&
                e.getChannelJoined().equals(jdaMember.getVoiceState().getChannel()) &&
                trackScheduler.getPlayer().getPlayingTrack() != null) {
            trackScheduler.getTimer().cancel();
        }

    }

}
