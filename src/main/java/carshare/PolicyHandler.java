package carshare;

import carshare.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }
    @Autowired
    ProductRepository productRepository;


    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_Productchange(@Payload Ordered ordered){

        if(ordered.isMe()){
            Product product = null;
            Optional<Product> optional = productRepository.findById(ordered.getProductId());
            if(optional.isPresent()) {
                product = optional.get();
                product.setId(ordered.getProductId());
                product.setQty((product.getQty() != null ? product.getQty().intValue() - ordered.getQty() : 0));
                productRepository.save(product);

            }
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_Productchange(@Payload OrderCancelled orderCancelled){

        if(orderCancelled.isMe()){
            Product product = null;
            Optional<Product> optional = productRepository.findById(orderCancelled.getProductId());
            if(optional.isPresent()) {
                product = optional.get();
                product.setId(orderCancelled.getProductId());
                product.setQty(product.getQty() != null ? product.getQty().intValue() + orderCancelled.getQty() : 0);
                productRepository.save(product);

            }
        }
    }

}
