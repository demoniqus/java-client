package Interfaces;

public interface IEventListener {
    <T> T handleEvent(IEvent event);
}
