package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.KeyDownEvent;

public interface KeyDownListener extends AbstractListener {
    public abstract void onKeyDown(KeyDownEvent event);
}
