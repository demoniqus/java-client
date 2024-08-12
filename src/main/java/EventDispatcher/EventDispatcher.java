package EventDispatcher;

import Interfaces.IEvent;
import Interfaces.IEventDispatcher;
import Interfaces.IEventListener;

import java.util.*;

public class EventDispatcher implements IEventDispatcher {
    private HashMap<String, Set<IEventListener>> listeners = new HashMap<String, Set<IEventListener>>();
    private HashMap<String, List<IEventListener>> orderedListeners = new HashMap<String, List<IEventListener>>();

    public IEventDispatcher subscribe(String eventName, IEventListener listener) {
        if (!listeners.containsKey(eventName)) {
            listeners.put(eventName, new HashSet<IEventListener>() {
            });
            orderedListeners.put(eventName, new ArrayList<IEventListener>() {});
        }
        if(!listeners.get(eventName).contains(listener)) {
            listeners.get(eventName).add(listener);
            orderedListeners.get(eventName).add(listener);
        }

        return this;
    }

    public void unsubscribe(String eventName, IEventListener listener) {
        if (listener != null)  {

            if (
                    listeners.containsKey(eventName)
            ) {
                listeners.get(eventName).remove(listener);
            }
        }
        else {
            listeners.remove(eventName);
        }
    }

    public <T> void fire(String eventName, T source, HashMap<String, Object> additionalParams) {
        if (listeners.containsKey(eventName)) {
            IEvent event = new IEvent() {
                private String _eventName;
                private T _source;
                private HashMap<String, Object> _additionalParams;
                {
                    this._eventName = eventName;
                    this._source = source;
                    this._additionalParams = additionalParams;
                }

                @Override
                public String getEventName() {
                    return this._eventName;
                }

                @Override
                public <T> T getEventSource() {
                    return (T)_source;
                }

                @Override
                public <TParam> TParam getParam(String paramName) {
                    if (this._additionalParams.containsKey(paramName)) {
                        return (TParam)this._additionalParams.get(paramName);
                    }
                    return null;
                }
            };

            for (IEventListener listener : orderedListeners.get(eventName)) {
                listener.handleEvent(event);
            }
        }
    }
//
//    record Event <T> (String eventName, T source ) implements IEvent { }
}
