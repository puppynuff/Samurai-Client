package kokabiel.samurai.event.events;

import com.mojang.logging.LogUtils;
import kokabiel.samurai.event.listeners.AbstractListener;
import kokabiel.samurai.event.listeners.Render2DListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Render2DEvent extends AbstractEvent {
    private DrawContext matrices;
    private RenderTickCounter renderTickCounter;

    public DrawContext getDrawContext() {
        return matrices;
    }
    public RenderTickCounter getRenderTickCounter() {
        return renderTickCounter;
    }


    public Render2DEvent(DrawContext context, RenderTickCounter renderTickCounter) {
        this.matrices = context;
        this.renderTickCounter = renderTickCounter;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for(AbstractListener listener : List.copyOf(listeners)) {
            Render2DListener renderListener = (Render2DListener) listener;
            renderListener.onRender(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Render2DListener> GetListenerClassType() {
        return Render2DListener.class;
    }
}