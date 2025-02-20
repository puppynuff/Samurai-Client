package kokabiel.samurai.module.modules.movement;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.TickEvent;
import kokabiel.samurai.event.listeners.TickListener;
import kokabiel.samurai.module.AntiCheat;
import kokabiel.samurai.module.Category;
import kokabiel.samurai.module.Module;
import kokabiel.samurai.settings.types.BooleanSetting;
import kokabiel.samurai.settings.types.FloatSetting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module implements TickListener {
    private final FloatSetting speedSetting = FloatSetting.builder().id("speed_setting").displayName("Speed").description("Sets your speed stat").defaultValue(1f).defaultValue(1f).maxValue(60f).step(1f).build();
    private final BooleanSetting exponentialSetting = BooleanSetting.builder().id("exponential_setting").displayName("Exponential Speed").description("Causes the speed setting to be exponention").defaultValue(false).build();

    public Speed() {
        super("Speed");
        this.setCategory(Category.of("Movement"));
        this.setDescription("Sets your speed stat (Increases players movement-speed");

        speedSetting.addOnUpdate((i) -> {
            if(state.getValue()) {
                EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                attribute.setBaseValue(speedSetting.getValue());
            }
        });

        this.addSetting(speedSetting);
        this.addSetting(exponentialSetting);

        this.setDetectable(AntiCheat.NoCheatPlus);
        this.setDetectable(AntiCheat.Vulcan);
        this.setDetectable(AntiCheat.AdvancedAntiCheat);
        this.setDetectable(AntiCheat.Verus);
        this.setDetectable(AntiCheat.Grim);
        this.setDetectable(AntiCheat.Matrix);
        this.setDetectable(AntiCheat.Negativity);
        this.setDetectable(AntiCheat.Karhu);
    }

    @Override
    public void onDisable() {
        MC.options.getFovEffectScale().setValue(1.0);
        if(MC.player != null) {
            EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            attribute.setBaseValue(0.1);
        }
        Samurai.getInstance().eventManager.RemoveListener(TickListener.class, this);
    }

    @Override
    public void onEnable() {
        MC.options.getFovEffectScale().setValue(0d);
        if(exponentialSetting.getValue() == false) {
            EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            attribute.setBaseValue(speedSetting.getValue() / 10f);

        }
        Samurai.getInstance().eventManager.AddListener(TickListener.class, this);
    }


    @Override
    public void onToggle() {}

    @Override
    public void onTick(TickEvent.Pre event)  {
        if(exponentialSetting.getValue()) {
            if (MC.player.forwardSpeed == 0 && MC.player.sidewaysSpeed == 0)
                return;

            if (!MC.player.isOnGround())
                return;

            if (MC.player.forwardSpeed > 0 && !MC.player.horizontalCollision)
                MC.player.setSprinting(true);

            Vec3d velocity = MC.player.getVelocity();

            MC.player.setVelocity(velocity.x * speedSetting.getValue() / 10, velocity.y, velocity.z * speedSetting.getValue() / 10);
        } else {
            EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            attribute.setBaseValue(speedSetting.getValue() / 10f);
        }
    }

    @Override
    public void onTick(TickEvent.Post event) {}
}
