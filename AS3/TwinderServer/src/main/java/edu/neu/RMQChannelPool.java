package edu.neu;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * a pool of channels
 */
public class RMQChannelPool {

  private ChannelFactory channelFactory;
  private final BlockingQueue<Channel> pool;

  // fixed size pool
  private int capacity;

  public RMQChannelPool(ChannelFactory channelFactory, int capacity)
      throws IOException, InterruptedException {
    this.channelFactory = channelFactory;
    this.capacity = capacity;
    this.pool = new ArrayBlockingQueue<>(capacity);
    for (int i = 0; i < capacity; i++) {
      Channel channel = channelFactory.create();
      pool.put(channel);
    }
  }

  public Channel borrowChannel() {
    try {
      return pool.take();
    } catch (InterruptedException e) {
      throw new RuntimeException("Error: no channels available" + e.toString());
    }
  }

  public void returnChannel(Channel channel) {
    if (channel != null) {
      pool.add(channel);
    }
  }
}
