/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.navigation.windows;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.gui.colors.Colors;
import kokabiel.samurai.gui.components.EnumComponent;
import kokabiel.samurai.gui.components.SeparatorComponent;
import kokabiel.samurai.gui.components.StackPanelComponent;
import kokabiel.samurai.gui.components.StringComponent;
import kokabiel.samurai.gui.navigation.Window;
import kokabiel.samurai.module.AntiCheat;

/**
 * Represents the AntiCheat Window that allows the user to select their
 * anticheat.
 */
public class AntiCheatWindow extends Window {
	public AntiCheatWindow() {
		super("AntiCheat", 50, 895);
		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.addChild(new StringComponent("AntiCheat Settings"));
		stackPanel.addChild(new SeparatorComponent());
		stackPanel.addChild(new EnumComponent<AntiCheat>(Samurai.getInstance().moduleManager.antiCheat));
		stackPanel.addChild(new StringComponent(
				"The selected AC will disable any features that are KNOWN detectable by that AC.", Colors.Gray, false));
		addChild(stackPanel);
		setMinWidth(300.0f);
	}
}
