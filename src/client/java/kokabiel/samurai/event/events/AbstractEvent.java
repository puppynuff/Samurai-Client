package kokabiel.samurai.event.events;

import kokabiel.samurai.event.listeners.AbstractListener;

import java.util.ArrayList;

public abstract class AbstractEvent {
    boolean isCancelled = false;

    public AbstractEvent() {
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        isCancelled = true;
    }

    public abstract void Fire(ArrayList<? extends AbstractListener> listeners);

    public abstract <T extends AbstractListener> Class<T> GetListenerClassType();
}
