package kokabiel.samurai.event.events;

import kokabiel.samurai.event.listeners.AbstractListener;
import kokabiel.samurai.event.listeners.SendPacketListener;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class SendPacketEvent extends AbstractEvent {
    private final Packet<?> packet;

    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for(AbstractListener listener : List.copyOf(listeners)) {
            SendPacketListener sendPacketListener = (SendPacketListener) listener;
            sendPacketListener.onSendPacket(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<SendPacketListener> GetListenerClassType() {
        return SendPacketListener.class;
    }
}
