package ru.itmo.soa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.itmo.soa.entity.data.*;
import javax.persistence.*;
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "human_being")
public class HumanBeing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @Column(columnDefinition = "TEXT NOT NULL CHECK (char_length(human_being.name) > 0)")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates; //Поле не может быть null
    @Column(nullable = false, name = "creation_date", columnDefinition = "date")
    @CreationTimestamp
    @Getter
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @Column(nullable = false, name = "real_hero")
    private boolean realHero;
    @Column(nullable = false, name = "has_toothpick")
    private Boolean hasToothpick; //Поле не может быть null
    @Column(name = "impact_speed", columnDefinition = "BIGINT CHECK (human_being.impact_speed < -741)")
    private float impactSpeed; //Значение поля должно быть больше -741
    @Column(nullable = false, name = "soundtrack_name")
    private String soundtrackName; //Поле не может быть null
    @Column(nullable = false, name = "minutes_of_waiting")
    private Double minutesOfWaiting; //Поле не может быть null
    @Column(nullable = false, name = "weapon_type")
    @Enumerated(value = EnumType.STRING)
    private WeaponType weaponType; //Поле не может быть null
    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    @JoinColumn(name = "car_id")
    private Car car; //Поле не может быть null

    public void update(HumanData data) {
        this.name = data.getName();
        this.car.update(data.getCar());
        this.coordinates.update(data.getCoordinates());
        this.hasToothpick = data.getHasToothpick();
        this.impactSpeed = data.getImpactSpeed();
        this.minutesOfWaiting = data.getMinutesOfWaiting();
        this.realHero = data.getRealHero();
        this.soundtrackName = data.getSoundtrackName();
        this.weaponType = data.getWeaponType();
    }
}
