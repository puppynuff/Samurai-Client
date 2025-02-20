package kokabiel.samurai.settings.types;

import kokabiel.samurai.settings.Setting;
import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class KeybindSetting extends Setting<InputUtil.Key> {
    protected KeybindSetting(String ID, String displayName, String description, InputUtil.Key defaultValue, Consumer<InputUtil.Key> onUpdate) {
        super(ID, description, defaultValue, onUpdate);
        type = TYPE.KEYBIND;
    }

    @Override
    protected boolean isValueValid(InputUtil.Key value) {
        return true;
    }

    public static BUILDER builder() {
        return new BUILDER();
    }

    public static class BUILDER extends Setting.BUILDER<BUILDER, KeybindSetting, InputUtil.Key> {
        protected BUILDER() {
            super();
        }

        @Override
        public KeybindSetting build() {
            return new KeybindSetting(id, displayName, description, defaultValue, onUpdate);
        }
    }
}
