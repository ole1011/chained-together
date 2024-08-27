package de.ole101.chained.common;

import com.google.inject.AbstractModule;
import de.ole101.chained.ChainedTogether;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuiceModule extends AbstractModule {

    private final ChainedTogether chainedTogether;

    @Override
    protected void configure() {
        bind(ChainedTogether.class).toInstance(this.chainedTogether);
    }
}
