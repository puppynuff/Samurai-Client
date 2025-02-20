/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.settings.types;

import java.util.function.Consumer;

import kokabiel.samurai.gui.GuiManager;
import kokabiel.samurai.gui.colors.Color;
import kokabiel.samurai.settings.Setting;

public class ColorSetting extends Setting<Color> {
	public enum ColorMode {
	    Solid,
	    Rainbow,
	    Random,
	}

    private ColorMode mode = ColorMode.Solid;

    protected ColorSetting(String ID, String displayName, String description, Color default_value, Consumer<Color> onUpdate) {
        super(ID, displayName, description, default_value);
        type = TYPE.COLOR;
    }

    @Override
    protected boolean isValueValid(Color value) {
        return (value.getRed() <= 255 && value.getGreen() <= 255 && value.getBlue() <= 255);
    }

    public ColorMode getMode() {
    	return mode;
    }
    
    public void setMode(ColorMode color) {
        mode = color;
        switch (mode) {
            case Solid:
                this.setValue(defaultValue);
                break;
            case Rainbow:
                this.setValue(GuiManager.rainbowColor);
                break;
            case Random:
                this.setValue(GuiManager.randomColor);
                break;
        }
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, ColorSetting, Color> {
		protected BUILDER() {
			super();
		}
		
		@Override
		public ColorSetting build() {
			return new ColorSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
