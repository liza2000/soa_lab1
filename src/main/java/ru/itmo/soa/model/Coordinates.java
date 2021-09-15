package ru.itmo.soa.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "coordinates")
public class Coordinates implements Comparable<Vehicle>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private int x;
    private Long y;

    public Coordinates() {
    }

    public Coordinates(int x, Long y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", x:" + x +
                ", y:" + y +
                '}';
    }

    @Override
    public int compareTo(Vehicle o) {
        return 0;
    }
}
