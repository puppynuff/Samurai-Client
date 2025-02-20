package kokabiel.samurai.settings.types;

import kokabiel.samurai.settings.Setting;

import java.util.function.Consumer;

public class BooleanSetting extends Setting<Boolean> {
    protected BooleanSetting(String ID, String displayName, String description, Boolean defaultValue, Consumer<Boolean> onUpdate) {
        super(ID, displayName, description, defaultValue, onUpdate);
        type = TYPE.BOOLEAN;
    }

    public void toggle() {
        setValue(!value);
    }

    @Override
    protected boolean isValueValid(Boolean value) {
        return true;
    }

    public static BUILDER builder() {
        return new BUILDER();
    }

    public static class BUILDER extends Setting.BUILDER<BUILDER, BooleanSetting, Boolean> {
        protected BUILDER() {
            super();
        }

        @Override
        public BooleanSetting build() {
            return new BooleanSetting(id, displayName, description, defaultValue, onUpdate);
        }
    }
}
