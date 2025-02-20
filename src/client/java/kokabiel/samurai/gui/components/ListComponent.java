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
import kokabiel.samurai.event.events.MouseClickEvent;
import kokabiel.samurai.event.listeners.MouseClickListener;
import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.Margin;
import kokabiel.samurai.gui.Rectangle;
import kokabiel.samurai.gui.Size;
import kokabiel.samurai.settings.types.StringSetting;
import kokabiel.samurai.utils.render.Render2D;
import kokabiel.samurai.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class ListComponent extends Component implements MouseClickListener {
	private StringSetting listSetting;

	private List<String> itemsSource;
	private int selectedIndex;

	public ListComponent(List<String> itemsSource) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.itemsSource = itemsSource;
	}

	public ListComponent(List<String> itemsSource, StringSetting listSetting) {
		super();
		this.listSetting = listSetting;
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.itemsSource = itemsSource;
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public String getSelectedItem() {
		if (itemsSource.size() > selectedIndex)
			return itemsSource.get(selectedIndex);
		else
			return null;
	}

	public List<String> getItemsSource() {
		return itemsSource;
	}

	public void setItemsSource(List<String> itemsSource) {
		this.itemsSource = itemsSource;
		setSelectedIndex(this.selectedIndex);
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (this.isVisible())
			Samurai.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		else
			Samurai.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();

		if (listSetting != null) {
			float stringWidth = Samurai.getInstance().fontManager.GetRenderer().getWidth(listSetting.getValue());
			Render2D.drawString(drawContext, listSetting.getValue(), actualX + (actualWidth / 2.0f) - stringWidth,
					actualY + 8, 0xFFFFFF);
		} else if (itemsSource.size() > 0) {
			float stringWidth = Samurai.getInstance().fontManager.GetRenderer().getWidth(itemsSource.get(selectedIndex));
			Render2D.drawString(drawContext, itemsSource.get(selectedIndex),
					actualX + (actualWidth / 2.0f) - stringWidth, actualY + 8, 0xFFFFFF);
		}

		Render2D.drawString(drawContext, "<<", actualX + 8, actualY + 4, GuiManager.foregroundColor.getValue());
		Render2D.drawString(drawContext, ">>", actualX + 8 + (actualWidth - 34), actualY + 4,
				GuiManager.foregroundColor.getValue());
	}

	public void setSelectedIndex(int index) {
		selectedIndex = index;

		if (listSetting != null) {
			listSetting.setValue(itemsSource.get(selectedIndex));
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		Rectangle actualSize = this.getActualSize();
		if (actualSize != null && actualSize.isDrawable()) {
			if (event.button == MouseButton.LEFT) {
				if (this.getActualSize().getY() < event.mouseY
						&& event.mouseY < this.getActualSize().getY() + this.getActualSize().getHeight()) {

					float mouseX = (float) event.mouseX;
					float actualX = this.getActualSize().getX();
					float actualWidth = this.getActualSize().getWidth();

					if (mouseX > actualX && mouseX < (actualX + 32)) {
						setSelectedIndex(Math.max(selectedIndex - 1, 0));
					} else if (mouseX > (actualX + actualWidth - 32) && mouseX < (actualX + actualWidth))
						setSelectedIndex(Math.min(selectedIndex + 1, itemsSource.size() - 1));

					event.cancel();
				}
			}
		}
	}
}
