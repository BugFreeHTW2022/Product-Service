package kbe.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Product")

public class Product implements Serializable {
    @Id
    @Indexed
    int id;
    String name;
    String size;
    String dough;
    String fill;
    String glasur;
    String description;
    String extras;
    String userName;

    public String toString(){
        return  this.getId()+","+this.name+","+this.size+","+this.dough+","+this.fill+","+this.glasur+","+this.description+","+this.extras+","+this.userName;

    }
}
