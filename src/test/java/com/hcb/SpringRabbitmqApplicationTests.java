package com.hcb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcb.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Map;

@SpringBootTest
class SpringRabbitmqApplicationTests {

    private String exchange_name = "spring.topic.exchange";

    private String queue_name = "spring.topic.queue";

    private String routing_key = "topic.#";

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextLoads() {
    }

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testRabbitmqAdmin() {
        rabbitAdmin.declareExchange(new TopicExchange(exchange_name, false, false, null));
        rabbitAdmin.declareQueue(new Queue(queue_name, false, false, false, null));
        rabbitAdmin.declareBinding(new Binding(queue_name, Binding.DestinationType.QUEUE, exchange_name, routing_key, null));
    }


    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Exchange testExchange;

    @Test
    public void sendMessage() {
        MessageProperties messageProperties = new MessageProperties();
        //过期时间
        messageProperties.setExpiration("10000");
        messageProperties.getHeaders().put("desc", "my first message for spring-amqp");
        Message message = new Message("send message".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(testExchange.getName(), "test.spring", message, message1 -> {
            System.out.println("-------附加属性-------");
            Map<String, Object> headers = message1.getMessageProperties().getHeaders();
            System.out.println("des = " + headers.get("desc"));
            headers.put("attr", "附加属性");
            return message1;
        });
    }

    @Test
    public void sendMessage4Text() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        //过期时间
        Message message = new Message("send message".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(testExchange.getName(), "test.spring", message);
    }

    /**
     * 发送jackson消息
     */
    @Test
    public void sendJackSon() throws Exception {
        MessageProperties messageProperties = new MessageProperties();
        Student student = new Student("001", "黄媛媛", "我的爱人");
        String json = objectMapper.writeValueAsString(student);
        System.out.println("json格式：" + json);
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        //过期时间
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(testExchange.getName(), "test.spring", message);
    }

    /**
     * 发送student消息
     *
     * @throws Exception
     */
    @Test
    public void sendJavaType() throws Exception {
        MessageProperties messageProperties = new MessageProperties();
        Student student = new Student("001", "黄媛媛", "我的爱人");
        String json = objectMapper.writeValueAsString(student);
        System.out.println("json格式：" + json);
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.getHeaders().put("__TypeId__","com.hcb.domain.Student");
        //过期时间
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(testExchange.getName(), "test.spring", message);
    }
    /**
     * 发送student消息
     *
     * @throws Exception
     */
    @Test
    public void sendIdClassType() throws Exception {
        MessageProperties messageProperties = new MessageProperties();
        Student student = new Student("001", "黄媛媛", "我的爱人");
        String json = objectMapper.writeValueAsString(student);
        System.out.println("json格式：" + json);
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.getHeaders().put("__TypeId__","student");
        //过期时间
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(testExchange.getName(), "test.spring", message);
    }
}
