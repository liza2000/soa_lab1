package ru.itmo.soa.entity.data.validator;

import ru.itmo.soa.entity.data.CarData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CarValidator implements Validator<CarData> {
    public List<String> validate(CarData car) throws IllegalAccessException {
        List<String> errorList = new ArrayList<>();
        if (car == null) {
            return errorList;
        }
        for (Field f : CarData.class.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(car) == null) {
                errorList.add(String.format("Car %s isn't specified", f.getName()));
            }
        }
        return errorList;
    }
}
