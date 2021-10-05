package org.mosim.refactorlizar.architecture_evaluation;

import org.mosim.refactorlizar.architecture_evaluation.codemetrics.Cohesion;
import org.mosim.refactorlizar.architecture_evaluation.codemetrics.Complexity;
import org.mosim.refactorlizar.architecture_evaluation.codemetrics.Coupling;
import org.mosim.refactorlizar.architecture_evaluation.codemetrics.HyperGraphSize;
import org.mosim.refactorlizar.architecture_evaluation.codemetrics.LinesOfCode;
import org.mosim.refactorlizar.architecture_evaluation.codemetrics.SizeOfSystem;

public class Result {

    private SizeOfSystem sizeOfSystem;
    private LinesOfCode loc;
    private Complexity complexity;
    private Coupling coupling;
    private Cohesion cohesion;
    private HyperGraphSize size;

    public Result(
            LinesOfCode loc,
            SizeOfSystem sos,
            HyperGraphSize size,
            Complexity graphComplexity,
            Coupling coupling,
            Cohesion cohesion) {
        this.size = size;
        this.complexity = graphComplexity;
        this.coupling = coupling;
        this.cohesion = cohesion;
        this.loc = loc;
        this.sizeOfSystem = sos;
    }

    /** Returns the cohesion */
    public Cohesion getCohesion() {
        return cohesion;
    }
    /** Returns the complexity */
    public Complexity getComplexity() {
        return complexity;
    }
    /** Returns the coupling */
    public Coupling getCoupling() {
        return coupling;
    }
    /** Returns the size */
    public HyperGraphSize getSize() {
        return size;
    }
    /** Returns the loc */
    public LinesOfCode getLoc() {
        return loc;
    }
    /** Returns the sizeOfSystem */
    public SizeOfSystem getSizeOfSystem() {
        return sizeOfSystem;
    }
}
