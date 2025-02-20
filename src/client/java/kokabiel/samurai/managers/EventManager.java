package kokabiel.samurai.managers;

import com.mojang.logging.LogUtils;
import kokabiel.samurai.event.events.AbstractEvent;
import kokabiel.samurai.event.listeners.AbstractListener;
import kokabiel.samurai.event.listeners.TickListener;
import kokabiel.samurai.gui.colors.AnimatedColor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

    private final ConcurrentHashMap<Class<AbstractListener>, ArrayList<AbstractListener>> listeners;

    public EventManager() {
        listeners = new ConcurrentHashMap<Class<AbstractListener>,ArrayList<AbstractListener>>();
    }

    @SuppressWarnings("all")
    public <T extends AbstractListener> void AddListener(Class<T> object, AbstractListener listener) {
        try {
            ArrayList<AbstractListener> listOfListeners = listeners.get(object);
            if(listOfListeners == null) {
                listOfListeners = new ArrayList<>(Arrays.asList(listener));
                listeners.put((Class<AbstractListener>) object, listOfListeners);
            } else {
                listOfListeners.add(listener);
            }
        } catch (Exception e) {
            LogUtils.getLogger().error("Issue adding listener: " + object.getTypeName() + "...");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public <T extends AbstractListener> void RemoveListener(Class<T> object, AbstractListener listener) {
        try {
            ArrayList<AbstractListener> listOfListeners = listeners.get(object);
            if(listOfListeners != null) {
                listOfListeners.add(listener);
            }
        } catch (Exception e) {
            LogUtils.getLogger().error("Issue removing listener: " + object.getTypeName() + "...");
            e.printStackTrace();
        }
    }

    public void Fire(AbstractEvent event) {
        ArrayList<? extends AbstractListener> listOfListeners = listeners.get(event.GetListenerClassType());

        if (listOfListeners == null) {
            return;
        }

        event.Fire(listOfListeners);
    }

    public <T extends AbstractListener> boolean isListenerRegistered(Class<T> object, AbstractListener listener) {
        ArrayList<AbstractListener> listOfListeners = listeners.get(object);
        return listOfListeners != null && listOfListeners.contains(listener);
    }

    public <T extends AbstractListener> List<AbstractListener> getListeners(Class<T> object) {
        return listeners.getOrDefault(object, new ArrayList<>());
    }

    public <T extends AbstractListener> void clearListeners(Class<T> object) {
        listeners.remove(object);
    }

    public void clearAllListeners() {
        listeners.clear();
    }

    public <T extends AbstractListener> int getListenerCount(Class<T> object) {
        ArrayList<AbstractListener> listOfListeners = listeners.get(object);
        return listOfListeners == null ? 0 : listOfListeners.size();
    }

    public <T extends AbstractListener> boolean hasListeners(Class<T> object) {
        ArrayList<AbstractListener> listOfListeners = listeners.get(object);
        return listOfListeners != null && !listOfListeners.isEmpty();
    }

    public ConcurrentHashMap.KeySetView<Class<AbstractListener>, ArrayList<AbstractListener>> getAllEventTypes() {
        return listeners.keySet();
    }

    public String getListenerInfo(AbstractListener listener) {
        for(Map.Entry<Class<AbstractListener>, ArrayList<AbstractListener>> entry: listeners.entrySet()) {
            Class<? extends AbstractListener> eventType = entry.getKey();
            ArrayList<AbstractListener> listOfListeners = entry.getValue();
            if(listOfListeners.contains(listener)) {
                return "Listener: " + listener.getClass().getTypeName() + ", Event Type: " + eventType.getTypeName();
            }
        }

        return "Listener not registered.";
    }

    public List<Class<? extends AbstractListener>> getEventTypesForListener(AbstractListener listener) {
        List<Class<? extends AbstractListener>> eventTypes = new ArrayList<>();
        for (Map.Entry<Class<AbstractListener>, ArrayList<AbstractListener>> entry : listeners.entrySet()) {
            Class<? extends AbstractListener> eventType = entry.getKey();
            ArrayList<AbstractListener> listOfListeners = entry.getValue();
            if (listOfListeners.contains(listener)) {
                eventTypes.add(eventType);
            }
        }
        return eventTypes;
    }
}
