package org.mosim.refactorlizar.architecture_evaluation.graphs;

import java.util.Random;

import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import spoon.Launcher;
import spoon.reflect.declaration.CtType;

public class CtTypeSystemGraphUtils implements SystemGraphUtils<CtType<?>> {

    @Override
    public MutableGraph<Node<CtType<?>>> convertToSystemGraph(Graph<Node<CtType<?>>> graph) {
        MutableGraph<Node<CtType<?>>> systemGraph = Graphs.copyOf(graph);
        // add empty node as system node
        CtType<?> type = Launcher.parseClass("class SystemNode" + new Random().nextInt() + " {}");
        var method = new Launcher().createFactory().createMethod();
        type.addMethod(method);
        systemGraph.addNode(new SpoonNode(method));
        return systemGraph;
    }
}
