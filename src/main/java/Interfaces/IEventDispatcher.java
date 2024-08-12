package Interfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface IEventDispatcher {
    IEventDispatcher subscribe(String eventName, IEventListener listener);
    void unsubscribe(String eventName, IEventListener listener);
    <T>void fire(String eventName, T source, HashMap<String, Object> additionalParams);
}
