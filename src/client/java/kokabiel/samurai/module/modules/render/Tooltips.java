package kokabiel.samurai.module.modules.render;

import kokabiel.samurai.module.Category;
import kokabiel.samurai.settings.types.BooleanSetting;
import kokabiel.samurai.module.Module;

public class Tooltips extends Module {

    private final BooleanSetting storage = BooleanSetting.builder().id("tooltips_storage").displayName("Storage")
            .description("Renders the contents of the storage item.").defaultValue(true).build();

    private final BooleanSetting maps = BooleanSetting.builder().id("tooltips_maps").displayName("Maps")
            .description("Render a map preview").defaultValue(true).build();

    public Tooltips() {
        super("Tooltips");
        this.setCategory(Category.of("Render"));
        this.setDescription("Renders custom item tooltips");

        this.addSetting(storage);
        this.addSetting(maps);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onToggle() {

    }

    public boolean getStorage() {
        return this.storage.getValue();
    }

    public boolean getMap() {
        return this.maps.getValue();
    }
}