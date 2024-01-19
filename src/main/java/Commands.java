import music.MusicCommands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Commands {
    public static void choose(SlashCommandInteractionEvent e) {
        String cmd = e.getName();

        switch (cmd) {
            case "play":
                MusicCommands.play(e);
            case "skip":
                //todo
            case "stop":
                //todo
            case "search":
                //todo
        }
    }
}
