package org.mosim.refactorlizar.architecture.evaluation.codemetrics;

public class Coupling extends CodeMetric {

    private static final String METRIC_NAME = "Coupling";

    public Coupling(double value) {
        super(value);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public String getName() {
        return METRIC_NAME;
    }
}
