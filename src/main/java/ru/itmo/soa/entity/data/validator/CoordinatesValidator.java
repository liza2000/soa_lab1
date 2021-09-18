package ru.itmo.soa.entity.data.validator;


import ru.itmo.soa.entity.data.CoordinatesData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CoordinatesValidator  implements Validator<CoordinatesData> {
    public List<String> validate(CoordinatesData coordinates) throws IllegalAccessException {
        List<String> errorList = new ArrayList<>();
        if (coordinates == null) {
            return errorList;
        }
        for (Field f : CoordinatesData.class.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(coordinates) == null) {
                errorList.add(String.format("Coordinate %s isn't specified", f.getName()));
            }
        }
        if (coordinates.getX() != null && coordinates.getX() <= -706.0) {
            errorList.add("Coordinate x should be bigger than -706");
        }
        return errorList;
    }
}
