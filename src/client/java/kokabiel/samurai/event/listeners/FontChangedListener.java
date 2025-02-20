package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.FontChangedEvent;

public interface FontChangedListener extends AbstractListener {
    public abstract void onFontChanged(FontChangedEvent event);
}