package kokabiel.samurai.event.listeners;


import kokabiel.samurai.event.events.KeyUpEvent;

public interface KeyUpListener extends AbstractListener {
    public abstract void onKeyUp(KeyUpEvent event);
}