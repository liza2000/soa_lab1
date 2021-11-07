package ru.itmo.soa.entity.data.validator;

import ru.itmo.soa.entity.data.CarData;

import java.util.ArrayList;
import java.util.List;

public class CarValidator implements Validator<CarData> {
    public List<String> validate(CarData car) {
        List<String> errorList = new ArrayList<>();
        if (car == null)
            return errorList;
            if (car.getName()==null)
                errorList.add("Car name isn't specified");
        return errorList;
    }
}
