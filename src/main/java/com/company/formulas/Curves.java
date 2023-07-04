package com.company.formulas;

import java.util.function.Function;

/*
    Class representing curves to be used in calculations (in case for current software's evolution)
 */
public class Curves {

    public static final Function<Float, Float> NORMAL_PDF = x -> (float)(1/(Math.sqrt(2*Math.PI))*Math.exp(-x*x/2));
}
