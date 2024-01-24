//source: https://github.com/topi314/LavaSrc/blob/master/main/src/main/java/com/github/topi314/lavasrc/yandexmusic/YandexMusicAudioTrack.java

package com.mxdndy.lava.LavaSrc;

import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3AudioTrack;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class YandexMusicAudioTrack extends DelegatedAudioTrack {
    private final YandexMusicSourceManager sourceManager;

    public YandexMusicAudioTrack(AudioTrackInfo trackInfo, YandexMusicSourceManager sourceManager) {
        super(trackInfo);
        this.sourceManager = sourceManager;
    }

    public void process(LocalAudioTrackExecutor executor) throws Exception {
        String downloadLink = this.getDownloadURL(this.trackInfo.identifier);
        HttpInterface httpInterface = this.sourceManager.getHttpInterface();

        try {
            PersistentHttpStream stream = new PersistentHttpStream(httpInterface, new URI(downloadLink), this.trackInfo.length);

            try {
                this.processDelegate(new Mp3AudioTrack(this.trackInfo, stream), executor);
            } catch (Throwable var9) {
                try {
                    stream.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }

                throw var9;
            }

            stream.close();
        } catch (Throwable var10) {
            if (httpInterface != null) {
                try {
                    httpInterface.close();
                } catch (Throwable var7) {
                    var10.addSuppressed(var7);
                }
            }

            throw var10;
        }

        if (httpInterface != null) {
            httpInterface.close();
        }

    }

    protected AudioTrack makeShallowClone() {
        return new YandexMusicAudioTrack(this.trackInfo, this.sourceManager);
    }

    public AudioSourceManager getSourceManager() {
        return this.sourceManager;
    }

    private String getDownloadURL(String id) throws IOException, NoSuchAlgorithmException {
        JsonBrowser json = this.sourceManager.getJson("https://api.music.yandex.net/tracks/" + id + "/download-info");
        if (!json.isNull() && !json.get("result").values().isEmpty()) {
            String downloadInfoLink = json.get("result").values().get(0).get("downloadInfoUrl").text();
            String downloadInfo = this.sourceManager.getDownloadStrings(downloadInfoLink);
            if (downloadInfo == null) {
                throw new IllegalStateException("No download URL found for track " + id);
            } else {
                Document doc = Jsoup.parse(downloadInfo, "", Parser.xmlParser());
                String host = doc.select("host").text();
                String path = doc.select("path").text();
                String ts = doc.select("ts").text();
                String s = doc.select("s").text();
                String sign = "XGRlBW9FXlekgbPrRHuSiA" + path + s;
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(sign.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }

                String md5 = sb.toString();
                return "https://" + host + "/get-mp3/" + md5 + "/" + ts + path;
            }
        } else {
            throw new IllegalStateException("No download URL found for track " + id);
        }
    }
}
