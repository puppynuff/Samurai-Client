package kokabiel.samurai.event.events;

import kokabiel.samurai.event.listeners.AbstractListener;
import kokabiel.samurai.event.listeners.FontChangedListener;

import java.util.ArrayList;
import java.util.List;

public class FontChangedEvent extends AbstractEvent {
    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            FontChangedListener fontChangeListener = (FontChangedListener) listener;
            fontChangeListener.onFontChanged(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<FontChangedListener> GetListenerClassType() {
        return FontChangedListener.class;
    }
}