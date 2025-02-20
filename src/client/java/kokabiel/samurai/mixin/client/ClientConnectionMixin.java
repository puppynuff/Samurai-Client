package kokabiel.samurai.mixin.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.ReceivePacketEvent;
import kokabiel.samurai.event.events.SendPacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.handler.PacketSizeLogger;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @SuppressWarnings("all")
    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal = 0)}, method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
    protected void onChannelRead(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        ReceivePacketEvent event = new ReceivePacketEvent(packet);
        Samurai.getInstance().eventManager.Fire(event);
    }

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", cancellable = true)
    private void onSend(Packet<?> packet, @Nullable PacketCallbacks callback, CallbackInfo ci) {
        SendPacketEvent event = new SendPacketEvent(packet);
        Samurai.getInstance().eventManager.Fire(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "addHandlers", at = @At("RETURN"))
    private static void addHandlersHook(ChannelPipeline pipeline, NetworkSide side, boolean local, PacketSizeLogger packetSizeLogger, CallbackInfo ci) {
        // This is useful if I ever elect to add proxies.
    }
}
