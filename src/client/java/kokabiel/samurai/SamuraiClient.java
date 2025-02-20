package kokabiel.samurai;

import com.mojang.logging.LogUtils;
import kokabiel.samurai.api.IAddon;
import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.font.FontManager;
import kokabiel.samurai.managers.EventManager;
import kokabiel.samurai.managers.SettingManager;
import kokabiel.samurai.managers.altmanager.AltManager;
import kokabiel.samurai.managers.macros.MacroManager;
import kokabiel.samurai.managers.proxymanager.ProxyManager;
import kokabiel.samurai.module.ModuleManager;
import kokabiel.samurai.module.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class SamuraiClient {
    public static MinecraftClient MC;
    private static Logger LOGGER;

    public EventManager eventManager;
    public FontManager fontManager;
    public ModuleManager moduleManager;
    public GuiManager guiManager;
    public MacroManager macroManager;
    public AltManager altManager;
    public ProxyManager proxyManager;
    public static final String SamuraiVersion = "0.1.1";

    public static List<IAddon> addons = new ArrayList<>();


    public void Initialize() {
        MC = MinecraftClient.getInstance();
        LOGGER = LogUtils.getLogger();
//        loadAssets();
    }

    public void loadAssets() {
        LOGGER.info("[Samurai] Starting Client...");

        for (EntrypointContainer<IAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("samurai", IAddon.class))
        {
            IAddon addon = entrypoint.getEntrypoint();

            try
            {
                LOGGER.info("[Samurai] Initializing addon: " + addon.getName());
                addon.onInitialize();
                LOGGER.info("[Samurai] Addon initialized: " + addon.getName());
            } catch (Throwable e)
            {
                LOGGER.error("Error initializing addon: " + addon.getName(), e.getMessage());
            }

            addons.add(addon);
        }

        LOGGER.info("[Samurai] Loading managers...");
        eventManager = new EventManager();
        fontManager = new FontManager();
        fontManager.Initialize();
        moduleManager = new ModuleManager(addons);
        macroManager = new MacroManager();
        guiManager = new GuiManager();
        guiManager.Initialize();
        altManager = new AltManager();
        proxyManager = new ProxyManager();
        LOGGER.info("[Samurai] Finished Loading Client");
    }

    public void endClient() {
        LOGGER.info("[Samurai] Shutting down...");
        try {
            SettingManager.saveSettings();
            altManager.saveAlts();
            macroManager.save();
            moduleManager.modules.forEach(Module::onDisable);
        } catch(Exception e) {
            LOGGER.error("[Samurai] Error saving data", e);
        }
    }
}
