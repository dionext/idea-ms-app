package com.dionext.ideaportal.db.entity;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CitePriorConverter implements AttributeConverter<CitePrior, String> {

    @Override
    public String convertToDatabaseColumn(CitePrior citePrior) {
        if (citePrior == CitePrior.HIT) return "0";
        else if (citePrior == CitePrior.NORMAL) return "1";
        else if (citePrior == CitePrior.BAD) return "2";
        else return null;
    }

    @Override
    public CitePrior convertToEntityAttribute(String citePrior) {
        if ("0".equals(citePrior)) return CitePrior.HIT;
        else if ("1".equals(citePrior)) return CitePrior.NORMAL;
        else if ("2".equals(citePrior)) return CitePrior.BAD;
        else return null;
    }

}
