package ru.itmo.soa.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vehicle")
public class Vehicle implements Comparable<Vehicle>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id; // Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotNull
    @NotBlank
    private String name; //Поле не может быть null, Строка не может быть пустой

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id")
    @NotNull
    Coordinates coordinates; //Поле не может быть null

    @Column(columnDefinition = "date", updatable = false)
    @CreationTimestamp
    @NotNull
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @NotNull
    private Long enginePower; //Поле может быть null, Значение поля должно быть больше 0
    @NotNull
    private Float capacity; //Поле не может быть null, Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    private VehicleType type; //Поле не может быть null
    @Enumerated(EnumType.STRING)
    private FuelType fuelType; //Поле не может быть null

    public Vehicle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }


    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }


    public int compareTo(Vehicle o) {
        return 0;
    }
}


