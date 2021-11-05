package ru.itmo.soa.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.soa.entity.Car;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class CarData {
    private String name; //Поле не может быть null

    public Car toCar() {
        return new Car(null, name);
    }
}