import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("OTE4NDIxMzYyMTQ4NzkwMzEy.GXWEJ-.D9dXeBf8997L4xJKkGxdyiwjv63JdCR7XeKEFA")
                //.enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS, GatewayIntent.GUILD_MESSAGES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.addEventListener(new Listeners());
    }
}