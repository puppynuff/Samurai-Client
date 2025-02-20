/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.components;

import java.util.List;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.MouseScrollEvent;
import kokabiel.samurai.event.listeners.MouseScrollListener;
import kokabiel.samurai.gui.UIElement;
import kokabiel.samurai.gui.components.StackPanelComponent.StackType;

public class ScrollComponent extends Component implements MouseScrollListener {
	protected StackType stackType = StackType.Vertical;

	protected int scroll = 0;
	protected int visibleElements = 5;

	/**
	 * Scroll component allows elements to be placed inside of a scroll-like
	 * component. All children of ScrollComponent MUST have a width/height set
	 * depending on which direction the stack type is.
	 */
	public ScrollComponent() {
		super();
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (this.isVisible())
			Samurai.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
		else
			Samurai.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
	}

	/*
	 * public void RecalculateHeight() { float height = 0; int childCount =
	 * children.size();
	 * 
	 * for (int i = scroll; i < scroll + visibleElements; i++) { if (i >=
	 * childCount) break;
	 * 
	 * Component iChild = children.get(i); // If the child is visible, increase the
	 * height of the StackPanel. if (iChild.isVisible()) { height +=
	 * iChild.getSize().getHeight(); } // Move the Top of the child below to the top
	 * + height of the previous element. if (i + 1 != children.size()) { Component
	 * childBelow = children.get(i + 1); Margin margin = childBelow.getMargin();
	 * childBelow.setMargin(new Margin(margin.getLeft(), height, margin.getRight(),
	 * margin.getBottom())); } } setHeight(height); }
	 */

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (Samurai.getInstance().guiManager.isClickGuiOpen() && this.hovered) {
			List<UIElement> children = getChildren();
			int childCount = children.size();
			if (event.GetVertical() > 0 && scroll > 0) {
				scroll--;
			} else if (event.GetVertical() < 0 && (scroll + visibleElements) < childCount) {
				scroll++;
			}
			event.cancel();
		}
	}
}