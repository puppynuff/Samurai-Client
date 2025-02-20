package kokabiel.samurai.event.events;

import kokabiel.samurai.event.listeners.AbstractListener;
import kokabiel.samurai.event.listeners.ReceivePacketListener;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class ReceivePacketEvent extends AbstractEvent {
    private Packet<?> packet;

    public Packet<?> getPacket() {
        return packet;
    }

    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            ReceivePacketListener readPacketListener = (ReceivePacketListener) listener;
            readPacketListener.onReceivePacket(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ReceivePacketListener> GetListenerClassType() {
        return ReceivePacketListener.class;
    }
}
