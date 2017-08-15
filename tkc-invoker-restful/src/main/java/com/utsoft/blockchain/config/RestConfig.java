package com.utsoft.blockchain.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author hunterfox
 * @date: 2017年8月15日
 * @version 1.0.0
 */
@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
      return new RestTemplate(factory);
   }
    
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(15000);
        return factory;
    }
}
