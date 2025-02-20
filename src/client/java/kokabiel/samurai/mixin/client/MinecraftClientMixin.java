package kokabiel.samurai.mixin.client;


import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.SamuraiClient;
import kokabiel.samurai.event.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    private int itemUseCooldown;
    @Shadow
    @Final
    private Session session;

    @Shadow
    @Final
    private Mouse mouse;

    @Shadow
    public ClientWorld world;

    @Shadow
    public ClientPlayerEntity player;

    private Session aobaSession;

    @Shadow
    public abstract boolean isWindowFocused();

    @Shadow
    @Final
    public GameOptions options;

    @Inject(at = @At("HEAD"), method = "onFinishedLoading(Lnet/minecraft/client/MinecraftClient$LoadingContext;)V")
    private void onfinishedloading(CallbackInfo info) {
        Samurai.getInstance().loadAssets();
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void onPreTick(CallbackInfo info) {
        if (this.world != null && player != null) {
            TickEvent.Pre updateEvent = new TickEvent.Pre();
            Samurai.getInstance().eventManager.Fire(updateEvent);
        }
    }

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void onPostTick(CallbackInfo info) {
        if (this.world != null && player != null) {
            TickEvent.Post updateEvent = new TickEvent.Post();
            Samurai.getInstance().eventManager.Fire(updateEvent);
        }
    }

    @Inject(at = { @At("HEAD") }, method = { "getSession()Lnet/minecraft/client/session/Session;" }, cancellable = true)
    private void onGetSession(CallbackInfoReturnable<Session> cir) {
        if (aobaSession == null)
            return;
        cir.setReturnValue(aobaSession);
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;session:Lnet/minecraft/client/session/Session;", opcode = Opcodes.GETFIELD, ordinal = 0), method = {
            "getSession()Lnet/minecraft/client/session/Session;" })
    private Session getSessionForSessionProperties(MinecraftClient mc) {
        if (aobaSession != null)
            return aobaSession;
        return session;
    }

    @Inject(at = { @At(value = "HEAD") }, method = { "close()V" })
    private void onClose(CallbackInfo ci) {
        try {
            Samurai.getInstance().endClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(at = { @At(value = "HEAD") }, method = { "openGameMenu(Z)V" })
    private void onOpenPauseMenu(boolean pause, CallbackInfo ci) {
        SamuraiClient aoba = Samurai.getInstance();

        if (aoba.guiManager != null) {
            Samurai.getInstance().guiManager.setClickGuiOpen(false);
        }
    }

    // TODO: InactivityFrameLimiter class... i guess.. :/
    /*
     * @Inject(method = "getCurrentFps", at = @At("HEAD"), cancellable = true)
     * private void onGetCurrentFps(CallbackInfoReturnable<Integer> info) { if
     * (Aoba.getInstance().moduleManager != null) { FocusFps focusfps = (FocusFps)
     * Aoba.getInstance().moduleManager.focusfps; if (focusfps.state.getValue() &&
     * !isWindowFocused()) {
     * info.setReturnValue(Math.min(focusfps.getFps().intValue(),
     * this.options.getMaxFps().getValue())); } } }
     */
}