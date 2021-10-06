package org.mosim.refactorlizar.architecture.evaluation;

import com.google.common.graph.Graph;
import java.util.Collection;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.Cohesion;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.Complexity;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.Coupling;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.HyperGraphSize;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.LinesOfCode;
import org.mosim.refactorlizar.architecture.evaluation.codemetrics.SizeOfSystem;
import org.mosim.refactorlizar.architecture.evaluation.cohesion.HyperGraphCohesionCalculator;
import org.mosim.refactorlizar.architecture.evaluation.complexity.HyperGraphComplexityCalculator;
import org.mosim.refactorlizar.architecture.evaluation.coupling.HyperGraphInterModuleCouplingGenerator;
import org.mosim.refactorlizar.architecture.evaluation.graphs.CtTypeSystemGraphUtils;
import org.mosim.refactorlizar.architecture.evaluation.graphs.HyperGraphGenerator;
import org.mosim.refactorlizar.architecture.evaluation.graphs.Node;
import org.mosim.refactorlizar.architecture.evaluation.graphs.SystemGraphUtils;
import org.mosim.refactorlizar.architecture.evaluation.projectfilter.DataTypesFilter;
import org.mosim.refactorlizar.architecture.evaluation.projectfilter.ObservedSystemFilter;
import org.mosim.refactorlizar.architecture.evaluation.size.HyperGraphSizeCalculator;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

public class Application {

    @Api
    public Result evaluate(CalculationMode mode, String... paths) {
        return evaluate(mode, "", "", paths);
    }

    @Api
    public Result evaluate(
            CalculationMode mode,
            String dataPatternsPath,
            String observedSystemPath,
            String... paths) {
        Collection<CtType<?>> types = parseTypes(paths);
        removeDataTypes(types, dataPatternsPath);
        LinesOfCode loc = calculateLoC(types);
        SizeOfSystem sos = calculateSizeOfSystem(types);
        Graph<Node<CtType<?>>> graph = new HyperGraphGenerator().createHyperGraph(types);
        graph = removeNotObservedSystem(graph, observedSystemPath);
        SystemGraphUtils<CtType<?>> systemGraphUtils = new CtTypeSystemGraphUtils();
        HyperGraphSize size = calculateHyperGraphSize(mode, systemGraphUtils, graph);
        Complexity graphComplexity =
                new HyperGraphComplexityCalculator<CtType<?>>(mode, systemGraphUtils)
                        .calculate(graph);
        Coupling graphCoupling =
                new HyperGraphInterModuleCouplingGenerator<CtType<?>>(mode, systemGraphUtils)
                        .calculate(graph);
        Cohesion cohesion =
                new HyperGraphCohesionCalculator<CtType<?>>(mode, systemGraphUtils)
                        .calculate(graph);
        return new Result(loc, sos, size, graphComplexity, graphCoupling, cohesion);
    }

    private HyperGraphSize calculateHyperGraphSize(
            CalculationMode mode,
            SystemGraphUtils<CtType<?>> systemGraphUtils,
            Graph<Node<CtType<?>>> graph) {
        return new HyperGraphSize(
                new HyperGraphSizeCalculator<CtType<?>>(mode)
                        .calculate(systemGraphUtils.convertToSystemGraph(graph)));
    }

    private Collection<CtType<?>> parseTypes(String... paths) {
        Launcher launcher = new Launcher();
        for (String path : paths) {
            launcher.addInputResource(path);
        }
        launcher.getEnvironment().setCommentEnabled(false);
        CtModel model = launcher.buildModel();
        return model.getAllTypes();
    }

    private Graph<Node<CtType<?>>> removeNotObservedSystem(
            Graph<Node<CtType<?>>> graph, String observedSystemPath) {
        if (observedSystemPath.isBlank()) {
            return graph;
        }
        return ObservedSystemFilter.removeNonObservedSystem(graph, observedSystemPath);
    }

    private void removeDataTypes(Collection<CtType<?>> types, String dataPatternsPath) {
        if (dataPatternsPath.isBlank()) {
            return;
        }
        DataTypesFilter.removeDataTypes(types, dataPatternsPath);
    }

    private LinesOfCode calculateLoC(Collection<CtType<?>> types) {
        return new LinesOfCode(sumLinesOfCode(types));
    }

    private int sumLinesOfCode(Collection<CtType<?>> types) {
        return types.stream()
                .map(type -> type.getPosition().getEndLine())
                .reduce(0, (a, b) -> a + b);
    }

    private SizeOfSystem calculateSizeOfSystem(Collection<CtType<?>> types) {
        return new SizeOfSystem(types.size());
    }
}
