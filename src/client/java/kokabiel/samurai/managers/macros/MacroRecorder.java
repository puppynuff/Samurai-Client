/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.managers.macros;

import java.util.LinkedList;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.KeyDownEvent;
import kokabiel.samurai.event.events.KeyUpEvent;
import kokabiel.samurai.event.events.MouseClickEvent;
import kokabiel.samurai.event.events.MouseMoveEvent;
import kokabiel.samurai.event.events.MouseScrollEvent;
import kokabiel.samurai.event.listeners.KeyDownListener;
import kokabiel.samurai.event.listeners.KeyUpListener;
import kokabiel.samurai.event.listeners.MouseClickListener;
import kokabiel.samurai.event.listeners.MouseMoveListener;
import kokabiel.samurai.event.listeners.MouseScrollListener;
import kokabiel.samurai.managers.macros.actions.KeyClickMacroEvent;
import kokabiel.samurai.managers.macros.actions.MacroEvent;
import kokabiel.samurai.managers.macros.actions.MouseClickMacroEvent;
import kokabiel.samurai.managers.macros.actions.MouseMoveMacroEvent;
import kokabiel.samurai.managers.macros.actions.MouseScrollMacroEvent;

/**
 * Class responsible for recording Macros
 */
public class MacroRecorder
		implements MouseClickListener, MouseMoveListener, MouseScrollListener, KeyDownListener, KeyUpListener {

	private LinkedList<MacroEvent> currentMacro = new LinkedList<MacroEvent>();
	private long startTime = 0;
	private boolean recording = false;

	/**
	 * Begins recording a Macro
	 */
	public void startRecording() {
		if (!recording) {
			currentMacro = new LinkedList<MacroEvent>();
			recording = true;
			startTime = System.nanoTime();

			Samurai.getInstance().eventManager.AddListener(MouseClickListener.class, this);
			Samurai.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
			Samurai.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
			Samurai.getInstance().eventManager.AddListener(KeyDownListener.class, this);
			Samurai.getInstance().eventManager.AddListener(KeyUpListener.class, this);
		}
	}

	/**
	 * Stops recording a Macro
	 */
	public void stopRecording() {
		if (recording) {
			recording = false;
			startTime = 0;

			Samurai.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
			Samurai.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
			Samurai.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
			Samurai.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
			Samurai.getInstance().eventManager.RemoveListener(KeyUpListener.class, this);

			addToMacroManager();
		}
	}

	/**
	 * Adds the Macro to the Macro manager.
	 */
	public void addToMacroManager() {
		if (!recording && currentMacro != null) {
			Macro macro = new Macro(currentMacro);
			Samurai.getInstance().macroManager.setCurrentlySelected(macro);
			currentMacro = null;
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (event.GetKey() != Samurai.getInstance().guiManager.clickGuiButton.getValue().getCode()
				&& event.GetKey() != 256
				&& !Samurai.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new KeyClickMacroEvent(timeStamp, event.GetKey(), event.GetScanCode(), event.GetAction(),
					event.GetModifiers()));
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (event.GetKey() != Samurai.getInstance().guiManager.clickGuiButton.getValue().getCode()
				&& event.GetKey() != 256
				&& !Samurai.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new KeyClickMacroEvent(timeStamp, event.GetKey(), event.GetScanCode(), event.GetAction(),
					event.GetModifiers()));
		}
	}


	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (!Samurai.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseScrollMacroEvent(timeStamp, event.GetHorizontal(), event.GetVertical()));
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (!Samurai.getInstance().guiManager.isClickGuiOpen()) {
			if (mouseMoveEvent.getX() == 0 && mouseMoveEvent.getY() == 0)
				return;

			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseMoveMacroEvent(timeStamp, mouseMoveEvent.getX(), mouseMoveEvent.getY()));
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent mouseClickEvent) {
		if (!Samurai.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseClickMacroEvent(timeStamp, mouseClickEvent.button, mouseClickEvent.action,
					mouseClickEvent.mods));
		}
	}
}
