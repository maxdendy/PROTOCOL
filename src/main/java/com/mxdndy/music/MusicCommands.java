package com.mxdndy.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.mxdndy.lava.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MusicCommands {
    public static void play(SlashCommandInteractionEvent e) {
        if(check(e)) return;
        join(e);

        String track = Objects.requireNonNull(e.getOption("track")).getAsString();
        try {
            new URL(track);
        } catch (MalformedURLException ex) {
            track = "ytsearch:" + track;
        }

        PlayerManager.get().play(e.getGuild(), track);
        e.reply("text").setEphemeral(true).queue();
    }

    public static void skip(SlashCommandInteractionEvent e) {
        if(check(e)) return;

        PlayerManager.get().skip(e.getGuild());

        e.reply("Попущен.").setEphemeral(true).queue();
    }

    public static void stop(SlashCommandInteractionEvent e) {
        if(check(e)) return;

        PlayerManager.get().stop(e.getGuild());

        e.reply("До новых втреч!").setEphemeral(true).queue();
        e.getGuild().getAudioManager().closeAudioConnection();
    }

    public static void now(SlashCommandInteractionEvent e) {
        if(check(e)) return;

        AudioTrackInfo info = PlayerManager.get().now(e.getGuild());
        if(info == null) {
            e.reply("Не играет, чего ты ждал?").setEphemeral(true).queue();
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Сейчас играет")
                .setDescription("**Название:** `" + info.title + "`")
                .appendDescription("\n**Автор:** `" + info.author + "`")
                .appendDescription("\n**Ссылка:** " + info.uri);
        e.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    //UTILS
    private static boolean check(SlashCommandInteractionEvent e) {
        GuildVoiceState memberVoiceState = e.getMember().getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            e.reply("Консось").setEphemeral(true).queue();
            return true;
        }

        Member self = e.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(selfVoiceState.inAudioChannel()) {
            if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                e.reply("Не твоё болото!").setEphemeral(true).queue();
                return true;
            }
        }
        return false;
    }

    private static void join(SlashCommandInteractionEvent e) {
        GuildVoiceState memberVoiceState = e.getMember().getVoiceState();
        e.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
    }
}
