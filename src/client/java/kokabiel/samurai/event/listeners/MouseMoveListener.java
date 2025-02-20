package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.MouseMoveEvent;

public interface MouseMoveListener extends AbstractListener {
    public abstract void onMouseMove(MouseMoveEvent mouseMoveEvent);
}