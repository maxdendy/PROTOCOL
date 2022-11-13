package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Locale;
import java.util.Objects;

public class Commands {
    static String prefix = "%";
    static String version = "Версия бота: 1.1";

    public static class Listener {
        public static void message(MessageReceivedEvent event) {
            Message message = event.getMessage();

            if (!event.getMessage().getContentRaw().startsWith(prefix)) return;

            if((!message.getMentions().getMembers().isEmpty()))
                if (message.getMentions().getMembers().get(0).getId().equals("918421362148790312"))
                    help(message);

            switch (Helper.getContentArray(message)[0].toLowerCase(Locale.ROOT).substring(prefix.length())) {
                case "конфиг":
                    Config.Listener.Message(message);
                    break;
                case "рагнарёк":
                    Ragnarok.Listeners.command(message);
                    break;
                case "юзер":
                    Info.CMDs.userInfo(message,Helper.getContentArray(message));
                    break;
                case "мут":
                    Moderation.mute(message,Helper.getContentArray(message));
            }
        }
    }

    private static class Helper {

        private static String[] getContentArray(Message message) {
            return message.getContentDisplay().split("\\s+");
        }
    }

    static EmbedBuilder eb = new EmbedBuilder();

    private static void help(Message message){
        if (Objects.requireNonNull(message.getMember()).getEffectiveName().isEmpty())
            eb.setAuthor(message.getAuthor().getName(),null,message.getAuthor().getEffectiveAvatarUrl());
        else
            eb.setAuthor(message.getMember().getEffectiveName(),null,message.getAuthor().getEffectiveAvatarUrl());
        String warning = ":warning:";
        eb.setTitle("Список комманд").setColor(Color.magenta)
                .addField(prefix+"рагнарёк","Рагнарёк",false)
                .setFooter(version);
        message.replyEmbeds(eb.build()).submit();
    }
}
