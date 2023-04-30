package edu.neu;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.model.SwipeDetailsWrapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class ComsumerPlain {

  private static final int NUM_THREAD = 200;

  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
//    factory.setHost("localhost"); 18.236.132.44   172.31.16.166
    factory.setHost("35.91.117.154"); // public ip for RMQ
    final Connection connection = factory.newConnection();
    Map<Integer, List<SwipeDetailsWrapper>> swipeQueue = new ConcurrentHashMap();
    System.out.println("curr time: " + System.currentTimeMillis());
    for (int i = 0; i < NUM_THREAD; i++) {
      Thread thread = new Thread(new ConsumerTread(connection, swipeQueue));
      thread.start();
    }
    int totalCount = 0;
    for (int swiper : swipeQueue.keySet()) {
      totalCount += swipeQueue.get(swiper).size();
    }
    System.out.println("consumer2 get: " + totalCount);
  }



}
