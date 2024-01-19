import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault(SOMETEXT.TOKEN)
                //.enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS, GatewayIntent.GUILD_MESSAGES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.addEventListener(new Listeners());
        jda.upsertCommand("play", "Запускает ссылку на медиа.")
                .addOption(OptionType.STRING, "track", "Добавляй чего смотришь?", true)
                .queue();
        /*todo патом
        jda.upsertCommand("stop", "Скипает всё и ливает.").queue();
        jda.upsertCommand("skip", "Скипает текущий.").queue();
        jda.upsertCommand("search", "Ищет на ютубе.").queue(); //todo добавить поддержку яндекса*/
    }
}
