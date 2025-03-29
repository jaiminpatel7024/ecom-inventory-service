package com.jp.inventoryservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Consumer
{
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    InventoryRepository inventoryRepo;
    @KafkaListener(topics = "product-events", groupId = "ecom-product-events-inventory-service")
    public void consumeProductEvents(String message) throws IOException
    {
        //analytics_counter.increment();
        ObjectMapper mapper  = new ObjectMapper();
        ProductData datum =  mapper.readValue(message, ProductData.class);

        logger.info(String.format("#### -> Consumed message -> %s", message));

        if(datum.getUpdateType().equalsIgnoreCase("NEW")){
            logger.info("New product details with name {} added to Product Catalog Service.", datum.getProductName());
            Inventory tempObj = new Inventory();
            tempObj.setProductId(datum.getProductId());
            tempObj.setQuantities(0);

            inventoryRepo.save(tempObj);
            logger.info("Inventory data added for given product : {} ",tempObj.getProductId());
        } else if(datum.getUpdateType().equalsIgnoreCase("DELETE")){
            logger.info("Product data deleted for id : {} ", datum.getProductId());
            inventoryRepo.deleteById(datum.getProductId());
            logger.info("Inventory data deleted for given product : {} ",datum.getProductId());
        }
    }
}