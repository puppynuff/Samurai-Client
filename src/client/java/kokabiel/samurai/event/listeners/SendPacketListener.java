package kokabiel.samurai.event.listeners;

import kokabiel.samurai.event.events.SendPacketEvent;

public interface SendPacketListener {
    public abstract void onSendPacket(SendPacketEvent event);
}
