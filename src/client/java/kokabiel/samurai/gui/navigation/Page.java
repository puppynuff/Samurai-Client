/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.navigation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.MouseClickEvent;
import kokabiel.samurai.event.events.MouseMoveEvent;
import kokabiel.samurai.event.events.MouseScrollEvent;
import kokabiel.samurai.event.listeners.MouseClickListener;
import kokabiel.samurai.event.listeners.MouseMoveListener;
import kokabiel.samurai.event.listeners.MouseScrollListener;
import net.minecraft.client.gui.DrawContext;

// TODO: Turn Page into a UI element.
public class Page implements MouseMoveListener, MouseClickListener, MouseScrollListener {
	protected String title;
	protected List<Window> tabs = new ArrayList<Window>();

	private boolean isVisible;

	public Page(String title) {
		this.title = title;
	}

	public void initialize() {
		for (Window tab : tabs) {
			tab.initialize();
		}
	}

	public String getTitle() {
		return this.title;
	}

	public void addWindow(Window hud) {
		hud.parentPage = this;
		hud.setVisible(this.isVisible);
		tabs.add(hud);
		if (hud.isInitialized())
			hud.invalidateMeasure();
		else
			hud.initialize();
	}

	public void removeWindow(Window hud) {
		hud.parentPage = null;
		tabs.remove(hud);
	}

	public void setVisible(boolean state) {
		this.isVisible = state;

		if (isVisible) {
			Samurai.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
			Samurai.getInstance().eventManager.AddListener(MouseClickListener.class, this);
			Samurai.getInstance().eventManager.AddListener(MouseScrollListener.class, this);

		} else {
			Samurai.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
			Samurai.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
			Samurai.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
		}

		for (Window hud : tabs) {
			hud.setVisible(state);
		}
	}

	public void update() {
		if (this.isVisible) {
			Iterator<Window> tabIterator = tabs.iterator();
			while (tabIterator.hasNext()) {
				tabIterator.next().update();
			}
		}
	}

	public void render(DrawContext drawContext, float partialTicks) {
		if (this.isVisible) {
			Iterator<Window> tabIterator = tabs.iterator();
			while (tabIterator.hasNext()) {
				tabIterator.next().draw(drawContext, partialTicks);
			}
		}
	}

	public void moveToFront(Window window) {
		if (tabs.size() > 1) {
			Window temp = tabs.get(tabs.size() - 1);
			int indexOfWindow = tabs.indexOf(window);
			tabs.set(indexOfWindow, temp);
			tabs.set(tabs.size() - 1, window);
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (Samurai.getInstance().guiManager.isClickGuiOpen()) {
			tabs.reversed().stream().toList().forEach(s -> s.onMouseMove(mouseMoveEvent));
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent mouseClickEvent) {
		if (Samurai.getInstance().guiManager.isClickGuiOpen()) {
			tabs.reversed().stream().toList().forEach(s -> s.onMouseClick(mouseClickEvent));
		}
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (Samurai.getInstance().guiManager.isClickGuiOpen()) {
			tabs.reversed().stream().toList().forEach(s -> s.onMouseScroll(event));
		}
	}
}
