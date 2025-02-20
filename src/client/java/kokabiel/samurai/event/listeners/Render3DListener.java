package kokabiel.samurai.event.listeners;


import kokabiel.samurai.event.events.Render3DEvent;

public interface Render3DListener extends AbstractListener {
    public abstract void onRender(Render3DEvent event);
}