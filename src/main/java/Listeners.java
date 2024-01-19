import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listeners extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        event.getGuild().loadMembers().onSuccess(memberList -> {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Вот они сверху вниз: \n");
            for (Member m: memberList) {
                prompt.append(m.getUser().getAsMention()).append('\n');
            }
            prompt.append("Все участники!");
            event.getMessage().reply(prompt).mentionRepliedUser(false).queue();
        });
    }
}
