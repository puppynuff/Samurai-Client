/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.navigation;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.gui.Rectangle;
import kokabiel.samurai.gui.UIElement;
import kokabiel.samurai.gui.colors.Color;
import kokabiel.samurai.managers.SettingManager;
import kokabiel.samurai.settings.types.BooleanSetting;
import kokabiel.samurai.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class HudWindow extends Window
{
    private static Color hoverColor = new Color(255, 0, 0);
    private static Color dragColor = new Color(255, 0, 0, 165);
    public BooleanSetting activated;

    public CloseableWindow optionsWindow;

    public HudWindow(String ID, float x, float y)
    {
        this(ID, x, y, 180.0f, 50f);
    }

    public HudWindow(String ID, float x, float y, float width, float height)
    {
        super(ID, x, y, width, height);
        activated = BooleanSetting.builder()
                .id(ID + "_activated")
                .defaultValue(false)
                .onUpdate(val -> Samurai.getInstance().guiManager.setHudActive(this, val))
                .build();

        SettingManager.registerSetting(activated);
    }

    private void onActivatedChanged(Boolean state)
    {
        Samurai.getInstance().guiManager.setHudActive(this, state.booleanValue());
    }

    @Override
    public boolean isVisible()
    {
        return activated.getValue().booleanValue();
    }

    // Override to do nothing.. We want it to be visible based off of whether it is
    // activated.
    @Override
    public void setVisible(boolean state)
    {
        if (!state)
        {
            isMoving = false;
        }
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks)
    {
        if (isVisible())
        {
            List<UIElement> children = getChildren();
            for (UIElement child : children)
            {
                child.draw(drawContext, partialTicks);
            }

            Rectangle pos = getActualSize();

            float x = pos.getX().floatValue();
            float y = pos.getY().floatValue();
            float width = pos.getWidth().floatValue();
            float height = pos.getHeight().floatValue();

            if (isMoving)
            {
                if (pos.isDrawable())
                {
                    Render2D.drawBox(drawContext.getMatrices().peek().getPositionMatrix(), x, y, width, height,
                            dragColor);
                }
            }
            if (Samurai.getInstance().guiManager.isClickGuiOpen())
            {
                Render2D.drawBoxOutline(drawContext.getMatrices().peek().getPositionMatrix(), x, y, width, height,
                        hoverColor);
            }
        }
    }
}
