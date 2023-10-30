/*******************************************************************************
 * Copyright (c) 2021 Eclipse RDF4J contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 *******************************************************************************/
package org.eclipse.rdf4j.query.algebra.evaluation.impl.evaluationsteps;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.algebra.Join;
import org.eclipse.rdf4j.query.algebra.Service;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.evaluation.EvaluationStrategy;
import org.eclipse.rdf4j.query.algebra.evaluation.ReactiveQueryEvaluationStep;
import org.eclipse.rdf4j.query.algebra.evaluation.federation.ServiceJoinIterator;
import org.eclipse.rdf4j.query.algebra.evaluation.impl.QueryEvaluationContext;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.HashJoinIteration;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.JoinIterator;
import org.eclipse.rdf4j.query.algebra.helpers.TupleExprs;
import reactor.core.publisher.Flux;

public class JoinQueryEvaluationStep implements ReactiveQueryEvaluationStep {

    private final java.util.function.Function<Flux<BindingSet>, Flux<BindingSet>> eval;

    private final ReactiveQueryEvaluationStep leftPrepared;
    private final ReactiveQueryEvaluationStep rightPrepared;

    public JoinQueryEvaluationStep(EvaluationStrategy strategy, Join join, QueryEvaluationContext context) {
        // efficient computation of a SERVICE join using vectored evaluation
        // TODO maybe we can create a ServiceJoin node already in the parser?
        leftPrepared = ReactiveQueryEvaluationStep.from(strategy.precompile(join.getLeftArg(), context));
        rightPrepared = ReactiveQueryEvaluationStep.from(strategy.precompile(join.getRightArg(), context));
        if (join.getRightArg() instanceof Service) {
            eval = input -> input.flatMapSequential(bindings -> Iterations.flux(
                    new ServiceJoinIterator(leftPrepared.evaluate(bindings),
                                            (Service) join.getRightArg(), bindings,
                                            strategy)
            ));
            join.setAlgorithm(ServiceJoinIterator.class.getSimpleName());
        } else if (isOutOfScopeForLeftArgBindings(join.getRightArg())) {
            String[] joinAttributes = HashJoinIteration.hashJoinAttributeNames(join);
            eval = input -> input.flatMapSequential(bindings -> Iterations.flux(
                    new HashJoinIteration(leftPrepared, rightPrepared, bindings, false,
                                          joinAttributes, context)
            ));
            join.setAlgorithm(HashJoinIteration.class.getSimpleName());
        } else {
            eval = input -> leftPrepared.transform(input)
                    .flatMapSequential(rightPrepared::transform,
                                       1,1);
            join.setAlgorithm(JoinIterator.class.getSimpleName());
        }
    }

    @Override
    public Flux<BindingSet> transform(Flux<BindingSet> input) {
        return eval.apply(input);
    }

    private static boolean isOutOfScopeForLeftArgBindings(TupleExpr expr) {
        return TupleExprs.isVariableScopeChange(expr) || TupleExprs.containsSubquery(expr);
    }

}
