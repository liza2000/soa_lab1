package ru.itmo.soa.entity.data.validator;

import javax.xml.bind.ValidationException;
import java.io.Serializable;
import java.util.List;

public interface Validator<T> extends Serializable {
    List<String> validate(T value)  throws IllegalAccessException, ValidationException;
}
