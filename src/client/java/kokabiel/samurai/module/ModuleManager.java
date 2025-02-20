package kokabiel.samurai.module;

import com.mojang.logging.LogUtils;
import kokabiel.samurai.Samurai;
import kokabiel.samurai.api.IAddon;
import kokabiel.samurai.event.events.KeyDownEvent;
import kokabiel.samurai.event.listeners.KeyDownListener;
import kokabiel.samurai.managers.SettingManager;
import kokabiel.samurai.module.modules.movement.Speed;
import kokabiel.samurai.module.modules.render.NoRender;
import kokabiel.samurai.module.modules.render.Tooltips;
import kokabiel.samurai.settings.Setting;
import kokabiel.samurai.settings.types.EnumSetting;
import net.minecraft.client.util.InputUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static kokabiel.samurai.SamuraiClient.MC;

public class ModuleManager implements KeyDownListener {
    public ArrayList<Module> modules = new ArrayList<Module>();

    // Modules
    public Speed speed = new Speed();
    public Tooltips tooltips = new Tooltips();
    public NoRender norender = new NoRender();

    public EnumSetting<AntiCheat> antiCheat = EnumSetting.<AntiCheat>builder().id("samurai_anticheat")
            .displayName("Current AntiCheat")
            .description("This setting will disable any modules or features known to be detected by a specific anticheat.")
            .defaultValue(AntiCheat.Vanilla).onUpdate(s -> {
                for(Module module: this.modules) {
                    if(module.isDetectable(s)) module.state.setValue(false);
                }
            }).build();

    public ModuleManager(List<IAddon> addons) {
        try {
            // Attempts to find each field of type Module and add it to the module list.
            for (Field field : ModuleManager.class.getDeclaredFields()) {
                if (!Module.class.isAssignableFrom(field.getType()))
                    continue;
                Module module = (Module) field.get(this);
                addModule(module);
            }

            // Gets each Addon and adds their modules to the client.
            addons.stream().filter(Objects::nonNull).forEach(addon -> {
                addon.modules().forEach(module -> {
                    addModule(module);
                });
            });
        } catch (Exception e) {
            LogUtils.getLogger().error("Error initializing Aoba modules: " + e.getMessage());
        }

        // Registers all Module settings to the settings manager.
        for (Module module : modules) {
            for (Setting<?> setting : module.getSettings()) {
                SettingManager.registerSetting(setting);
            }
        }

        Samurai.getInstance().eventManager.AddListener(KeyDownListener.class, this);
    }

    public void addModule(Module module) {
        modules.add(module);
    }

    public void disableAll() {
        for (Module module : modules) {
            module.state.setValue(false);
        }
    }

    public Module getModuleByName(String string) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(string)) {
                return module;
            }
        }
        return null;
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (MC.currentScreen == null) {
            for (Module module : modules) {
                if (module.isDetectable(antiCheat.getValue()))
                    continue;

                InputUtil.Key binding = module.getBind().getValue();
                if (binding.getCode() == event.GetKey()) {
                    module.toggle();
                }
            }
        }
    }
}
