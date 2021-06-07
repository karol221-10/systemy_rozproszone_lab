package model;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Client {
    private static final long serialVersionUID = 1L;
    private String _id;
    private String name;
    private String surname;
    private Integer age;
}