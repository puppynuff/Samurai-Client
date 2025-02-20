/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.components;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.KeyDownEvent;
import kokabiel.samurai.event.events.MouseClickEvent;
import kokabiel.samurai.event.listeners.KeyDownListener;
import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.Margin;
import kokabiel.samurai.gui.Size;
import kokabiel.samurai.gui.colors.Color;
import kokabiel.samurai.settings.types.KeybindSetting;
import kokabiel.samurai.utils.render.Render2D;
import kokabiel.samurai.utils.types.MouseAction;
import kokabiel.samurai.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;

public class KeybindComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;
	private KeybindSetting keyBind;

	public KeybindComponent(KeybindSetting keyBind) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.keyBind = keyBind;
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (this.isVisible()) {
			Samurai.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Samurai.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		Render2D.drawString(drawContext, "Keybind", actualX, actualY + 8, 0xFFFFFF);
		Render2D.drawOutlinedRoundedBox(matrix4f, actualX + actualWidth - 100, actualY, 100, actualHeight, 3.0f,
				GuiManager.borderColor.getValue(), new Color(115, 115, 115, 200));

		String keyBindText = this.keyBind.getValue().getLocalizedText().getString();
		if (keyBindText.equals("scancode.0") || keyBindText.equals("key.keyboard.0"))
			keyBindText = "N/A";

		Render2D.drawString(drawContext, keyBindText, actualX + actualWidth - 90, actualY + 6, 0xFFFFFF);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				setListeningForKey(true);
				event.cancel();
			} else {
				setListeningForKey(false);
			}
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			int key = event.GetKey();
			int scanCode = event.GetScanCode();

			if (key == GLFW.GLFW_KEY_ESCAPE) {
				keyBind.setValue(InputUtil.UNKNOWN_KEY);
			} else {
				keyBind.setValue(InputUtil.fromKeyCode(key, scanCode));
			}

			listeningForKey = false;

			event.cancel();
		}
	}

	private void setListeningForKey(boolean state) {
		listeningForKey = state;
		if (listeningForKey) {
			Samurai.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Samurai.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}
}