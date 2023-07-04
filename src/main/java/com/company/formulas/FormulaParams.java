package com.company.formulas;

import lombok.Data;

@Data
public class FormulaParams {

    private float start;
    private int cycleIndex;
    private float step;

    public FormulaParams(float start, int cycleIndex, float step) {
        this.start = start;
        this.cycleIndex = cycleIndex;
        this.step = step;
    }
}
