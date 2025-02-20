package kokabiel.samurai.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import kokabiel.samurai.Samurai;
import kokabiel.samurai.SamuraiClient;
import kokabiel.samurai.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private Camera camera;

    @Shadow
    private MinecraftClient client;

    @Inject(at = { @At("HEAD") }, method = {
            "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V" }, cancellable = true)
    private void onBobViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
        SamuraiClient samurai = Samurai.getInstance();
//        if (aoba != null && aoba.moduleManager.norender.state.getValue()) {
//            ci.cancel();
//        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0), method = {
            "renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V" })
    private float nauseaLerp(float delta, float first, float second) {
        SamuraiClient samurai = Samurai.getInstance();
//        if (aoba != null && aoba.moduleManager.norender.state.getValue())
//            return 0;

        return MathHelper.lerp(delta, first, second);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {
            "ldc=hand" }))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 2) Matrix4f matrix4f3,
                               @Local(ordinal = 1) float tickDelta, @Local MatrixStack matrixStack) {
        if (client == null && client.world == null && client.player == null) {
            return;
        }

        Render2D.updateScreenCenter();
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo info) {
        SamuraiClient samurai = Samurai.getInstance();
        if (samurai == null)
            return;

//        NoRender norender = (NoRender) aoba.moduleManager.norender;
//        if (floatingItem.getItem() == Items.TOTEM_OF_UNDYING && norender.state.getValue()
//                && norender.getNoTotemAnimation()) {
//            info.cancel();
//        }
    }
}