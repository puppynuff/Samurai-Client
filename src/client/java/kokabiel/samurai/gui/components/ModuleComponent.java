/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.components;

import static kokabiel.samurai.utils.render.TextureBank.gear;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.MouseClickEvent;
import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.Margin;
import kokabiel.samurai.gui.Size;
import kokabiel.samurai.gui.colors.Color;
import kokabiel.samurai.gui.navigation.CloseableWindow;
import kokabiel.samurai.module.Module;
import kokabiel.samurai.settings.Setting;
import kokabiel.samurai.settings.types.BlocksSetting;
import kokabiel.samurai.settings.types.BooleanSetting;
import kokabiel.samurai.settings.types.ColorSetting;
import kokabiel.samurai.settings.types.EnumSetting;
import kokabiel.samurai.settings.types.FloatSetting;
import kokabiel.samurai.utils.render.Render2D;
import kokabiel.samurai.utils.types.MouseAction;
import kokabiel.samurai.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;

public class ModuleComponent extends Component {
	private Module module;

	private CloseableWindow lastSettingsTab = null;
	private boolean spinning = false;
	private float spinAngle = 0;

	public ModuleComponent(Module module) {
		super();

		this.header = module.getName();
		this.module = module;
		this.tooltip = module.getDescription();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
	}

	@Override
	public void update() {
		super.update();
		if (spinning) {
			spinAngle = (spinAngle + 5) % 360;
		}
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();

		if (this.header != null) {
			if (module.isDetectable(SamuraiClientlient.moduleManager.antiCheat.getValue())) {
				Render2D.drawString(drawContext, this.header, actualX, actualY + 8, Colors.GRAY);
			} else {
				Render2D.drawString(drawContext, this.header, actualX, actualY + 8, module.state.getValue() ? 0x00FF00
						: this.hovered ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);
			}
		}

		if (module.hasSettings()) {
			Color hudColor = GuiManager.foregroundColor.getValue();

			if (spinning) {
				matrixStack.push();
				matrixStack.translate((actualX + actualWidth - 8), (actualY + 14), 0);
				matrixStack.multiply(new Quaternionf().rotateZ((float) Math.toRadians(spinAngle)));
				matrixStack.translate(-(actualX + actualWidth - 8), -(actualY + 14), 0);
				Render2D.drawTexturedQuad(matrixStack.peek().getPositionMatrix(), gear, (actualX + actualWidth - 16),
						(actualY + 6), 16, 16, hudColor);
				matrixStack.pop();
			} else {
				Render2D.drawTexturedQuad(matrix4f, gear, (actualX + actualWidth - 16), (actualY + 6), 16, 16,
						hudColor);
			}
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				float mouseX = (float) event.mouseX;
				float actualX = actualSize.getX();
				float actualY = actualSize.getY();
				float actualWidth = actualSize.getWidth();

				boolean isOnOptionsButton = (mouseX >= (actualX + actualWidth - 34)
						&& mouseX <= (actualX + actualWidth));
				if (isOnOptionsButton) {
					spinning = true;
					if (lastSettingsTab == null) {
						lastSettingsTab = new CloseableWindow(this.module.getName(), actualX + actualWidth + 1,
								actualY);
						lastSettingsTab.setMinWidth(320.0f);
						// lastSettingsTab.setInheritHeightFromChildren(true);
						StackPanelComponent stackPanel = new StackPanelComponent();

						StringComponent titleComponent = new StringComponent(module.getName() + " Settings");
						titleComponent.setIsHitTestVisible(false);
						stackPanel.addChild(titleComponent);

						stackPanel.addChild(new SeparatorComponent());

						KeybindComponent keybindComponent = new KeybindComponent(module.getBind());
						// keybindComponent.setSize(new Rectangle(null, null, null, 30f));

						stackPanel.addChild(keybindComponent);

						for (Setting<?> setting : this.module.getSettings()) {
							if (setting == this.module.state)
								continue;

							Component c;
							if (setting instanceof FloatSetting) {
								c = new SliderComponent((FloatSetting) setting);
							} else if (setting instanceof BooleanSetting) {
								c = new CheckboxComponent((BooleanSetting) setting);
								// }else if (setting instanceof StringListSetting) {
								// c = new ListComponent(stackPanel, (IndexedStringListSetting) setting);
							} else if (setting instanceof ColorSetting) {
								c = new ColorPickerComponent((ColorSetting) setting);
							} else if (setting instanceof BlocksSetting) {
								c = new BlocksComponent((BlocksSetting) setting);
							} else if (setting instanceof EnumSetting) {
								c = new EnumComponent<>((EnumSetting) setting);
							} else {
								c = null;
							}

							if (c != null) {
								stackPanel.addChild(c);
							}
						}

						lastSettingsTab.addChild(stackPanel);

						lastSettingsTab.setOnClose(() -> {
							spinning = false;
						});

						lastSettingsTab.setMinWidth(250.0f);
						lastSettingsTab.setMaxWidth(600f);
						Samurai.getInstance().guiManager.addWindow(lastSettingsTab, "Modules");
						lastSettingsTab.initialize();
						spinning = true;
					} else {
						Samurai.getInstance().guiManager.removeWindow(lastSettingsTab, "Modules");
						spinning = false;
						lastSettingsTab = null;
					}
				} else {
					if (!module.isDetectable(SamuraiClientlient.moduleManager.antiCheat.getValue()))
						module.toggle();
				}

				event.cancel();
			}
		}
	}
}
