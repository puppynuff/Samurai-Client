/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.components;

import java.util.List;

import org.joml.Matrix4f;

import kokabiel.samurai.event.events.MouseClickEvent;
import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.Margin;
import kokabiel.samurai.gui.Size;
import kokabiel.samurai.gui.UIElement;
import kokabiel.samurai.gui.colors.Color;
import kokabiel.samurai.utils.render.Render2D;
import kokabiel.samurai.utils.types.MouseAction;
import kokabiel.samurai.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ButtonComponent extends Component {

	private Runnable onClick;

	/**
	 * Constructor for button component.
	 *
	 * @param onClick OnClick delegate that will run when the button is pressed.
	 */
	public ButtonComponent(Runnable onClick) {
		super();

		this.setMargin(new Margin(8f, 2f, 8f, 2f));

		this.onClick = onClick;
	}

	@Override
	public void measure(Size availableSize) {
		if (!isVisible()) {
			preferredSize = Size.ZERO;
			return;
		}

		if (initialized) {
			float finalWidth = 0;
			float finalHeight = 0;

			List<UIElement> children = getChildren();
			for (UIElement element : children) {
				if (!element.isVisible())
					continue;

				element.measure(availableSize);
				Size resultingSize = element.getPreferredSize();

				if (resultingSize.getWidth() > finalWidth)
					finalWidth = resultingSize.getWidth();

				if (resultingSize.getHeight() > finalHeight)
					finalHeight = resultingSize.getHeight();
			}

			if (margin != null) {

				Float marginLeft = margin.getLeft();
				Float marginTop = margin.getTop();
				Float marginRight = margin.getRight();
				Float marginBottom = margin.getBottom();

				if (marginLeft != null)
					finalWidth += marginLeft;

				if (marginRight != null)
					finalWidth += marginRight;

				if (marginTop != null)
					finalHeight += marginTop;

				if (marginBottom != null)
					finalHeight += marginBottom;
			}

			if (minWidth != null && finalWidth < minWidth) {
				finalWidth = minWidth;
			} else if (maxWidth != null && finalWidth > maxWidth) {
				finalWidth = maxWidth;
			}

			if (minHeight != null && finalHeight < minHeight) {
				finalHeight = minHeight;
			} else if (maxHeight != null && finalHeight > maxHeight) {
				finalHeight = maxHeight;
			}

			preferredSize = new Size(finalWidth, finalHeight);
		}
	}

	/**
	 * Sets the OnClick delegate of the button.
	 *
	 * @param onClick Delegate to set.
	 */
	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}

	/**
	 * Draws the button to the screen.
	 *
	 * @param drawContext  The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 */
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		Color color = GuiManager.foregroundColor.getValue();
		if (hovered) {
			color = color.add(45, 45, 45);
		}

		Render2D.drawOutlinedRoundedBox(matrix4f, actualX, actualY, actualWidth, actualHeight, 3.0f,
				GuiManager.borderColor.getValue(), color);

		super.draw(drawContext, partialTicks);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (this.hovered) {
				if (onClick != null)
					onClick.run();
				event.cancel();
			}
		}
	}
}
