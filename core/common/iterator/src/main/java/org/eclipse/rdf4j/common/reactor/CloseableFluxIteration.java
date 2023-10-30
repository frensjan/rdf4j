package org.eclipse.rdf4j.common.reactor;

import org.eclipse.rdf4j.common.iteration.AbstractCloseableIteration;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Wraps a {@link Flux} reactive stream into a {@link org.eclipse.rdf4j.common.iteration.CloseableIteration}.
 *
 * @param <T> The type of elements iterated over.
 * @param <X> The exception type that can be thrown from {@link #hasNext()}, {@link #next()} and other methods.
 */
@SuppressWarnings("deprecation")
public class CloseableFluxIteration<T> extends AbstractCloseableIteration<T> implements FluxIteration<T> {

    private final Flux<T> flux;
    private Iterator<T> iter;

    /**
     * Creates a new IterationWrapper that operates on the supplied Iteration.
     *
     * @param flux The wrapped Iteration for this <var>IterationWrapper</var>, must not be <var>null</var>.
     */
    public CloseableFluxIteration(Flux<T> flux) {
        this.flux = flux;
    }

    @Override
    public Flux<T> flux() {
        return flux;
    }

    @Override
    public boolean hasNext() {
        if (iter == null) {
            iter = flux.toIterable().iterator();
        }

        return iter.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return iter.next();
    }

    @Override
    protected void handleClose() {
        // TODO interrupt any thread blocking on the iterator from the flux
    }
}
