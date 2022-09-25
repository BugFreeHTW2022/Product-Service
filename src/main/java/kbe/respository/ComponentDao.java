package kbe.respository;

import kbe.entity.Component;
import kbe.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public class ComponentDao {

    public static final String HASH_KEY = "Component";
    @Autowired
    private RedisTemplate template;

    public Component save(Component component){
        template.opsForHash().put(HASH_KEY,component.getId(),component);
        return component;
    }

    public List<Component> findAll(){
        return template.opsForHash().values(HASH_KEY);
    }

    public Product findProductById(int id){
        return (Product) template.opsForHash().get(HASH_KEY,id);
    }


    public String deleteProduct(int id){
        template.opsForHash().delete(HASH_KEY,id);
        return "product removed !!";
    }



    public int generateId(){
        List<Component> list = findAll();
        if(list.isEmpty()){
            return 1;
        }else {
            int biggest = 0;
            for (Component component:list) {
                if(component.getId()>biggest){
                    biggest = component.getId();
                }

            }
            return biggest+1;
        }
    }



}
