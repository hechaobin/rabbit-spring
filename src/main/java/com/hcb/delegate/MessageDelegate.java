package com.hcb.delegate;

import com.hcb.domain.Student;

import java.util.Map;

/**
 * 消息委托
 *
 * @author chaobin_he
 * @date 2020.4.6
 */
public class MessageDelegate {

    public void handleMessage(byte[] message) {
        String msg = new String(message);
        System.out.println("handleMessage接收到消息" + msg);
    }

    public void handleMessageText(String message) {
        System.out.println("handleMessageText接收到消息" + message);
    }

    public void handleJackson(Map message) {
        System.out.println("handleJackSon接收到消息" + message);
    }

    public void handleStudent(Student student) {
        System.out.println("handleStudent接收到消息:" + student);
    }
}
