/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.navigation.huds;

import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.Rectangle;
import kokabiel.samurai.gui.ResizeMode;
import kokabiel.samurai.gui.navigation.HudWindow;
import kokabiel.samurai.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FPSHud extends HudWindow {

	private static final MinecraftClient MC = MinecraftClient.getInstance();

	public FPSHud(int x, int y) {
		super("FPSHud", x, y, 50, 24);
		this.minWidth = 50f;
		this.minHeight = 20f;
		this.maxHeight = 20f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				int fps = MC.getCurrentFps();
				String fpsText = "FPS: " + fps;
				Render2D.drawString(drawContext, fpsText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}
		super.draw(drawContext, partialTicks);
	}
}