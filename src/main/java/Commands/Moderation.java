package Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

public class Moderation {
    public static void mute(Message msg, String[] str) {
        System.out.println("Phase 1");
        if (!msg.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
        System.out.println("Phase 2");
        if (str.length < 3) return;
        System.out.println("Phase 3");

        String reason = "";
        if (str.length == 4) reason = str[3];
        System.out.println("reason: "+reason);

        if (msg.getMentions().getMembers().isEmpty()) {
            msg.getGuild().getMemberById(str[1]).timeoutFor(Utils.time(str[2])).reason(reason).queue();
            System.out.println("If 1");
        } else if (msg.getMentions().getMembers().size() == 1) {
            msg.getGuild().getMemberById(msg.getMentions().getMembers().get(0).getId()).timeoutFor(Utils.time(str[2])).reason(reason).queue();
            System.out.println("If 2");
        }
    }

    static class Utils {
        static Duration time(String str) {
            Duration dur = null;
            long num = 0;
            switch (str.substring(str.length() - 1)) {
                case "w":
                    dur = Duration.ofDays(num * 7);
                    break;
                case "d":
                    dur = Duration.ofDays(num);
                    break;
                case "h":
                    dur = Duration.ofHours(num);
                    break;
                case "m":
                    dur = Duration.ofMinutes(num);
                    break;
                case "s":
                    dur = Duration.ofSeconds(num);
                    break;
            }
            return dur;
        }
    }
}
