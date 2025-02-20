/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.components;

import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.Size;
import kokabiel.samurai.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;

public class SeparatorComponent extends Component {

	public SeparatorComponent() {
		super();
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 1.0f);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		Render2D.drawLine(drawContext.getMatrices().peek().getPositionMatrix(), actualX, actualY, actualX + actualWidth,
				actualY + actualHeight, GuiManager.borderColor.getValue());
	}
}
