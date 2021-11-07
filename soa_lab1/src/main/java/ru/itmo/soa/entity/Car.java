package ru.itmo.soa.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.itmo.soa.entity.data.CarData;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // в модели отсутствует
    @Column(nullable = false)
    private String name; 

    public void update(CarData carData) {
        this.name = carData.getName();
    }
}
