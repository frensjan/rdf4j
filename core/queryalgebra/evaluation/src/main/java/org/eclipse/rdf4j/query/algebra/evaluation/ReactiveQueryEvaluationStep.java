package org.eclipse.rdf4j.query.algebra.evaluation;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.common.reactor.CloseableFluxIteration;
import org.eclipse.rdf4j.query.BindingSet;
import reactor.core.publisher.Flux;

public interface ReactiveQueryEvaluationStep extends QueryEvaluationStep {

    Flux<BindingSet> transform(Flux<BindingSet> input);

    default Flux<BindingSet> transform(BindingSet input) {
        return transform(Flux.just(input));
    }

    @Override
    default CloseableIteration<BindingSet> evaluate(BindingSet bindings) {
        Flux<BindingSet> input = Flux.just(bindings);
        Flux<BindingSet> output = transform(input);
        return new CloseableFluxIteration<>(output);
    }

    static ReactiveQueryEvaluationStep from(QueryEvaluationStep step) {
        if (step instanceof ReactiveQueryEvaluationStep) {
            return (ReactiveQueryEvaluationStep) step;
        } else {
            return input -> input.flatMapSequential(
                    bindings -> step.evaluate(bindings).flux(),
                    1,1
            );
        }
    }
}
