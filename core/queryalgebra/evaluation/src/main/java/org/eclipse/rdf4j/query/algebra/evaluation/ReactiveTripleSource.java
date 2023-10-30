package org.eclipse.rdf4j.query.algebra.evaluation;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import reactor.core.publisher.Flux;

public interface ReactiveTripleSource {

    Flux<? extends Statement> getStatements(Resource subj, IRI pred, Value obj, Resource... contexts)
            throws QueryEvaluationException;

    static ReactiveTripleSource from(TripleSource source) {
        return (subj, pred, obj, contexts) -> source.getStatements(subj, pred, obj, contexts).flux();
    }
}
