package dev.swiss.kix.profanity;

import dev.swiss.kix.Main;
import dev.swiss.kix.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProfanityFilter implements Listener {

    private List<String> blacklist = new ArrayList<>();
    private List<ReplacementRule> rules = new ArrayList<>();

    public List<String> getBlacklist() {
        return blacklist;
    }
    public List<ReplacementRule> getRules() { return rules; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String originalMessage = event.getMessage();
        String messageToProcess = originalMessage.toLowerCase();
        for (ReplacementRule rule : this.getRules()) {
            messageToProcess = rule.apply(messageToProcess);
        }
        String finalMessageToProcess = messageToProcess;
        if (!event.getPlayer().hasPermission(Permissions.BYPASS_PROFANITY_FILTER) && this.blacklist.stream()
                .anyMatch(word -> finalMessageToProcess.contains(word.toLowerCase()))) {
            event.getPlayer().sendMessage(
                    ChatColor.translateAlternateColorCodes('&', "&6[&4PROFANITY&6] &cPlease watch your language."));
            String message = originalMessage;
            for (String word : this.blacklist) {
                message = message.replaceAll("(?i)"+ Pattern.quote(word), "<censored>");
            }
            if (message.equals(originalMessage)) {
                message = "<couldn't locate profanity, censored entire message>";
            }
            event.setMessage(message);
            Main.LOGGER.info("<{}> used profanity.", event.getPlayer().getName());
            Bukkit.getOnlinePlayers()
                    .parallelStream()
                    .filter(player -> ((Player) player).hasPermission(Permissions.SEE_PROFANITY_MESSAGES))
                    .forEach(player -> {
                        String formattedMessage = String.format(
                                ChatColor.translateAlternateColorCodes('&', "&6[&4PROFANITY&6] &c%s used profanity: \"%s\""),
                                event.getPlayer().getName(),
                                originalMessage);
                        ((Player) player).sendMessage(formattedMessage);
                    });
        }
    }

}
