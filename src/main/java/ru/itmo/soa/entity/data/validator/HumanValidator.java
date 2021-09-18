package ru.itmo.soa.entity.data.validator;


import ru.itmo.soa.entity.data.HumanData;

import jakarta.validation.ValidationException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HumanValidator implements Validator<HumanData> {

    private final CarValidator carValidator;
    private final CoordinatesValidator coordinatesValidator;

    public HumanValidator() {
        carValidator = new CarValidator();
        coordinatesValidator = new CoordinatesValidator();
    }

    public List<String> validate(HumanData human) throws IllegalAccessException, jakarta.validation.ValidationException {
        List<String> errorList = new ArrayList<>();
        for (Field f : HumanData.class.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(human) == null) {
                errorList.add((String.format("human_being %s isn't specified", f.getName())));
            }
        }
        if (human.getImpactSpeed() != null && human.getImpactSpeed() <= -741) {
            errorList.add("human_being impact_speed should be not bigger than -741");
        }
        if (human.getName() != null && human.getName().trim().length() == 0) {
            errorList.add("human_being name should be not empty");
        }
        errorList.addAll(carValidator.validate(human.getCar()));
        errorList.addAll(coordinatesValidator.validate(human.getCoordinates()));
        if (errorList.size() > 0) {
            throw new ValidationException(String.join(", ", errorList));
        }
        return errorList;
    }
}
