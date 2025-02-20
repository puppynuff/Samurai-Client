package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.Render2DEvent;

public interface Render2DListener extends AbstractListener {
    public abstract void onRender(Render2DEvent event);
}
