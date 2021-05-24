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
public class Courier implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String surname;
    private Double age;
}
