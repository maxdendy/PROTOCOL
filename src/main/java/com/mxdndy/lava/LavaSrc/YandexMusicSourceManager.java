//source: https://github.com/topi314/LavaSrc/blob/master/main/src/main/java/com/github/topi314/lavasrc/yandexmusic/YandexMusicSourceManager.java

package com.mxdndy.lava.LavaSrc;

import com.github.topi314.lavasrc.LavaSrcTools;
import com.github.topi314.lavasrc.ExtendedAudioPlaylist.Type;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YandexMusicSourceManager implements AudioSourceManager, HttpConfigurable {
    public static final Pattern URL_PATTERN = Pattern.compile("(https?://)?music\\.yandex\\.(ru|com)/(?<type1>artist|album)/(?<identifier>[0-9]+)/?((?<type2>track/)(?<identifier2>[0-9]+)/?)?");
    public static final Pattern URL_PLAYLIST_PATTERN = Pattern.compile("(https?://)?music\\.yandex\\.(ru|com)/users/(?<identifier>[0-9A-Za-z@.-]+)/playlists/(?<identifier2>[0-9]+)/?");
    public static final String SEARCH_PREFIX = "ymsearch:";
    public static final String PUBLIC_API_BASE = "https://api.music.yandex.net";
    private static final Logger log = LoggerFactory.getLogger(YandexMusicSourceManager.class);
    private final HttpInterfaceManager httpInterfaceManager;
    private final String accessToken;

    public YandexMusicSourceManager(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            this.accessToken = accessToken;
            this.httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();
        } else {
            throw new IllegalArgumentException("Yandex Music accessToken must be set");
        }
    }

    public String getSourceName() {
        return "yandexmusic";
    }

    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        try {
            if (reference.identifier.startsWith("ymsearch:")) {
                return this.getSearch(reference.identifier.substring("ymsearch:".length()));
            } else {
                Matcher matcher = URL_PATTERN.matcher(reference.identifier);
                if (matcher.find()) {
                    switch (matcher.group("type1")) {
                        case "album":
                            String albumId;
                            if (matcher.group("type2") != null) {
                                albumId = matcher.group("identifier2");
                                return this.getTrack(albumId);
                            }

                            albumId = matcher.group("identifier");
                            return this.getAlbum(albumId);
                        case "artist":
                            String artistId = matcher.group("identifier");
                            return this.getArtist(artistId);
                        default:
                            return null;
                    }
                } else {
                    matcher = URL_PLAYLIST_PATTERN.matcher(reference.identifier);
                    if (matcher.find()) {
                        var userId = matcher.group("identifier");
                        String playlistId = matcher.group("identifier2");
                        return this.getPlaylist(userId, playlistId);
                    } else {
                        return null;
                    }
                }
            }
        } catch (IOException var8) {
            throw new RuntimeException(var8);
        }
    }

    private AudioItem getSearch(String query) throws IOException {
        JsonBrowser json = this.getJson("https://api.music.yandex.net/search?text=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&type=track&page=0");
        if (!json.isNull() && !json.get("result").get("tracks").isNull()) {
            List<AudioTrack> tracks = this.parseTracks(json.get("result").get("tracks").get("results"));
            return tracks.isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist("Yandex Music Search: " + query, tracks, null, true);
        } else {
            return AudioReference.NO_TRACK;
        }
    }

    private AudioItem getAlbum(String id) throws IOException {
        JsonBrowser json = this.getJson("https://api.music.yandex.net/albums/" + id + "/with-tracks");
        if (!json.isNull() && !json.get("result").isNull()) {
            ArrayList<AudioTrack> tracks = new ArrayList<>();

            for (JsonBrowser volume : json.get("result").get("volumes").values()) {

                for (JsonBrowser track : volume.values()) {
                    AudioTrack parsedTrack = this.parseTrack(track);
                    if (parsedTrack != null) {
                        tracks.add(parsedTrack);
                    }
                }
            }

            if (tracks.isEmpty()) {
                return AudioReference.NO_TRACK;
            } else {
                String coverUri = json.get("result").get("coverUri").text();
                String author = json.get("result").get("artists").values().get(0).get("name").text();
                return new YandexMusicAudioPlaylist(json.get("result").get("title").text(), tracks, Type.ALBUM, json.get("result").get("url").text(), this.formatCoverUri(coverUri), author);
            }
        } else {
            return AudioReference.NO_TRACK;
        }
    }

    private AudioItem getTrack(String id) throws IOException {
        JsonBrowser json = this.getJson("https://api.music.yandex.net/tracks/" + id);
        return (AudioItem)(!json.isNull() && !((JsonBrowser)json.get("result").values().get(0)).get("available").text().equals("false") ? this.parseTrack((JsonBrowser)json.get("result").values().get(0)) : AudioReference.NO_TRACK);
    }

    private AudioItem getArtist(String id) throws IOException {
        JsonBrowser json = this.getJson("https://api.music.yandex.net/artists/" + id + "/tracks?page-size=10");
        if (!json.isNull() && !json.get("result").values().isEmpty()) {
            List<AudioTrack> tracks = this.parseTracks(json.get("result").get("tracks"));
            if (tracks.isEmpty()) {
                return AudioReference.NO_TRACK;
            } else {
                JsonBrowser artistJson = this.getJson("https://api.music.yandex.net/artists/" + id);
                String coverUri = json.get("result").get("coverUri").text();
                String author = artistJson.get("result").get("artist").get("name").text();
                return new YandexMusicAudioPlaylist(author + "'s Top Tracks", tracks, Type.ARTIST, json.get("result").get("url").text(), this.formatCoverUri(coverUri), author);
            }
        } else {
            return AudioReference.NO_TRACK;
        }
    }

    private AudioItem getPlaylist(String userString, String id) throws IOException {
        JsonBrowser json = this.getJson("https://api.music.yandex.net/users/" + userString + "/playlists/" + id);
        if (!json.isNull() && !json.get("result").isNull() && !json.get("result").get("tracks").values().isEmpty()) {
            ArrayList<AudioTrack> tracks = new ArrayList<>();

            for (JsonBrowser track : json.get("result").get("tracks").values()) {
                AudioTrack parsedTrack = this.parseTrack(track.get("track"));
                if (parsedTrack != null) {
                    tracks.add(parsedTrack);
                }
            }

            if (tracks.isEmpty()) {
                return AudioReference.NO_TRACK;
            } else {
                String playlistTitle = json.get("result").get("kind").text().equals("3") ? "Liked songs" : json.get("result").get("title").text();
                String coverUri = json.get("result").get("cover").get("uri").text();
                String author = json.get("result").get("owner").get("name").text();
                return new YandexMusicAudioPlaylist(playlistTitle, tracks, Type.PLAYLIST, json.get("result").get("url").text(), this.formatCoverUri(coverUri), author);
            }
        } else {
            return AudioReference.NO_TRACK;
        }
    }

    public JsonBrowser getJson(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Authorization", "OAuth " + this.accessToken);
        return LavaSrcTools.fetchResponseAsJson(this.httpInterfaceManager.getInterface(), request);
    }

    public String getDownloadStrings(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Authorization", "OAuth " + this.accessToken);
        return HttpClientTools.fetchResponseLines(this.httpInterfaceManager.getInterface(), request, "downloadinfo-xml-page")[0];
    }

    private List<AudioTrack> parseTracks(JsonBrowser json) {
        ArrayList<AudioTrack> tracks = new ArrayList<>();

        for (JsonBrowser track : json.values()) {
            AudioTrack parsedTrack = this.parseTrack(track);
            if (parsedTrack != null) {
                tracks.add(parsedTrack);
            }
        }

        return tracks;
    }

    private AudioTrack parseTrack(JsonBrowser json) {
        if (json.get("available").asBoolean(false) && !json.get("albums").values().isEmpty()) {
            String id = json.get("id").text();
            String artist = json.get("major").get("name").text().equals("PODCASTS") ? json.get("albums").values().get(0).get("title").text() : json.get("artists").values().get(0).get("name").text();
            String coverUri = json.get("albums").values().get(0).get("coverUri").text();
            return new YandexMusicAudioTrack(new AudioTrackInfo(json.get("title").text(), artist, json.get("durationMs").as(Long.class), id, false, "https://music.yandex.ru/album/" + json.get("albums").values().get(0).get("id").text() + "/track/" + id), this);
        } else {
            return null;
        }
    }

    private String formatCoverUri(String coverUri) {
        return coverUri != null ? "https://" + coverUri.replace("%%", "400x400") : null;
    }

    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    public void encodeTrack(AudioTrack track, DataOutput output) {
    }

    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return new YandexMusicAudioTrack(trackInfo, this);
    }

    public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
        this.httpInterfaceManager.configureRequests(configurator);
    }

    public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
        this.httpInterfaceManager.configureBuilder(configurator);
    }

    public void shutdown() {
        try {
            this.httpInterfaceManager.close();
        } catch (IOException var2) {
            log.error("Failed to close HTTP interface manager", var2);
        }

    }

    public HttpInterface getHttpInterface() {
        return this.httpInterfaceManager.getInterface();
    }
}
