package ru.itmo.soa.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.itmo.soa.entity.data.CoordinatesData;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // в модели отсутствует
    @Column(columnDefinition = "REAL NOT NULL CHECK (Coordinates.x > -706)")
    private float x; //Значение поля должно быть больше -706
    private Float y; //Поле не может быть null

    public void update(CoordinatesData data) {
        this.x = data.getX();
        this.y = data.getY();
    }
}
