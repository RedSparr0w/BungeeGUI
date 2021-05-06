package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.command.ReloadCommand;
import com.danifoldi.bungeegui.util.FileUtil;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.StringUtil;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BungeeGuiLoader {

    private final GuiHandler guiHandler;
    private final BungeeGuiPlugin plugin;
    private final Logger logger;
    private final PluginManager pluginManager;
    private final Path datafolder;

    @Inject
    public BungeeGuiLoader(final @NotNull GuiHandler guiHandler,
                           final @NotNull BungeeGuiPlugin plugin,
                           final @NotNull Logger logger,
                           final @NotNull PluginManager pluginManager,
                           final @NotNull Path datafolder) {
        this.guiHandler = guiHandler;
        this.plugin = plugin;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.datafolder = datafolder;
    }

    void load() {
        StringUtil.blockPrint("Loading " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion()).forEach(logger::info);

        BungeeGuiAPI.setInstance(new BungeeGuiAPI(guiHandler, this));

        try {
            final FileConfig config = FileUtil.ensureConfigFile(datafolder, "config.yml");
            config.load();

            guiHandler.load(config);
            Message.setMessageProvider(config);

            pluginManager.registerCommand(plugin, new ReloadCommand());
            guiHandler.registerCommands();
            pluginManager.registerListener(plugin, new BungeeGuiListener(guiHandler));
        } catch (IOException e) {
            StringUtil.blockPrint("Could not enable plugin, please see the error below").forEach(logger::severe);
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    void unload() {
        StringUtil.blockPrint("Unloading " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion()).forEach(logger::info);

        BungeeGuiAPI.setInstance(null);
        pluginManager.unregisterCommands(plugin);
        pluginManager.unregisterListeners(plugin);
    }
}