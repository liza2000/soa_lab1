package ru.itmo.soa.entity.data;

import lombok.*;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.WeaponType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HumanData {
    private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private CoordinatesData coordinates; //Поле не может быть null
    private Boolean realHero; //Поле не может быть null
    private Boolean hasToothpick;
    private Float impactSpeed; //Максимальное значение поля: 333
    private String soundtrackName; //Поле не может быть null
    private Double minutesOfWaiting;
    private WeaponType weaponType; //Поле не может быть null
    private CarData car; //Поле не может быть null

    public HumanBeing toHumanBeing() {
        return new HumanBeing(
                id,
                name,
                coordinates.toCoordinates(),
                realHero,
                hasToothpick,
                impactSpeed,
                soundtrackName,
                minutesOfWaiting,
                weaponType,
                car.toCar()
        );
    }
}
