package com.company.data;

import com.company.interfaces.Calculations;
import lombok.Getter;

@Getter
public class ObjectZ {

    private final float decimal;
    private final float centesimal;
    private final float probability;

    public ObjectZ(float decimal, float centesimal, float probability) {
        this.decimal = decimal;
        this.centesimal = centesimal;
        this.probability = probability;
    }

    public float totalNumber(){
        return Calculations.roundToDecimalPlaces(decimal + centesimal, 2);
    }
}
