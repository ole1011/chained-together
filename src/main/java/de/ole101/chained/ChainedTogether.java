package de.ole101.chained;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.ole101.chained.common.GuiceModule;
import de.ole101.chained.common.registry.Registry;
import lombok.extern.log4j.Log4j2;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

@Log4j2
public class ChainedTogether extends JavaPlugin {

    private final Injector injector;
    private final Registry registry;

    public ChainedTogether() {
        this.injector = Guice.createInjector(new GuiceModule(this));
        this.registry = new Registry(this, getClassLoader(), this.injector);
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        Bukkit.getServicesManager().register(Injector.class, this.injector, this, ServicePriority.Normal);

        this.registry.registerAllListeners();

        log.info("ChainedTogether enabled in {}ms", System.currentTimeMillis() - startTime);
    }
}
