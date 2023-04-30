package edu.neu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import edu.neu.model.SwipeDetailsWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsumerTread implements Runnable {

  private static final String TASK_QUEUE_NAME = "queue_one";

  private static final String EXCHANGE_NAME = "fanout_exchange";

  private Connection connection;
  private Map<Integer, List<SwipeDetailsWrapper>> msgList;

  private Gson gson = new Gson();

  public ConsumerTread(Connection connection, Map<Integer, List<SwipeDetailsWrapper>> msgList) {
    this.connection = connection;
    this.msgList = msgList;
  }

  @Override
  public void run() {
    try {
      final Channel channel = connection.createChannel();

//      channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//      channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
////      String queueName = channel.queueDeclare().getQueue();
//      channel.queueBind(TASK_QUEUE_NAME, EXCHANGE_NAME, "");

//      channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
//      System.out.println(" [*] consumer One Waiting for messages. To exit press CTRL+C");
      channel.basicQos(1);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");

    // System.out.println(" [x] consumer 1 Received '" + message + "'");
        try {
          doWork(message);
        } finally {
//          System.out.println(" [x] consumer 1 Done");
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
      };
      channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
        System.out.println("consumer1 : " + consumerTag);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void doWork(String msg) {
    // Given a user id, return the number of likes and dislikes this user has swiped
    SwipeDetailsWrapper swipeWrapper = this.gson.fromJson(msg.toString(), SwipeDetailsWrapper.class);
    Integer swiper = Integer.valueOf(swipeWrapper.getSwiper());
//    System.out.println(
//        "consumer 1: swiper: " + swipeWrapper.getSwiper() + " : " + swipeWrapper.getLeftOrRight());

    // TODO: 这里的ArrayList是否也要用thread safe的呢？
    msgList.put(swiper, msgList.getOrDefault(swiper, new ArrayList<>()));

  }
}
