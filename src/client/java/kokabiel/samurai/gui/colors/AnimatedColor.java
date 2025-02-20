/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package kokabiel.samurai.gui.colors;

import kokabiel.samurai.Samurai;
import kokabiel.samurai.event.events.TickEvent;
import kokabiel.samurai.event.listeners.TickListener;

public abstract class AnimatedColor extends Color implements TickListener {
    public AnimatedColor() {
        super(255, 0, 0);
        Samurai.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onTick(TickEvent.Pre event) { }
    
    @Override
    public abstract void onTick(TickEvent.Post event);
}
