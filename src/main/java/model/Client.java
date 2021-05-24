package model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Builder
@Setter
@EqualsAndHashCode
public class Client implements Serializable {
    private String name;
    private String surname;
    private String city;
}
