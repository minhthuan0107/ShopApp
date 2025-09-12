package com.project.shopapp.configurations.rabbitmqconfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitMQConfig {
    public static final String EXCHANGE_NAME = "order.exchange";
    public static final String ROUTING_KEY = "order.notify";
    public static final String QUEUE_NAME = "order.notify.queue";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Queue lưu message tạm thời
    @Bean
    public Queue orderQueue() {
        return new Queue(QUEUE_NAME);
    }

    // Binding giữa Exchange và Queue
    @Bean
    public Binding orderBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // Cấu hình Converter để deserialize JSON thành object
    @Bean(name = "orderMessageConverter")
    public MessageConverter orderMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("com.project.shopapp.responses.customer.notification",
                "com.project.shopapp.models"); // <- đổi đúng package chứa class DTO
        converter.setClassMapper(classMapper);
        return converter;
    }
    // Đăng ký converter này cho RabbitListener
    @Bean(name = "orderRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory orderRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(orderMessageConverter());
        return factory;
    }
}
