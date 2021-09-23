package ru.itmo.soa.entity.data.validator;


import ru.itmo.soa.entity.data.HumanData;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

public class HumanValidator implements Validator<HumanData> {

    private final CarValidator carValidator;
    private final CoordinatesValidator coordinatesValidator;

    public HumanValidator() {
        carValidator = new CarValidator();
        coordinatesValidator = new CoordinatesValidator();
    }

    public List<String> validate(HumanData human) throws  ValidationException {
        List<String> errorList = new ArrayList<>();
        if (human.getName() == null)
            errorList.add("human_being name isn't specified");
        if (human.getRealHero() == null)
            errorList.add("human_being real hero isn't specified");
        if (human.getHasToothpick() == null)
            errorList.add("human_being \"has toothpick\" hero isn't specified");
        if (human.getImpactSpeed() == null)
            errorList.add("human_being impact speed isn't specified");
        if (human.getSoundtrackName() == null)
            errorList.add("human_being soundtrack name isn't specified");
        if (human.getMinutesOfWaiting() == null)
            errorList.add("human_being minutes of waiting name isn't specified");
        if (human.getWeaponType() == null)
            errorList.add("human_being weapon type isn't specified");
        if (human.getImpactSpeed() != null && human.getImpactSpeed() <= -741)
            errorList.add("human_being impact_speed should be not bigger than -741");
        if (human.getName() != null && human.getName().trim().length() == 0)
            errorList.add("human_being name should be not empty");
        errorList.addAll(carValidator.validate(human.getCar()));
        errorList.addAll(coordinatesValidator.validate(human.getCoordinates()));
        if (errorList.size() > 0)
            throw new ValidationException(String.join(", ", errorList));

        return errorList;
    }
}
