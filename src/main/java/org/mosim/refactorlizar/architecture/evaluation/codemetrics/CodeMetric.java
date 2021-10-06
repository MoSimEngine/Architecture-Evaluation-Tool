package org.mosim.refactorlizar.architecture.evaluation.codemetrics;

public abstract class CodeMetric {

    protected final double value;

    protected CodeMetric(double value) {
        this.value = value;
    }

    /** Returns the value */
    public double getValue() {
        return value;
    }

    abstract String getName();
}
