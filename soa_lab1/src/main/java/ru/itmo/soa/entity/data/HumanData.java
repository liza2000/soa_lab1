package ru.itmo.soa.entity.data;

import lombok.*;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.WeaponType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HumanData {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private CoordinatesData coordinates; //Поле не может быть null
    private Boolean realHero;
    private Boolean hasToothpick; //Поле не может быть null
    private Float impactSpeed; //Значение поля должно быть больше -741
    private String soundtrackName; //Поле не может быть null
    private Double minutesOfWaiting; //Поле не может быть null
    private WeaponType weaponType; //Поле не может быть null
    private CarData car; //Поле может быть null

    public HumanBeing toHumanBeing() {
        return new HumanBeing(
                null,
                name,
                coordinates.toCoordinates(),
                null,
                realHero,
                hasToothpick,
                impactSpeed,
                soundtrackName,
                minutesOfWaiting,
                weaponType,
                car==null?null:car.toCar()
        );
    }
}
