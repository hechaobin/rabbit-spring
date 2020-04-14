package com.hcb.config;

import com.hcb.converter.TextMessageConverter;
import com.hcb.delegate.MessageDelegate;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * rabbitmq配置类
 *
 * @author chaobin_he
 * @date 2020.4.2
 */
@Configuration
@ComponentScan(basePackages = {"com.hcb.config"})
public class RabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("my-aliyun:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setConnectionTimeout(15000);
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Queue testQueue() {
        return new Queue("spring.test.queue", false, false, false, null);
    }

    @Bean
    public Queue testQueue1() {
        return new Queue("spring.test.queue1", false, false, false, null);
    }

    @Bean
    public Queue testQueue2() {
        return new Queue("spring.test.queue2", false, false, false, null);
    }

    @Bean
    public TopicExchange testExchange() {
        return new TopicExchange("spring.test.exchange", false, false, null);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(testQueue()).to(testExchange()).with("test.#");
    }

    @Bean
    public Binding bindingQueue1() {
        return BindingBuilder.bind(testQueue1()).to(testExchange()).with("test.queue1.#");
    }

    @Bean
    public Binding bindingQueue2() {
        return BindingBuilder.bind(testQueue2()).to(testExchange()).with("test.queue2.#");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("correlationData = " + correlationData);
            }
        });
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {

        });
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(testQueue(), testQueue1(), testQueue2());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        container.setConsumerTagStrategy(queue -> queue + "_" + UUID.randomUUID().toString());
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        /**
         * 1 监听方式
         container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
         String body = new String(message.getBody());
         System.out.println("SimpleMessageListenerContainer 接收到消息 = " + body);
         });
         */
        /**
         * 2 自定义消息转换器
         adapter.setDefaultListenerMethod("handleMessageText");
         adapter.setMessageConverter(new TextMessageConverter());
         container.setMessageListener(adapter);
         */
        /**
         * jackson转换器
         *  Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
         *  adapter.setDefaultListenerMethod("handleJackson");
         *  adapter.setMessageConverter(converter);
         */

        /**
         *
         java对象
         adapter.setDefaultListenerMethod("handleStudent");
         Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         javaTypeMapper.setTrustedPackages("*");
         converter.setJavaTypeMapper(javaTypeMapper);
         */
        /**
         * 多对象映射
         *         adapter.setDefaultListenerMethod("handleStudent");
         *         Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
         *         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         *         Map<String, Class<?>> idClassMapping = new HashMap<>(1);
         *         idClassMapping.put("student", Student.class);
         *         javaTypeMapper.setIdClassMapping(idClassMapping);
         *         converter.setJavaTypeMapper(javaTypeMapper);
         *         adapter.setMessageConverter(converter);
         *         container.setMessageListener(adapter);
         */
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();
        converter.addDelegate("text", new TextMessageConverter());
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        jsonMessageConverter.setJavaTypeMapper(new DefaultJackson2JavaTypeMapper());
        converter.addDelegate("application/json", jsonMessageConverter);

        return container;
    }
}
