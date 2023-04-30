package edu.neu;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import edu.neu.model.SwipeDetailsWrapper;

public class ComsumerRPC {

  private static final String REQ_QUEUE_NAME = "twinder_req_queue";

  private static int fib(int n) {
    if (n == 0) {
      return 0;
    }
    if (n == 1) {
      return 1;
    }
    return fib(n - 1) + fib(n - 2);
  }

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.queueDeclare(REQ_QUEUE_NAME, false, false, false, null);
    // When you call this method, all the messages that are currently in the queue will be removed,
    // regardless of their state (whether they are acknowledged or not).
    //channel.queuePurge(RPC_QUEUE_NAME);

    channel.basicQos(1);

    System.out.println(" [x] Awaiting requests");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      AMQP.BasicProperties replyProps = new AMQP.BasicProperties
          .Builder()
          .correlationId(delivery.getProperties().getCorrelationId())
          .build();

      String response = "";
      try {
        String message = new String(delivery.getBody(), "UTF-8");
        Gson gson = new Gson();
        SwipeDetailsWrapper swipe = gson.fromJson(message.toString(), SwipeDetailsWrapper.class);
        System.out.println("swiper = " + swipe.getSwiper() + " swipee = " + swipe.getSwipee());
        response += "OK";
//        response += fib(n);
      } catch (RuntimeException e) {
        System.out.println(" [.] " + e);
      } finally {
        System.out.println("server ReplyTo() = " + delivery.getProperties().getReplyTo());
        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps,
            response.getBytes("UTF-8"));
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      }
    };

    channel.basicConsume(REQ_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
    }));
  }

}
