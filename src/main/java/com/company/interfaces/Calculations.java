package com.company.interfaces;

import com.company.data.TableStorage;
import com.company.formulas.Curves;
import com.company.formulas.FormulaParams;

import java.util.function.BiFunction;
import java.util.function.Function;

/*
    Class that involves all calculations used in the program, starting from functions of distributions
    and ending with individual formulas for numerical methods
 */
public class Calculations {

    private static BiFunction<Function<Float, Float>, FormulaParams, Float> active;

    public static void setActive(BiFunction<Function<Float, Float>, FormulaParams, Float> active) {
        Calculations.active = active;
    }

    public static BiFunction<Function<Float, Float>, FormulaParams, Float> getActive(){
        return active;
    }

    public static float computeArea(Function<Float, Float> function,
                                    BiFunction<Function<Float, Float>, FormulaParams, Float> formula,
                                    float x0, float x1, int numOfDivisions){
        float result = 0;
        float step = (x1 - x0)/numOfDivisions;

        for(int i = 0; i < numOfDivisions; i++){
            result += active.apply(Curves.NORMAL_PDF, new FormulaParams(x0, i, step));
        }
        return result;
    }

    public static String calculateConfidenceInterval(int size, float mean, float variance, float cl, int dp){
        float area = Calculations.roundToDecimalPlaces(0.5f - Calculations.roundToDecimalPlaces((1 - cl)/2 , 4), 4);
        float alphaHalf = Calculations.roundToDecimalPlaces((1 - cl)/2, 4);
        System.out.println("Alpha/2 = " + alphaHalf);
        System.out.println("Area = " + area);
        TableStorage table = Actions.storage;
        int index = table.binarySearchForArea(table.getTableValues().size(), area);

        float valueZ = table.getTableValues().get(index).totalNumber();
        System.out.println(valueZ);
        float leftEdge = Calculations.roundToDecimalPlaces(mean - valueZ*(variance/(float)Math.sqrt(size)), dp);
        float rightEdge = Calculations.roundToDecimalPlaces(mean + valueZ*(variance/(float)Math.sqrt(size)), dp);
        StringBuilder sb = new StringBuilder();
        sb.append("(" + leftEdge + " ; " + rightEdge + "), with corresponding z (" + alphaHalf + ") = " + valueZ + " and z (-" + (-1)*alphaHalf + ") = " + (-1)*valueZ + "\n");
        return sb.toString();
    }

    public static float roundToDecimalPlaces(float value, int dp){
        float factor = (float)Math.pow(10, dp);
        return Math.round(value*factor)/factor;
    }
}
