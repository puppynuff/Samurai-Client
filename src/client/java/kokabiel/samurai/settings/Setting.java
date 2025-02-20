package kokabiel.samurai.settings;

import kokabiel.samurai.utils.render.TextUtils;

import java.util.HashSet;
import java.util.function.Consumer;

public abstract class Setting<T> {
    public enum TYPE {
        BOOLEAN, FLOAT, STRING, INTEGER, STRINGLIST, INDEXEDSTRINGLIST, KEYBIND, COLOR, BLOCKS, ENUM, RECTANGLE, VEC3D
    }

    public final String ID;
    public final String displayName;
    public final String description;
    public final T defaultValue;

    protected T value;

    public TYPE type;

    private final HashSet<Consumer<T>> onUpdate = new HashSet<Consumer<T>>();

    public Setting(String ID, String description, T defaultValue) {
        this.ID = ID;
        this.displayName = TextUtils.IDToName(ID);
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public Setting(String ID, String DisplayName, String description, T defaultValue) {
        this.ID = ID;
        this.displayName = description;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public Setting(String ID, String DisplayName, String description, T defaultValue, Consumer<T> onUpdate) {
        this.ID = ID;
        this.displayName = DisplayName;
        this.description = description;
        this.defaultValue = defaultValue;
        this.onUpdate.add(onUpdate);
        this.value = defaultValue;
    }

    public Setting(String ID, String description, T defaultValue, Consumer<T> onUpdate) {
        this.ID = ID;
        this.displayName = TextUtils.IDToName(ID);
        this.description = description;
        this.defaultValue = defaultValue;
        this.onUpdate.add(onUpdate);
        this.value = defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (isValueValid(value)) {
            this.value = value;
        }

        update();
    }

    public void resetToDefault() {
        resetValue();
        update();
    }

    public void silentSetValue(T value) {
        if(isValueValid(value)) {
            this.value = value;
        }
    }

    public void resetValue() {
        setValue(defaultValue);
    }

    public void update() {
        for(Consumer<T> consumer : onUpdate) {
            if(consumer != null) consumer.accept(value);
        }
    }

    public void addOnUpdate(Consumer<T> consumer) {
        onUpdate.add(consumer);
    }

    public void removeOnUpdate(Consumer<T> consumer) {
        onUpdate.remove(consumer);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    protected abstract boolean isValueValid(T value);

    @SuppressWarnings("unchecked")
    public abstract static class BUILDER<B extends BUILDER<?, ?, ?>, S extends Setting<T>, T> {
        protected String id;
        protected String displayName;
        protected String description;
        protected T defaultValue;
        protected Consumer<T> onUpdate;

        protected BUILDER() {}

        public B id(String id) {
            this.id = id;
            return (B) this;
        }

        public B displayName(String displayName) {
            this.displayName = displayName;
            return (B) this;
        }

        public B description(String description) {
            this.description = description;
            return (B) this;
        }

        public B defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return (B) this;
        }

        public B onUpdate(Consumer<T> onUpdate) {
            this.onUpdate = onUpdate;
            return (B) this;
        }

        public abstract S build();
    }
}
