package kokabiel.samurai.event.events;

import kokabiel.samurai.event.listeners.AbstractListener;
import kokabiel.samurai.event.listeners.Render3DListener;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class Render3DEvent extends AbstractEvent {
    MatrixStack matrices;
    Frustum frustum;
    RenderTickCounter renderTickCounter;

    public MatrixStack GetMatrix() {
        return matrices;
    }

    public RenderTickCounter getRenderTickCounter() {
        return renderTickCounter;
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public Render3DEvent(MatrixStack matrix4f, Frustum frustum, RenderTickCounter renderTickCounter) {
        this.matrices = matrix4f;
        this.renderTickCounter = renderTickCounter;
        this.frustum = frustum;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            Render3DListener renderListener = (Render3DListener) listener;
            renderListener.onRender(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Render3DListener> GetListenerClassType() {
        return Render3DListener.class;
    }
}