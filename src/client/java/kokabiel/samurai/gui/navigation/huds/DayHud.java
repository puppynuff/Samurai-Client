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
import net.minecraft.client.gui.DrawContext;

public class DayHud extends HudWindow {
	private String timeText = null;

	public DayHud(int x, int y) {
		super("DayHud", x, y, 50, 24);
		this.minWidth = 50f;
		this.minHeight = 24f;
		this.maxHeight = 24f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void update() {
		super.update();
		timeText = "Day: " + (int) (MC.world.getTime() / 24000);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (timeText != null && isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				Render2D.drawString(drawContext, timeText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}

		super.draw(drawContext, partialTicks);
	}
}