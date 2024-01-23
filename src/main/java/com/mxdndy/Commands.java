package com.mxdndy;

import com.mxdndy.music.MusicCommands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Commands {
    public static void choose(SlashCommandInteractionEvent e) {
        String cmd = e.getName();

        switch (cmd) {
            case "play":
                MusicCommands.play(e);
                break;
            case "stop":
                MusicCommands.stop(e);
                break;
            case "skip":
                MusicCommands.skip(e);
                break;
            case "playing":
                MusicCommands.now(e);
                break;
            case "search":
                //todo
                break;
        }
    }
}
