package ru.itmo.soa.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.soa.entity.Car;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CarData {
    private String name; //Поле может быть null

    public Car toCar() {
        return new Car(0, name);
    }
}