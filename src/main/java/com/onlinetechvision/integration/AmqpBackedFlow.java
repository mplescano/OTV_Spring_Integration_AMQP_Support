package com.onlinetechvision.integration;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.channel.AbstractAmqpChannel;
import org.springframework.integration.amqp.config.AmqpChannelFactoryBean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.messaging.MessageChannel;

import com.onlinetechvision.process.service.OrderProcessService;

@Configuration
public class AmqpBackedFlow {

    private static final String HA_RABBIT_QUEUE = "ha.rabbit.channel";
    
    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public OrderGateway orderGateway(@Qualifier("inputChannel") MessageChannel inputChannel, 
                                     BeanFactory beanFactory) throws Exception {
        GatewayProxyFactoryBean factory = new GatewayProxyFactoryBean(OrderGateway.class);
        factory.setBeanFactory(beanFactory);
        factory.setDefaultRequestChannel(inputChannel);
        factory.afterPropertiesSet();
        return (OrderGateway) factory.getObject();
    }

    @Bean
    public AbstractAmqpChannel processChannel(ConnectionFactory connectionFactory, 
                                              BeanFactory beanFactory) throws Exception {
        AmqpChannelFactoryBean factory = new AmqpChannelFactoryBean(true);
        factory.setConnectionFactory(connectionFactory);
        factory.setBeanFactory(beanFactory);
        factory.setQueueName(HA_RABBIT_QUEUE);
        factory.setBeanName("processChannel");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    @Splitter(inputChannel = "inputChannel", outputChannel = "processChannel")
    public OrderSplitter orderSplitter() throws Exception {
        return new OrderSplitter();
    }

    @Bean
    @ServiceActivator(inputChannel = "processChannel")
    public OrderProcessService orderProcessServiceActivator(Logger logger) {
        return new OrderProcessService(logger);
    }
}