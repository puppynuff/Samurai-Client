package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.TickEvent;

public interface TickListener extends AbstractListener {
    public abstract void onTick(TickEvent.Pre event);
    public abstract void onTick(TickEvent.Post event);
}
