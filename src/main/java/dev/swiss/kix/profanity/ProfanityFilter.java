package dev.swiss.kix.profanity;

import dev.swiss.kix.Main;
import dev.swiss.kix.Permissions;
import dev.swiss.kix.model.Violation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProfanityFilter implements Listener {

    private List<String> blacklist = new ArrayList<>();
    private List<ReplacementRule> rules = new ArrayList<>();
    private List<Violation> violations = new ArrayList<>();

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
            String message = originalMessage;
            for (String word : this.blacklist) {
                message = message.replaceAll("(?i)"+ Pattern.quote(word), "<censored>");
            }
            if (message.equals(originalMessage)) {
                message = "<couldn't locate profanity, censored entire message>";
            }
            event.setMessage(message);
            Main.LOGGER.info("<{}> used profanity.", event.getPlayer().getName());
            String formattedMessage = String.format(
                ChatColor.translateAlternateColorCodes('&', "&6[&4PROFANITY&6] &c%s used profanity: \"%s\""),
                event.getPlayer().getName(),
                originalMessage);
            Bukkit.getConsoleSender().sendMessage(formattedMessage);
            Bukkit.getOnlinePlayers()
                    .parallelStream()
                    .filter(player -> ((Player) player).hasPermission(Permissions.SEE_PROFANITY_MESSAGES))
                    .forEach(player -> {
                        ((Player) player).sendMessage(formattedMessage);
                    });

            // clean violations
            cleanupViolations();
            // add violation
            violations.add(new Violation(event.getPlayer()));
            // count violation
            List<Violation> playerViolations = getViolationsForPlayer(event.getPlayer());
            // run commands based on violation count
            Integer violationCount = playerViolations.size();

            if (violationCount >= 3) {
                String command = String.format("tempban %s 10m Violations exceeded: Profanity.", event.getPlayer().getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } else if (violationCount == 2) {
                String command = String.format("kick %s Violations exceeded: Profanity.", event.getPlayer().getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } else if (violationCount == 1) {
                event.getPlayer().sendMessage(
                    ChatColor.translateAlternateColorCodes('&', "&6[&4PROFANITY&6] &cPlease watch your language."));
            }

        }
    }

    private List<Violation> getViolationsForPlayer(Player player) {
        return this.violations.stream().filter(violation -> violation.getPlayer().getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList());
    }

    private void cleanupViolations() {
        this.violations.removeAll(
            this.violations.stream()
                .filter(violation -> violation.getTime().isBefore(new DateTime().minusMinutes(10)))
                .collect(Collectors.toList())
            );
    }

}
