package kbe.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceProduct {

    int id;
    String name;
    String size;
    String dough;
    String fill;
    String glasur;
    String description;
    String extras;
    String userName;
    double price;


}
