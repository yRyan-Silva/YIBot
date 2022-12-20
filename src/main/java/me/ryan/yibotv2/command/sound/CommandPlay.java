package me.ryan.yibotv2.command.sound;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import me.ryan.yibotv2.command.manager.CommandExecutor;
import me.ryan.yibotv2.music.PlayerManager;
import me.ryan.yibotv2.music.TrackScheduler;
import me.ryan.yibotv2.utils.EmbedApi;
import me.ryan.yibotv2.utils.Services;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import javax.sound.midi.Track;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CommandPlay implements CommandExecutor {

    private final PlayerManager PLAYER_MANAGER = Services.load(PlayerManager.class);

    @Override
    public void process(SlashCommandInteractionEvent e) {

        JDA jda = e.getJDA();
        Guild guild = e.getGuild();
        String music = e.getOption("música").getAsString();
        Member member = e.getMember(), jdaMember = guild.getMemberById(jda.getSelfUser().getId());
        TrackScheduler trackScheduler = PLAYER_MANAGER.getTrackScheduler(guild);

        if (!member.getVoiceState().inAudioChannel()) {
            e.replyEmbeds(EmbedApi.ERROR.getMessage(":headphones: Você precisa estar em um canal de voz!")).queue();
            return;
        }

        if (jdaMember.getVoiceState().inAudioChannel() &&
                !jdaMember.getVoiceState().getChannel().equals(member.getVoiceState().getChannel()) &&
                trackScheduler.getTimer() != null &&
                System.currentTimeMillis() - trackScheduler.getStartTimerMillis() <= 90000) {
            e.replyEmbeds(EmbedApi.ERROR.getMessage(":headphones: Eu já estou em um canal tocando música!")).queue();
            return;
        }

        if (!isUrl(music)) {
            e.replyEmbeds(EmbedApi.WARNING.getMessage("\uD83D\uDD0E Procurado música... [" + music + "]")).queue();

            music = searchYoutube(music);
        }

        PLAYER_MANAGER.loadAndPlay(e, music, e.getMember().getVoiceState().getChannel().asVoiceChannel());

    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[]{Commands.slash("play", "Tocar uma música.")
                .addOption(OptionType.STRING, "música", "Insira a url ou o nome da música.",
                true)};
    }

    private String searchYoutube(String input) {
        try {
            List<SearchResult> results = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    new GsonFactory(), null).setApplicationName("LupanBot 2.0").build()
                    .search().list(List.of("id", "snippet")).setQ(input).setMaxResults(1L)
                    .setType(List.of("video"))
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey("AIzaSyAQMJMcIRmeqBZZMUvq2kgsJHPkKzhFuro").execute().getItems();
            if (!results.isEmpty())
                return "https://www.youtube.com/watch?v=" + results.get(0).getId().getVideoId();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

}
