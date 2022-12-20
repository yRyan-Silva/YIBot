package me.ryan.yibotv2.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final LinkedList<AudioTrack> queue;
    private final Guild guild;
    private Timer timer;
    private long startTimerMillis;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.queue = new LinkedList<>();
        this.guild = guild;
        this.startTimerMillis = 0;
    }

    public void addAudioTrack(AudioTrack track) {
        if (queue.isEmpty() && player.getPlayingTrack() == null)
            player.startTrack(track, false);
        else queue.offer(track);
        if (timer != null) timer.cancel();
    }

    public AudioTrack nextTrack() {
        if (queue.isEmpty()) {
            startDisconnect();
            return null;
        } else {
            AudioTrack at = queue.poll();
            player.startTrack(at, false);
            return at;
        }
    }

    public void startDisconnect() {
        if (timer != null) timer.cancel();
        startTimerMillis = System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopPlayer();
            }
        }, TimeUnit.MINUTES.toMillis(3));
    }

    public void stopPlayer() {
        guild.getAudioManager().closeAudioConnection();
        player.destroy();
        queue.clear();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        queue.remove(track);
        if (endReason.mayStartNext) nextTrack();
    }

}

