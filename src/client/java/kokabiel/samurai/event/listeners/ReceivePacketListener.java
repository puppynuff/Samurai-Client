package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.ReceivePacketEvent;

public interface ReceivePacketListener {
    public abstract void onReceivePacket(ReceivePacketEvent readPacketEvent);
}
