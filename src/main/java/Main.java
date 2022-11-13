import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder
                .createDefault("OTE4NDIxMzYyMTQ4NzkwMzEy.YbHAjA.V6Lq0CbKOgenQnC_smTafrb78c4")
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new Listeners())
                .setActivity(Activity.watching("@mention me to get help"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .build();
    }
}
/*
heroku deploy:jar C:\Users\Max\IdeaProjects\RussianVodka\target\RussianVodka-1.1-jar-with-dependencies.jar --app russian-vodka-discord-bot
 */