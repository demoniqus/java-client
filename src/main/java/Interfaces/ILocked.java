package Interfaces;

import jdk.jfr.Description;

@Description("Интерфейс блокируемых от изменений объектов")
public interface ILocked {
    void lock();
    default void throwException()
    {
        throw new RuntimeException(this.getClass().getName() + " cannot be modified because is locked.");
    }
}
