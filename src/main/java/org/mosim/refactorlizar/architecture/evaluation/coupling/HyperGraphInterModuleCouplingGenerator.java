package org.mosim.refactorlizar.architecture.evaluation.coupling;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mosim.refactorlizar.architecture.evaluation.CalculationMode;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.Coupling;
import org.mosim.refactorlizar.architecture.evaluation.complexity.HyperGraphComplexityCalculator;
import org.mosim.refactorlizar.architecture.evaluation.graphs.Node;
import org.mosim.refactorlizar.architecture.evaluation.graphs.SystemGraphUtils;

public class HyperGraphInterModuleCouplingGenerator<T> {

    private CalculationMode mode;
    private SystemGraphUtils<T> systemGraphUtils;

    // die größe eines Graphen wird immer auf dem Systemgraphen berechnet
    public HyperGraphInterModuleCouplingGenerator(
            CalculationMode mode, SystemGraphUtils<T> systemGraphUtils) {
        this.mode = Objects.requireNonNull(mode);
        this.systemGraphUtils = systemGraphUtils;
    }

    public Coupling calculate(Graph<Node<T>> graph) {
        MutableGraph<Node<T>> interModuleGraph = Graphs.copyOf(graph);
        graph.edges().stream()
                .filter(this::hasEndpointsInSameTypes)
                .collect(Collectors.toSet())
                .forEach(v -> interModuleGraph.removeEdge(v.nodeU(), v.nodeV()));
        return new Coupling(
                new HyperGraphComplexityCalculator<T>(mode, systemGraphUtils)
                        .calculate(interModuleGraph)
                        .getValue());
    }

    private boolean hasEndpointsInSameTypes(EndpointPair<Node<T>> edge) {
        return isSameType(edge.nodeU(), edge.nodeV());
    }

    private boolean isSameType(Node<T> u, Node<T> v) {
        T typeV = getType(v);
        T typeU = getType(u);
        if (typeV.hashCode() != typeU.hashCode()) {
            return false;
        }
        return getType(u).equals(getType(v));
    }

    private T getType(Node<T> executable) {
        return executable.getModule();
    }
}
