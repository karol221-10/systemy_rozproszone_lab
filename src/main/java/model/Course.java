package model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class Course implements Serializable {
    private String courseId;
    private Client sourceClient;
    private Client destinationClient;
    private Courier courier;
    private String productName;
    private LocalDate arrivalDate;
}
