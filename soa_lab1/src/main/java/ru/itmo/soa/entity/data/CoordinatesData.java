package ru.itmo.soa.entity.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.soa.entity.Coordinates;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CoordinatesData {
    private Float x; //Значение поля должно быть больше -706
    private Float y; //Поле не может быть null

    public Coordinates toCoordinates() {
        return new Coordinates(null, x, y);
    }
}
