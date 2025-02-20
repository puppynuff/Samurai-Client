package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.MouseClickEvent;

public interface MouseClickListener extends AbstractListener {
    public abstract void onMouseClick(MouseClickEvent mouseClickEvent);
}