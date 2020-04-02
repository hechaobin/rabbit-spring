package com.hcb;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SpringRabbitmqApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testRabbitmqAdmin() {
        rabbitAdmin.declareExchange(new TopicExchange("spring.topic.exchange", false, false, null));
        rabbitAdmin.declareQueue(new Queue("spring.topic.queue", false, false, false, null));
        rabbitAdmin.declareBinding(new Binding("spring.topic.queue", Binding.DestinationType.QUEUE, "spring.rabbit.topic", "topic.#", null));
    }
}
