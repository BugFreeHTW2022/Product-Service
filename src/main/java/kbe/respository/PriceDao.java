package kbe.respository;

import kbe.entity.Price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceDao {

    public static final String HASH_KEY = "Price";
    @Autowired
    private RedisTemplate template;

    public Price save(Price price){
        template.opsForHash().put(HASH_KEY,price.getId(),price);
        return price;
    }

    public Price update(Price price){
        deletePrice(price.getId());
        template.opsForHash().put(HASH_KEY, price.getId(), price);
        return price;

    }

    public List<Price> findAll(){
        return template.opsForHash().values(HASH_KEY);
    }

    public Price findPriceById(int id){
        return (Price) template.opsForHash().get(HASH_KEY,id);
    }


    public String deletePrice(int id){
        template.opsForHash().delete(HASH_KEY,id);
        return "product removed !!";
    }


}
