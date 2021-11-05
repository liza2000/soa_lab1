package ru.itmo.soa.entity.data.validator;


import ru.itmo.soa.entity.data.CoordinatesData;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesValidator  implements Validator<CoordinatesData> {
    public List<String> validate(CoordinatesData coordinates) {
        List<String> errorList = new ArrayList<>();
        if (coordinates == null) {
            errorList.add("Coordinates aren't specified");
            return errorList;
        }
        if (coordinates.getX() == null)
            errorList.add("Coordinate x is't specified");
        if (coordinates.getY() == null)
            errorList.add("Coordinate y is't specified");
        if (coordinates.getX() != null && coordinates.getX() <= -706.0)
            errorList.add("Coordinate x should be bigger than -706");

        return errorList;
    }
}
