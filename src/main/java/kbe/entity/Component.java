package kbe.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Component")

public class Component {
    @Id
    @Indexed
    int id;
    String name;
    String description;
    String componentGroup;
    String nutriscore;
    boolean vegan;
    int price;
}
