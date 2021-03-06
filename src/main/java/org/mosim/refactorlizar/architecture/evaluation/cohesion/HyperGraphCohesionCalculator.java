package org.mosim.refactorlizar.architecture.evaluation.cohesion;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mosim.refactorlizar.architecture.evaluation.CalculationMode;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.Cohesion;
import org.mosim.refactorlizar.architecture.evaluation.complexity.HyperGraphComplexityCalculator;
import org.mosim.refactorlizar.architecture.evaluation.graphs.Node;
import org.mosim.refactorlizar.architecture.evaluation.graphs.SystemGraphUtils;

public class HyperGraphCohesionCalculator<T> {

    private CalculationMode mode;
    private SystemGraphUtils<T> systemGraphUtils;

    public HyperGraphCohesionCalculator(
            CalculationMode mode, SystemGraphUtils<T> systemGraphUtils) {
        this.mode = Objects.requireNonNull(mode);
        this.systemGraphUtils = systemGraphUtils;
    }

    public Cohesion calculate(Graph<Node<T>> graph) {
        HyperGraphComplexityCalculator<T> complexityCalculator =
                new HyperGraphComplexityCalculator<T>(mode, systemGraphUtils);
        MutableGraph<Node<T>> intraModuleGraph = transformToIntraModuleGraph(graph);
        double interModuleGraphComplexity =
                complexityCalculator.calculate(intraModuleGraph).getValue();
        MutableGraph<Node<T>> fullyConnectedGraph = transformToFullyConnectedGraph(graph);
        double fullyConnectedGraphComplexity =
                complexityCalculator.calculateForFullyConnected(fullyConnectedGraph).getValue();
        return new Cohesion(interModuleGraphComplexity / fullyConnectedGraphComplexity);
    }

    private MutableGraph<Node<T>> transformToFullyConnectedGraph(Graph<Node<T>> graph) {
        MutableGraph<Node<T>> fullyConnectedGraph = Graphs.copyOf(graph);
        for (Node<T> first : fullyConnectedGraph.nodes()) {
            for (Node<T> second : fullyConnectedGraph.nodes()) {
                if (first.hashCode() != second.hashCode()) {
                    fullyConnectedGraph.putEdge(EndpointPair.ordered(first, second));
                } else {
                    if (!first.equals(second)) {
                        fullyConnectedGraph.putEdge(EndpointPair.ordered(first, second));
                    }
                }
            }
        }
        return fullyConnectedGraph;
    }

    private MutableGraph<Node<T>> transformToIntraModuleGraph(Graph<Node<T>> graph) {
        MutableGraph<Node<T>> intraModuleGraph = Graphs.copyOf(graph);
        graph.edges().stream()
                .filter(this::hasEndpointsNotInSameTypes)
                .collect(Collectors.toSet())
                .forEach(v -> intraModuleGraph.removeEdge(v.nodeU(), v.nodeV()));
        return intraModuleGraph;
    }

    private boolean hasEndpointsNotInSameTypes(EndpointPair<Node<T>> edge) {
        return !isSameType(edge.nodeU(), edge.nodeV());
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
