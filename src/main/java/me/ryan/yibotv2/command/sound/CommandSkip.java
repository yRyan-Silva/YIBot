package me.ryan.yibotv2.command.sound;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.ryan.yibotv2.command.manager.CommandExecutor;
import me.ryan.yibotv2.music.PlayerManager;
import me.ryan.yibotv2.music.TrackScheduler;
import me.ryan.yibotv2.utils.EmbedApi;
import me.ryan.yibotv2.utils.Services;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandSkip implements CommandExecutor {

    private final PlayerManager PLAYER_MANAGER = Services.load(PlayerManager.class);
    private final List<String> VOTES = new ArrayList<>();

    @Override
    public void process(SlashCommandInteractionEvent e) {

        Guild guild = e.getGuild();
        Member jdaMember = guild.getMemberById(e.getJDA().getSelfUser().getId()),
                member = e.getMember();
        TrackScheduler trackScheduler = PLAYER_MANAGER.getTrackScheduler(guild);
        if (!jdaMember.getVoiceState().inAudioChannel())
            e.replyEmbeds(EmbedApi.ERROR.getMessage(":headphones: Eu não estou tocando música em nenhum canal.")).queue();
        else if (!member.getVoiceState().inAudioChannel() || !jdaMember.getVoiceState().getChannel()
                .equals(member.getVoiceState().getChannel()))
            e.replyEmbeds(EmbedApi.ERROR.getMessage(":headphones: Você não está no mesmo canal que eu.")).queue();
        else if (trackScheduler.getQueue().isEmpty())
            e.replyEmbeds(EmbedApi.ERROR.getMessage(":headphones: Não existe mais nenhuma música na fila para poder pular.")).queue();
        else if (member.hasPermission(Permission.MANAGE_CHANNEL))
            skip(e);
        else {
            List<Member> members = e.getMember().getVoiceState().getChannel().getMembers().stream().filter($ -> !$.getUser().isBot()).collect(Collectors.toList());
            if (members.size() <= 3) skip(e);
            else {
                if (addVote(member)) {
                    if (VOTES.size() >= members.size() / 2) skip(e);
                    else
                        e.replyEmbeds(EmbedApi.ERROR.getMessage(":headphones: É necessário mais votos, (" + VOTES.size() + "/" + (members.size() / 2) + ")")).queue();
                } else
                    e.replyEmbeds(EmbedApi.ERROR.getMessage(":headphones: Você já votou, aguarde outras pessoas votarem também. (" + VOTES.size() + "/" + (members.size() / 2) + ")")).queue();
            }
        }

    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[]{Commands.slash("skip", "Pular música atual."),
                Commands.slash("s", "Pular música atual.")};
    }

    private void skip(SlashCommandInteractionEvent e) {
        TrackScheduler trackScheduler = PLAYER_MANAGER.getTrackScheduler(e.getGuild());
        AudioTrack track = trackScheduler.nextTrack();
        e.replyEmbeds(EmbedApi.SUCCESS.getMessage(":headphones: Música pulada com sucesso, tocando agora: [**" + track.getInfo().title + "**](" + track.getInfo().uri + ")!")).queue();
    }

    private boolean addVote(Member member) {
        if (!VOTES.contains(member.getId())) {
            VOTES.add(member.getId());
            return true;
        }
        return false;
    }

}
