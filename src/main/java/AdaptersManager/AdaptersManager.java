package AdaptersManager;

import Interfaces.IAdaptersManager;
import jdk.jfr.Description;

import java.util.HashMap;
import java.util.Map;
@Description("Менеджер различных адаптеров")
public class AdaptersManager implements IAdaptersManager {
    private Map<Class<?>, Map<Class<?>, Object>> adapters = new HashMap<>();

    private Boolean locked = false;

    /**
     *
     * @param type - тип адаптера (например, адаптер моделей данных или адаптер самих данных)
     * @param className - целевой класс, для которого предназначен адаптер (например, конкретный визуализатор)
     * @param adapter - сам адаптер
     * @return this
     */
    public AdaptersManager addAdapter(Class<?> type, Class<?> className, Object adapter) {
        if (locked) {
            throwException();
        }
        if (!adapters.containsKey(type)) {
            adapters.put(type, new HashMap<>());
        }
        adapters.get(type).put(className, adapter);

        return this;
    };

    @Override
    public <T> T getAdapter(Class<?> type, Class<?> className) {
        return (T)adapters.get(type).get(className);
    }

    @Override
    public void lock() {
        locked = true;
    }
}
