package com.mxdndy;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault(SOMETEXT.BETOKEN)
                .setEnabledIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.GUILD_MESSAGES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.addEventListener(new Listeners());

        //todo добавить поддержку яндекса
        jda.upsertCommand("play", "Включает на ютубе или по ссылке.")
                .addOption(OptionType.STRING, "track", "btw можно ссылку яндекса.", true)
                .queue();
        jda.upsertCommand("skip", "Скипает текущий.")
                .queue();
        jda.upsertCommand("stop", "Скипает всё и ливает.")
                .queue();
        jda.upsertCommand("playing", "Скажу что сейчас играет.")
                .queue();
        /*jda.upsertCommand("search", "Ищет на ютубе.")
                .queue();*/
    }
}
