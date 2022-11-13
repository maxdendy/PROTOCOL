package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Config {
    public static class Listener {
        public static void Button(@NotNull ButtonInteractionEvent event) {
            if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                //
            }
        }

        public static void Message(Message message) {
            EmbedBuilder eb = new EmbedBuilder();
            if (message.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                eb.setColor(0x00ff00).setTitle("Настройки бота").setFooter(Commands.version);
                get("sex");
            } else {
                eb.setTitle("Ошибка!").setDescription("Недостаточно прав!").setColor(0xff0000);
                message.delete().queue();
                message.replyEmbeds(eb.build()).queueAfter(2, TimeUnit.SECONDS);
            }
        }
    }

    public static String get(String config) {
        String content = "";
        String returns = "";

        try (FileReader reader = new FileReader("config")) {
            int c;
            while ((c=reader.read())!=-1) {
                content+=(char)c;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return returns;
    }

    public void set(String config, String state) {
        String content = "";

        try (FileReader reader = new FileReader("config")) {
            int c;

            while ((c=reader.read())!=-1) {
                content+=(char)c;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileOutputStream fos = new FileOutputStream("config")) {
            byte[] b = content.getBytes(StandardCharsets.UTF_8);
            fos.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}