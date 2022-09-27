package kbe.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kbe.config.MessagingConfig;
import kbe.config.ProductServiceRequest;
import kbe.entity.Price;
import kbe.entity.PriceProduct;
import kbe.entity.Product;
import kbe.respository.PriceDao;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Component
public class ProductServiceProcessor {


    private final RestTemplate restTemplate;

    public ProductServiceProcessor(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    @Autowired
    private PriceDao priceDao;

    @Autowired
    private RabbitTemplate template;


    @RabbitListener(queues = MessagingConfig.ProductMSQueueName)
    public Object receiveMessage(ProductServiceRequest request) {


        if (request.getRequestType().equals("createProduct")) {

            String[] arr = request.message.split(",");
            Double pr = calculatePrice(arr);
            Product entity = new Product(0, arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7]);
            List<String> comp = Arrays.asList(arr);
            String url = "http://localhost:9297/addProduct";
            int id = this.restTemplate.postForObject(url, entity, int.class);
            Price price = new Price(id, pr);
            priceDao.save(price);
            return "Product was added";

        } else if (request.getRequestType().equals("getComponents")) {
            String url = "http://localhost:9297/findAllComponents";
            List<kbe.entity.Component> components = this.restTemplate.getForObject(url, List.class);
            return components;

        } else if (request.getRequestType().equals("getProducts")) {
            ObjectMapper mapper = new ObjectMapper();
            String url = "http://localhost:9297//findAllProducts";
            List<Product> products = this.restTemplate.getForObject(url, List.class);
            List<Product> convertedProducts = mapper.convertValue(products, new TypeReference<List<Product>>() {
            });

            return calculatePrices(convertedProducts);

        } else if (request.getRequestType().equals("getProduct")) {
            String url = "http://localhost:9297//findProductsById/" + request.message;
            Product product = this.restTemplate.getForObject(url, Product.class);
            if (product != null) {
                if (priceDao.findPriceById(product.getId()) == null) {
                    Double pr = calculatePrice(product.toString().split(","));
                    PriceProduct priceProduct  = new PriceProduct(product.getId(), product.getName(), product.getSize(), product.getDough(), product.getFill(), product.getGlasur(), product.getDescription(), product.getExtras(), product.getUserName(), pr);
                    Price price = new Price(product.getId(), pr);
                    priceDao.save(price);
                    return priceProduct;
                } else {
                    PriceProduct priceProduct = new PriceProduct(product.getId(), product.getName(), product.getSize(), product.getDough(), product.getFill(), product.getGlasur(), product.getDescription(), product.getExtras(), product.getUserName(), priceDao.findPriceById(product.getId()).getPrice());
                    return priceProduct;
                }
            } else {
                return "{}";
            }


        } else if (request.getRequestType().equals("getProductsFromUser")) {
            ObjectMapper mapper = new ObjectMapper();
            String url = "http://localhost:9297/findUserProducts/" + request.message;
            List<Product> userProducts = this.restTemplate.getForObject(url, List.class);
            List<Product> convertedUserProducts = mapper.convertValue(userProducts, new TypeReference<List<Product>>() {
            });
            return calculatePrices(convertedUserProducts);


        } else if (request.getRequestType().equals("updateProduct")) {
            String[] arr = request.message.split(",");
            String url = "http://localhost:9297/updateProduct";
            Product entity = new Product(Integer.parseInt(arr[8]), arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7]);
            int response = this.restTemplate.postForObject(url, entity, int.class);
            if (response == 1) {
                Double pr = calculatePrice(Arrays.copyOfRange(arr, 0, 6));
                Price price = new Price(entity.getId(), pr);
                priceDao.update(price);
                return "Product was updated";
            } else {
                return "Product was not found";
            }

        } else if (request.getRequestType().equals("deleteProduct")) {
            String url = "http://localhost:9297/delete/" + request.message;
            this.restTemplate.delete(url);
            priceDao.deletePrice(Integer.parseInt(request.message));
            return "Product was deleted";
        }

        return "Something went Wrong. Request was not recognized";


    }

    Double calculatePrice(String[] arr) {
        ObjectMapper mapper = new ObjectMapper();
        String url = "http://localhost:9297/findAllComponents";
        List<kbe.entity.Component> list = this.restTemplate.getForObject(url, List.class);
        List<kbe.entity.Component> list2 = mapper.convertValue(list, new TypeReference<List<kbe.entity.Component>>() {
        });
        List<String> list3 = Arrays.asList(arr);
        List<kbe.entity.Component> list4 = new ArrayList<>();
        for (kbe.entity.Component component : list2) {
            if (list3.contains(component.getName())) {
                list4.add(component);
            }
        }
        Object b = template.convertSendAndReceive(MessagingConfig.priceExchange, MessagingConfig.PriceMSRoutingKey, list4);
        return (Double) b;
    }


    List<PriceProduct> calculatePrices(List<Product> list) {
        List<PriceProduct> list3 = new ArrayList<>();
        for (Product p : list) {
            if (priceDao.findPriceById(p.getId()) == null) {
                Double pr = calculatePrice(p.toString().split(","));
                list3.add(new PriceProduct(p.getId(), p.getName(), p.getSize(), p.getDough(), p.getFill(), p.getGlasur(), p.getDescription(), p.getExtras(), p.getUserName(), pr));
                Price price = new Price(p.getId(), pr);
                priceDao.save(price);
            } else {
                list3.add(new PriceProduct(p.getId(), p.getName(), p.getSize(), p.getDough(), p.getFill(), p.getGlasur(), p.getDescription(), p.getExtras(), p.getUserName(), priceDao.findPriceById(p.getId()).getPrice()));
            }

        }
        return list3;
    }


}
