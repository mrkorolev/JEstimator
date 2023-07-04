package com.company.formulas;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Formulas {

    /*
        Formulas for computation of the integral, in case of further
     */
    public static final BiFunction<Function<Float, Float>, FormulaParams, Float> RECTANGLE_MID = (f, fp) -> {
        float x0 = fp.getStart();
        int i = fp.getCycleIndex();
        float step = fp.getStep();

        return (f.apply(x0 + (i + 0.5f)*step))*step;
    };

    public static final BiFunction<Function<Float, Float>, FormulaParams, Float> TRAPEZIUM = (f, fp) -> {
        float x0 = fp.getStart();
        int i = fp.getCycleIndex();
        float step = fp.getStep();

        return (f.apply(x0 + i*step) + f.apply(x0 + (i + 1)*step))*step/2;
    };

    public static final BiFunction<Function<Float, Float>, FormulaParams, Float> SIMPSON = (f, fp) -> {
        float x0 = fp.getStart();
        int i = fp.getCycleIndex();
        float step = fp.getStep();

        return (f.apply(x0 + i*step) + 4*f.apply(x0 + (i + 0.5f)*step) + f.apply(x0 + (i + 1)*step))*step/6;
    };
}
