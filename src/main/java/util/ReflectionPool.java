package util;


import java.lang.reflect.Constructor;

/**
 * Pool that creates new instances of a type using reflection. The type must
 * have a zero argument constructor. {@link Constructor#setAccessible(boolean)}
 * will be used if the class and/or constructor is not visible.
 * 
 * @author Nathan Sweet
 */
public class ReflectionPool<T> extends Pool<T> {
    private final Constructor constructor;

    public ReflectionPool(Class<T> type) {
        this(type, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int max) {
        super(max);
        constructor = findConstructor(type);
        if (constructor == null) {
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): "
                    + type.getName());
        }
    }

    private Constructor findConstructor(Class<T> type) {
        try {
            return type.getConstructor((Class[]) null);
        }
        catch (Exception ex1) {
            try {
                Constructor constructor = type.getDeclaredConstructor((Class[]) null);
                constructor.setAccessible(true);
                return constructor;
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T newObject() {
        try {
            return (T) constructor.newInstance((Object[]) null);
        }
        catch (Exception ex) {
            throw new RuntimeException("Unable to create new instance: "
                                       + constructor.getDeclaringClass().getName(), ex);
        }
    }
}
