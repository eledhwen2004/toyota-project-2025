package com.main.ClassLoader;

import com.main.Subscriber.SubscriberInterface;

import java.lang.reflect.InvocationTargetException;

public class SubscriberClassLoader {
    private static final String subscriberClassPath = "com.main.Subscriber.Subscribers.";

    public static SubscriberInterface loadSubscriber(String subscriberName,
                                                     Class<?>[] paramTypes,
                                                     Object[] params) {
        try {
            Class<?> loadedClass = Class.forName(subscriberClassPath + subscriberName);
            return (SubscriberInterface) loadedClass.getConstructor(paramTypes).newInstance(params);
        } catch (ClassNotFoundException | InvocationTargetException |
                 InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Subscriber class loading failed: " + e.getMessage(), e);
        }
    }
}
