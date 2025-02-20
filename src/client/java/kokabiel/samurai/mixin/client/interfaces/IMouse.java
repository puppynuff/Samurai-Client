package kokabiel.samurai.mixin.client.interfaces;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mouse.class)
public interface IMouse {
    @Invoker("onCursorPos")
    void executeonCursorPos(long window, double x, double y);

    @Invoker("onMouseScroll")
    void executeOnMouseScroll(long window, double horizontal, double vertical);

    @Invoker("onMouseButton")
    void executeOnMouseButton(long window, int button, int action, int mods);
}