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

public class NetherCoordsHud extends HudWindow {

	private static final MinecraftClient MC = MinecraftClient.getInstance();

	public NetherCoordsHud(int x, int y) {
		super("NetherCoordsHud", x, y, 50, 24);
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
				String coordsText = String.format("X: %.1f, Y: %.1f, Z: %.1f", MC.player.getX() * 8, MC.player.getY(),
						MC.player.getZ() * 8);
				Render2D.drawString(drawContext, coordsText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}

		super.draw(drawContext, partialTicks);
	}
}
