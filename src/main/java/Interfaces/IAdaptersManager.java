package Interfaces;

public interface IAdaptersManager extends ILocked {
    <T> T getAdapter(Class<?> type, Class<?> className);
}
