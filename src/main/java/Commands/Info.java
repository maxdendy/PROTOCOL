package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Info {
    public static class CMDs {
        public static void userInfo(Message msg, String[] str) {
            EmbedBuilder eb = new EmbedBuilder();
            msg.getJDA().retrieveUserById(str[2])
                    .flatMap(user -> msg.replyEmbeds(eb.setColor(0xff0000)
                            .setTitle("User info")
                            .setFooter(msg.getAuthor().getName(),msg.getAuthor().getAvatarUrl())
                            .setDescription(user.getAsMention())
                            .addField("Name",user.getName(),false)
                            .addField("Registration date",regDate(user), true)
                            .setImage(user.getAvatarUrl())
                            .build())).queue();
        }
    }

    static String regDate(User user){
        String th = "th";
        switch (user.getTimeCreated().getDayOfMonth()%10) {
            case 1: th = "st"; break;
            case 2: th = "nd"; break;
            case 3: th = "rd"; break;
        }
        String out = user.getTimeCreated().getDayOfMonth()+th;
        out += " ";
        out += user.getTimeCreated().getMonth().name();
        out += " ";
        out += user.getTimeCreated().getYear();
        out += " ";
        out += user.getTimeCreated().getHour();
        out += ":";
        if (user.getTimeCreated().getMinute()<10) out += "0" + user.getTimeCreated().getMinute();
        else out += user.getTimeCreated().getMinute();
        out += ":";
        if (user.getTimeCreated().getSecond()<10) out += "0" + user.getTimeCreated().getSecond();
        else out += user.getTimeCreated().getSecond();
        return out;
    }
}
