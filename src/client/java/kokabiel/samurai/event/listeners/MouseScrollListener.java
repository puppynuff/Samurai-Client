package kokabiel.samurai.event.listeners;


import kokabiel.samurai.event.events.MouseScrollEvent;

public interface MouseScrollListener extends AbstractListener {
    public abstract void onMouseScroll(MouseScrollEvent event);
}