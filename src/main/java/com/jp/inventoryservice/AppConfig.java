package com.jp.inventoryservice;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Autowired
    EurekaClient eurekaClient;

    @Bean(name = "customer-service-web-client")
    public WebClient webClientCustomerService(WebClient.Builder webClientBuilder)
    {
        InstanceInfo service = eurekaClient
                .getApplication("ecom-customer-service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();

        return webClientBuilder
                .baseUrl("http://"+hostName+":"+port+"/api/v1")
                .filter(new LoggingWebClientFilter())
                .build();
    }
}
