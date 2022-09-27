package kbe.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessagingConfig {

    public static final String ProductMSQueueName = "ProductMSQueue";
    public static final String PriceMSQueueName = "PriceMSQueue";

    public static final String productExchange = "productExchange";
    public static final String priceExchange = "priceExchange";
    public static final String ProductMSRoutingKey = "ProductMSRoutingKey";
    public static final String PriceMSRoutingKey = "PriceMSRoutingKey";




    @Bean
    public Queue ProductMSQueue(){
        return new Queue(ProductMSQueueName);
    }

    @Bean
    public Queue PriceMSQueue(){
        return new Queue(PriceMSQueueName);
    }


    @Bean
    public DirectExchange ProductExchange(){
        return new DirectExchange(productExchange);
    }
    @Bean
    public DirectExchange PriceExchange(){
        return new DirectExchange(priceExchange);
    }

    @Bean
    public Binding bindingPriceMS(){
        return BindingBuilder.bind(PriceMSQueue()).to(PriceExchange()).with(PriceMSRoutingKey);
    }

    @Bean
    public Binding bindingProductMS(){
        return BindingBuilder.bind(ProductMSQueue()).to(ProductExchange()).with(ProductMSRoutingKey);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabTemplate(ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }






}

