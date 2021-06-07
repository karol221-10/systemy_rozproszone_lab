package model;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tourist {
    private String id;
    private String name;
    private String surname;
    private Integer age;
}
