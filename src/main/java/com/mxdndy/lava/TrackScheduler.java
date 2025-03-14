package com.mxdndy.lava;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.player.setVolume(DefaultMusicConfig.VOLUME);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        player.startTrack(queue.poll(), false);
    }

    public void queue(AudioTrack track) {
        if(!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }
}
