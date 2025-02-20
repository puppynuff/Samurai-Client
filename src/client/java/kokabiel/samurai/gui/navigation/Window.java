/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.navigation;

import java.util.List;

import com.mojang.logging.LogUtils;
import org.joml.Matrix4f;

import kokabiel.samurai.event.events.MouseClickEvent;
import kokabiel.samurai.event.events.MouseMoveEvent;
import kokabiel.samurai.gui.Direction;
import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.Rectangle;
import kokabiel.samurai.gui.ResizeMode;
import kokabiel.samurai.gui.Size;
import kokabiel.samurai.gui.UIElement;
import kokabiel.samurai.managers.SettingManager;
import kokabiel.samurai.settings.types.RectangleSetting;
import kokabiel.samurai.utils.input.CursorStyle;
import kokabiel.samurai.utils.render.Render2D;
import kokabiel.samurai.utils.types.MouseAction;
import kokabiel.samurai.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class Window extends UIElement {
	protected String ID;
	protected Page parentPage;

	public RectangleSetting position;

	public boolean isMoving = false;
	public boolean isResizing = false;

	public boolean moveable = true;
	public ResizeMode resizeMode = ResizeMode.WidthAndHeight;

	public Direction grabDirection = Direction.None;

	public Window(String ID, float x, float y) {
		this(ID, x, y, 180f, 50f);
	}

	public Window(String ID, float x, float y, float width, float height) {
		super();
		this.ID = ID;
		minWidth = 180.0f;
		minHeight = 50.0f;

		visible = false;
		position = RectangleSetting.builder().id(ID + "_position").displayName(ID + "Position")
				.defaultValue(new Rectangle(x, y, width, height)).onUpdate((Rectangle vec) -> {
					setSize(vec.getWidth(), vec.getHeight());
					invalidateArrange();
				}).build();

		setSize(getWidth(), getHeight());
		SettingManager.registerGlobalSetting(position);
	}

	@Override
	public Rectangle getActualSize() {
		Rectangle newSize = actualSize;
		if (position.getX() != null)
			newSize.setX(position.getX());

		if (position.getY() != null)
			newSize.setY(position.getY());

		return newSize;
	}

	@Override
	public void setWidth(Float width) {
		this.position.setWidth(width);
	}

	@Override
	public void setHeight(Float height) {
		this.position.setHeight(height);
	}

	public String getID() {
		return ID;
	}

	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		// Draws background depending on components width and height
		Render2D.drawRoundedBox(matrix4f, actualX, actualY, actualWidth, actualHeight,
				GuiManager.roundingRadius.getValue(), GuiManager.backgroundColor.getValue());
		Render2D.drawRoundedBoxOutline(matrix4f, actualX, actualY, actualWidth, actualHeight,
				GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue());

		List<UIElement> children = getChildren();
		for (UIElement child : children) {
			child.draw(drawContext, partialTicks);
		}
	}

	@Override
	protected Size getStartingSize(Size availableSize) {
		// Account for minimum size.

		return availableSize;
	}

	protected void setResizing(boolean state, MouseClickEvent event, Direction direction) {
		if (state) {
			parentPage.moveToFront(this);
			switch (direction) {
			case Left:
			case Right:
				GuiManager.setCursor(CursorStyle.HorizonalResize);
				break;
			case Top:
			case Bottom:
				GuiManager.setCursor(CursorStyle.VerticalResize);
				break;
			case None:
			default:
				break;
			}
			event.cancel();
		}
		isMoving = false;
		isResizing = state;
		grabDirection = direction;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// Propagate to children ONLY if the user is not moving or resizing the window.
		if (!isMoving && !isResizing) {
			super.onMouseMove(event);
		}

		if (!event.isCancelled() && isVisible()) {
			double mouseX = event.getX();
			double mouseY = event.getY();
			double mouseDeltaX = event.getDeltaX();
			double mouseDeltaY = event.getDeltaY();

			Rectangle pos = getActualSize();

			if (this.isMoving) {
//				float targetX = pos.getX() + (float) mouseX;
//				float targetY = pos.getY() + (float) mouseY;

//				float currentX = this.position.getX();
//				float currentY = this.position.getY();

//				float interpolatedX = lerp(currentX, targetX, GuiManager.dragSmoothening.getValue());
//				float interpolatedY = lerp(currentY, targetY, GuiManager.dragSmoothening.getValue());

				position.setX((float) mouseX);
				position.setY((float) mouseY);
			} else if (this.isResizing) {
				switch (grabDirection) {
				case Direction.Top:
					float newHeightTop = getActualSize().getHeight() - (float) mouseDeltaY;

					if (minHeight != null && newHeightTop < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightTop > maxHeight.floatValue())
						break;

					position.setY(getActualSize().getY() + (float) mouseDeltaY);
					position.setHeight(newHeightTop);
					break;
				case Direction.Bottom:
					float newHeightBottom = getActualSize().getHeight() + (float) mouseDeltaY;

					if (minHeight != null && newHeightBottom < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightBottom > maxHeight.floatValue())
						break;

					position.setHeight(newHeightBottom);
					break;
				case Direction.Left:
					float newWidthLeft = getActualSize().getWidth() - (float) mouseDeltaX;
					if (minWidth != null && newWidthLeft < minWidth.floatValue())
						break;

					if (maxWidth != null && newWidthLeft > maxWidth.floatValue())
						break;

					position.setX(getActualSize().getX() + (float) mouseDeltaX);
					position.setWidth(newWidthLeft);
					break;
				case Direction.Right:
					float newWidthRight = getActualSize().getWidth() + (float) mouseDeltaX;
					if (minWidth != null && newWidthRight < minWidth.floatValue())
						break;

					if (maxWidth != null && newWidthRight > maxWidth.floatValue())
						break;
					position.setWidth(newWidthRight);
					break;
				default:
					break;
				}
			}
		}
	}

	public void onMouseClick(MouseClickEvent event) {
		// Propagate to children.
		super.onMouseClick(event);

		// Check to see if the event is cancelled. If not, execute branch.
		if (!event.isCancelled()) {
			if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				Rectangle pos = getActualSize();

				if (resizeMode != ResizeMode.None) {
					Rectangle topHitbox = new Rectangle(pos.getX(), pos.getY() - 8, pos.getWidth(), 8.0f);
					Rectangle leftHitbox = new Rectangle(pos.getX() - 8, pos.getY(), 8.0f, pos.getHeight());
					Rectangle rightHitbox = new Rectangle(pos.getX() + pos.getWidth(), pos.getY(), 8.0f,
							pos.getHeight());
					Rectangle bottomHitbox = new Rectangle(pos.getX(), pos.getY() + pos.getHeight(), pos.getWidth(),
							8.0f);

					boolean resizableWidth = resizeMode == ResizeMode.Width || resizeMode == ResizeMode.WidthAndHeight;
					boolean resizableHeight = resizeMode == ResizeMode.Height
							|| resizeMode == ResizeMode.WidthAndHeight;

					if (resizableWidth && leftHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Left);
					else if (resizableWidth && rightHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Right);
					else if (resizableHeight && topHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Top);
					else if (resizableHeight && bottomHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Bottom);
					else
						setResizing(false, event, Direction.None);
				}

				if (moveable && !isResizing) {
					if (pos.intersects(mouseX, mouseY)) {
						GuiManager.setCursor(CursorStyle.Click);
						parentPage.moveToFront(this);
						isMoving = true;
						event.cancel();
						return;
					}
				}
			} else if (event.button == MouseButton.LEFT && event.action == MouseAction.UP) {
				if (isMoving || isResizing) {
					isMoving = false;
					isResizing = false;
					GuiManager.setCursor(CursorStyle.Default);
				}
			}
		}
	}

	public float lerp(float start, float end, float alpha) {
		return start + alpha * (end - start);
	}
}