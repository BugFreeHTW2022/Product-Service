package kbe.respository;

import kbe.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDao {

    public static final String HASH_KEY = "Product";
    @Autowired
    private RedisTemplate template;

    public Product save(Product product){
        template.opsForHash().put(HASH_KEY,product.getId(),product);
        return product;
    }

    public Product update(Product product){
            deleteProduct(product.getId());
            template.opsForHash().put(HASH_KEY, product.getId(), product);
            return product;

    }

    public List<Product> findAll(){
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
        List<Product> list = findAll();
        if(list.isEmpty()){
            return 1;
        }else {
            int biggest = 0;
            for (Product product:list) {
                if(product.getId()>biggest){
                    biggest = product.getId();
                }

            }
            return biggest+1;
        }
    }

}
