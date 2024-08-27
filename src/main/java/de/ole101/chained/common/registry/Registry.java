package de.ole101.chained.common.registry;

import com.google.inject.Injector;
import de.ole101.chained.ChainedTogether;
import de.ole101.chained.common.annotations.RegisteredListener;
import lombok.extern.log4j.Log4j2;
import org.atteo.classindex.ClassIndex;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
public class Registry {

    private final ChainedTogether chainedTogether;
    private final ClassLoader classLoader;
    private final Injector injector;

    public Registry(ChainedTogether chainedTogether, ClassLoader classLoader, Injector injector) {
        this.chainedTogether = chainedTogether;
        this.classLoader = classLoader;
        this.injector = injector;
    }

    public void registerAllListeners() {
        AtomicInteger successCases = new AtomicInteger();
        Set<Class<?>> listenerClasses = StreamSupport.stream(ClassIndex.getAnnotated(RegisteredListener.class, this.classLoader).spliterator(), false)
                .filter(Listener.class::isAssignableFrom)
                .collect(Collectors.toSet());

        PluginManager pluginManager = Bukkit.getPluginManager();
        listenerClasses.forEach(listenerClass -> {
            pluginManager.registerEvents((Listener) this.injector.getInstance(listenerClass), this.chainedTogether);
            successCases.getAndIncrement();
        });

        log.info("Registered listeners: {}/{}", successCases.get(), listenerClasses.size());
    }
}
