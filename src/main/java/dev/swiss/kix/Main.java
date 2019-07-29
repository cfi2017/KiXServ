package dev.swiss.kix;

import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import dev.swiss.kix.commands.KixCommand;
import dev.swiss.kix.profanity.ProfanityFilter;
import dev.swiss.kix.profanity.ReplacementRule;
import dev.swiss.kix.spawner.SpawnerDestroyFilter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    private FileConfiguration config = this.getConfig();
    public static ProfanityFilter profanityFilter = new ProfanityFilter();
    public static SpawnerDestroyFilter spawnerDestroyFilter = new SpawnerDestroyFilter();
    public static final Logger LOGGER = LoggerFactory.getLogger("KiXServ");
    private MultiverseCore multiverseCore;

    @Override
    public void onEnable() {
        if (Multiverse.getCore() == null) {
            LOGGER.error("Multiverse Core not present.");
            getServer().getPluginManager().disablePlugin(this);
        }

        ConfigurationSerialization.registerClass(ReplacementRule.class, "ReplacementRule");

        config.options().copyDefaults(true);
        saveConfig();

        loadConfig();

        getServer().getPluginManager().registerEvents(profanityFilter, this);
        getServer().getPluginManager().registerEvents(spawnerDestroyFilter, this);
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new KixCommand(this));
    }

    public void reload() {
        this.reloadConfig();
        loadConfig();
    }

    private void loadConfig() {
        profanityFilter.getBlacklist().clear();
        profanityFilter.getRules().clear();
        if (config.contains("profanity")) {
            ConfigurationSection profanityConfig = config.getConfigurationSection("profanity");
            if (profanityConfig.contains("blacklist")) {
                profanityFilter.getBlacklist().addAll(profanityConfig.getStringList("blacklist"));
            }
            if (profanityConfig.contains("replacements")) {
                List<ReplacementRule> rules = profanityConfig.getList("replacements", new ArrayList())
                        .stream().map(rule -> (ReplacementRule)rule).collect(Collectors.toList());
                profanityFilter.getRules().addAll(rules);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}
