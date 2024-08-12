package Interfaces;

public interface IEvent {
    public String getEventName();
    public <TEventSource> TEventSource getEventSource();
    public <TParam> TParam getParam(String paramName);
}
