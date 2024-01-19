package music;

import lava.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class MusicCommands {
    public static void play(SlashCommandInteractionEvent e) {
        Member member = e.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            e.reply("Консось").setEphemeral(true).queue();
            return;
        }

        Member self = e.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            e.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else {
            if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                e.reply("Не видишь, занято!").queue();
                return;
            }
        }

        //todo пока что поиск но потом будет не поиск
        //todo или тут ещё ссылка есть хз
        String track = e.getOption("track").getAsString();
        try {
            new URI(track);
        } catch (URISyntaxException ex) {
            track = "ytsearch:" + track;
        }

        PlayerManager playerManager = PlayerManager.get();
        e.reply("Ништяк").queue();
        playerManager.play(e.getGuild(), track);
    }
}
