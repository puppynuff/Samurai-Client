/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.navigation.windows;

import java.util.ArrayList;

import kokabiel.samurai.gui.Margin;
import kokabiel.samurai.gui.components.HudComponent;
import kokabiel.samurai.gui.components.SeparatorComponent;
import kokabiel.samurai.gui.components.StackPanelComponent;
import kokabiel.samurai.gui.components.StringComponent;
import kokabiel.samurai.gui.navigation.HudWindow;
import kokabiel.samurai.gui.navigation.Window;
import net.minecraft.client.gui.DrawContext;

public class ToggleHudsTab extends Window {
	public ToggleHudsTab(ArrayList<HudWindow> huds) {
		super("Toggle HUDs", 0, 0);

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setMargin(new Margin(null, 30f, null, null));

		stackPanel.addChild(new StringComponent("Toggle HUDs"));
		stackPanel.addChild(new SeparatorComponent());

		for (HudWindow hud : huds) {
			HudComponent hudComponent = new HudComponent(hud.getID(), hud);
			stackPanel.addChild(hudComponent);
		}

		addChild(stackPanel);
		this.setMinWidth(300.0f);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
	}
}
