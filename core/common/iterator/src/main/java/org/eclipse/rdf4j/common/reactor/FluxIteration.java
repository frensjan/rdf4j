package org.eclipse.rdf4j.common.reactor;

import reactor.core.publisher.Flux;

public interface FluxIteration<T> {

     Flux<T> flux();

}
