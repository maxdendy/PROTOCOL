// source: https://github.com/topi314/LavaSrc/blob/master/main/src/main/java/com/github/topi314/lavasrc/yandexmusic/YandexMusicAudioPlaylist.java

package com.mxdndy.lava.LavaSrc;

import com.github.topi314.lavasrc.ExtendedAudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;

public class YandexMusicAudioPlaylist extends ExtendedAudioPlaylist {
    public YandexMusicAudioPlaylist(String name, List<AudioTrack> tracks, ExtendedAudioPlaylist.Type type, String identifier, String artworkURL, String author) {
        super(name, tracks, type, identifier, artworkURL, author, (Integer)null);
    }
}
