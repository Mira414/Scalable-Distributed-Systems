package edu.neu;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RPCClient implements AutoCloseable {

  private Connection connection;
  private Channel channel;
  private String requestQueueName = "twinder_req_queue";

  public RPCClient() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    connection = factory.newConnection();
    channel = connection.createChannel();
  }

  public String call(String message) throws IOException, InterruptedException, ExecutionException {
    // generate random corrId for request
    final String corrId = UUID.randomUUID().toString();
//    System.out.println("corrId = " + corrId);

    // generate random queue name to response queue
    String replyQueueName = channel.queueDeclare().getQueue();
    System.out.println("replyQueueName = " + replyQueueName);
    AMQP.BasicProperties props = new AMQP.BasicProperties
        .Builder()
        .correlationId(corrId)
        .replyTo(replyQueueName)
        .build();

//    System.out.println("call 1");

    channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

    final CompletableFuture<String> response = new CompletableFuture<>();

//    System.out.println("call 2");

    String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
      if (delivery.getProperties().getCorrelationId().equals(corrId)) {
//        System.out.println("replyQueueName = " + replyQueueName + " corrId = "+ corrId);
        response.complete(new String(delivery.getBody(), "UTF-8"));
      }
    }, consumerTag -> {
    });

//    System.out.println("call 3");

    String result = response.get();
    System.out.println("result= " + result );

    // the consumer_tag is a unique identifier for a specific consumer instance that is consuming messages from a queue.
    // When there are multiple consumers subscribed to the same queue, each of them will have a different consumer_tag
    channel.basicCancel(ctag);
//    System.out.println("client ctag = " + ctag);
    return result;
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }
}
