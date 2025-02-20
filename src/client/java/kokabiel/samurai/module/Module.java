package kokabiel.samurai.module;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.SamuraiClient;
import kokabiel.samurai.interfaces.IClientPlayerInteractionManager;
import kokabiel.samurai.managers.SettingManager;
import kokabiel.samurai.settings.Setting;
import kokabiel.samurai.settings.types.BooleanSetting;
import kokabiel.samurai.settings.types.KeybindSetting;
import kokabiel.samurai.utils.FindItemResult;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public abstract class Module {
    private String name;
    private String description;
    private Category category;

    public final BooleanSetting state;
    public final KeybindSetting keyBind;
    private final List<Setting<?>> settings = new ArrayList<Setting<?>>();

    private final HashSet<AntiCheat> knownDetectable = new HashSet<AntiCheat>();

    protected static final MinecraftClient MC = SamuraiClient.MC;
    protected final SamuraiClient SAMURAI_CLIENT = Samurai.getInstance();

    public Module(String name) {
        this(name, InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0));
    }

    public Module(String name, Key keybind) {
        this.name = name;
        this.keyBind = KeybindSetting.builder().id("key." + name.toLowerCase()).displayName(name + " Key")
                .defaultValue(keybind).build();

        this.state = BooleanSetting.builder().id("state." + name.toLowerCase()).displayName(name + " State")
                .defaultValue(false).onUpdate(s -> {
                    if (s) this.onEnable();
                    else this.onDisable();
                    this.onToggle();
                }).build();

        this.addSetting(keyBind);
        this.addSetting(state);

        SettingManager.registerSetting(this.keyBind);
        SettingManager.registerSetting(this.state);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public KeybindSetting getBind() {
        return this.keyBind;
    }

    public void addSetting(Setting<?> setting) {
        this.settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    public abstract void onDisable();

    public abstract void onEnable();

    public abstract void onToggle();

    public boolean isKeyPressed(int button) {
        if(button == -1) return false;

        if(button < 10) return false;

        return InputUtil.isKeyPressed(MC.getWindow().getHandle(), button);
    }

    public void toggle() {
        if(isDetectable(SAMURAI_CLIENT.moduleManager.antiCheat.getValue())) state.setValue(false);
        else state.setValue(!state.getValue());
    }

    public boolean isDetectable(AntiCheat anticheat) {
        return knownDetectable.contains(anticheat);
    }

    public void setDetectable(AntiCheat anticheat) {
        setDetectable(anticheat, true);
    }

    public void setDetectable(AntiCheat anticheat, boolean state) {
        if(state) knownDetectable.add(anticheat);
        else knownDetectable.remove(anticheat);
    }

    public String getStatus() {
        return this.state.getValue() ? "Enabled" : "Disabled";
    }

    public String getKeyBindDisplayName() {
        return keyBind.displayName;
    }

    public void resetSettings() {
        for(Setting<?> setting: settings) {
            setting.resetToDefault();
        }
    }

    public final boolean isCategory(Category category) {
        return category.equals(this.category);
    }


    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModInfo {
        String name();
        String description();
        String category();
        int bind();
    }

    public static int previousSlot = -1;

    public static FindItemResult findInHotbar(Item... items) {
        return findInHotbar(itemStack -> {
            for(Item item: items) {
                if(itemStack.getItem() == item) return true;
            }
            return false;
        });
    }

    public static FindItemResult findInHotbar(Predicate<ItemStack> isGood) {
        if (testInOffHand(isGood)) {
            return new FindItemResult(45, MC.player.getOffHandStack().getCount());
        }

        if (testInMainHand(isGood)) {
            return new FindItemResult(MC.player.getInventory().selectedSlot, MC.player.getMainHandStack().getCount());
        }

        return find(isGood, 0, 8);
    }

    public static FindItemResult find(Predicate<ItemStack> isGood) {
        if (MC.player == null) {
            return new FindItemResult(0, 0);
        }

        return find(isGood, 0, MC.player.getInventory().size());
    }

    public static FindItemResult find(Predicate<ItemStack> isGood, int start, int end) {
        if (MC.player == null) {
            return new FindItemResult(0, 0);
        }

        int slot = -1;
        int count = 0;

        for (int i = start; i <= end; i++) {
            ItemStack stack = MC.player.getInventory().getStack(i);

            if (isGood.test(stack)) {
                if (slot == -1) {
                    slot = i;
                }
                count += stack.getCount();
            }
        }

        return new FindItemResult(slot, count);
    }

    public static FindItemResult findFastestTool(BlockState state) {
        float bestScore = 1;
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = MC.player.getInventory().getStack(i);

            if (stack.isSuitableFor(state)) {
                float score = stack.getMiningSpeedMultiplier(state);

                if (score > bestScore) {
                    bestScore = score;
                    slot = i;
                }
            }
        }

        return new FindItemResult(slot, 1);
    }

    public static boolean testInMainHand(Predicate<ItemStack> predicate) {
        return predicate.test(MC.player.getMainHandStack());
    }

    public static boolean testInOffHand(Predicate<ItemStack> predicate) {
        return predicate.test(MC.player.getOffHandStack());
    }

    public static boolean swap(int slot, boolean swapBack) {
        if (slot == 45) {
            return true;
        }

        if (slot < 0 || slot > 8) {
            return false;
        }

        if (swapBack) {
            if (previousSlot == -1) {
                previousSlot = MC.player.getInventory().selectedSlot;
            }
        } else {
            previousSlot = -1;
        }

        MC.player.getInventory().selectedSlot = slot;
        ((IClientPlayerInteractionManager) MC.interactionManager).samurai$syncSelected();
        return true;
    }

    public static boolean swapBack() {
        if (previousSlot == -1) {
            return false;
        }

        boolean result = swap(previousSlot, false);
        previousSlot = -1;
        return result;
    }

    public static FindItemResult find(Item... items) {
        return find(itemStack -> {
            for (Item item : items) {
                if (itemStack.getItem() == item)
                    return true;
            }
            return false;
        });
    }

    public static void rotatePitch(float degrees) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null) {
            float currentPitch = player.getPitch();
            float newPitch = currentPitch + degrees;

            newPitch = Math.max(-90.0F, Math.min(90.0F, newPitch));

            player.setPitch(newPitch);

            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.LookAndOnGround(player.getYaw(), newPitch, player.isOnGround(), false));
        }
    }

    public static void sendChatMessage(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.DARK_PURPLE + "[" + Formatting.LIGHT_PURPLE + "Aoba"
                    + Formatting.DARK_PURPLE + "] " + Formatting.RESET + message));
        }
    }
}