package me.ryan.yibotv2.music;

import java.awt.*;
import java.util.Objects;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.ryan.yibotv2.utils.EmbedApi;
import me.ryan.yibotv2.utils.Services;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayerManager {

    private final AudioPlayerManager PLAYER_MANAGER;
    private final PlayerRegistry PLAYER_REGISTRY;

    public PlayerManager() {
        PLAYER_REGISTRY = Services.load(PlayerRegistry.class);
        PLAYER_MANAGER = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = PLAYER_REGISTRY.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(PLAYER_MANAGER, guild);
            PLAYER_REGISTRY.register(musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void destroy(Guild guild){
        getGuildMusicManager(guild).getPlayer().destroy();
        getTrackScheduler(guild).getQueue().clear();
    }

    public TrackScheduler getTrackScheduler(Guild guild) {
        return getGuildMusicManager(guild).getScheduler();
    }

    public void loadAndPlay(SlashCommandInteractionEvent e, String trackUrl, VoiceChannel voiceChannel) {
        GuildMusicManager musicManager = getGuildMusicManager(Objects.requireNonNull(e.getGuild()));
        PLAYER_MANAGER.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                AudioManager audio = voiceChannel.getManager().getGuild().getAudioManager();
                audio.openAudioConnection(voiceChannel);
                audio.setSelfDeafened(true);

                MessageEmbed embed = new EmbedBuilder().setDescription(":headphones: Música adicionada: [**" + track.getInfo().title + "**](" + trackUrl + ")!")
                        .setColor(Color.blue).build();
                e.replyEmbeds(embed).queue((success) -> {
                        },
                        (error) -> e.getChannel().sendMessageEmbeds(embed).queue());
                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioManager audio = voiceChannel.getManager().getGuild().getAudioManager();
                audio.openAudioConnection(voiceChannel);
                audio.setSelfDeafened(true);
                AudioTrack firstTrack = playlist.getSelectedTrack() == null ?
                        playlist.getTracks().get(0) : playlist.getSelectedTrack();

                if (playlist.getTracks().isEmpty()) {
                    MessageEmbed embed = EmbedApi.ERROR
                            .getMessage(":headphones: Não foi possível carregar esta playlist.");

                    e.replyEmbeds(embed).queue((success) -> {
                            },
                            (error) -> e.getChannel().sendMessageEmbeds(embed).queue());
                    return;
                }

                MessageEmbed embed = EmbedApi.SUCCESS.getMessage(":headphones: Adicionado na fila: **" +
                        playlist.getTracks().size() + "** músicas! - " +
                        "Primeira música da playlist [**" + firstTrack.getInfo().title + "**]("
                        + trackUrl + ").");

                e.replyEmbeds(embed).queue((success) -> {
                        },
                        (error) -> e.getChannel().sendMessageEmbeds(embed).queue());

                play(musicManager, firstTrack);
                playlist.getTracks().remove(0);
                playlist.getTracks().forEach(musicManager.getScheduler()::addAudioTrack);
            }

            @Override
            public void noMatches() {
                MessageEmbed embed = EmbedApi.ERROR
                        .getMessage(":headphones: Infelizmente não foi possível encontrar essa música.");

                e.replyEmbeds(embed).queue((success) -> {
                        },
                        (error) -> e.getChannel().sendMessageEmbeds(embed).queue());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                MessageEmbed embed = EmbedApi.ERROR
                        .getMessage(":headphones: Impossível carregar à música, erro: " + exception.getMessage());

                e.replyEmbeds(embed).queue((success) -> {
                        },
                        (error) -> e.getChannel().sendMessageEmbeds(embed).queue());
            }
        });
    }

    private void play(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.getScheduler().addAudioTrack(track);
    }

}