import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Producto implements Serializable {
    private final String name;
    private final String category;
    private double precio;
}
