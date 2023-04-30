package edu.neu;

import com.rabbitmq.client.Connection;
import java.io.IOException;
import org.apache.commons.pool2.BasePooledObjectFactory;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * produce channel
 */
public class ChannelFactory extends BasePooledObjectFactory<Channel>{

  private int count;
  private Connection connection;

  public ChannelFactory(Connection connection) {
    this.count = 0;
    this.connection = connection;
  }

  @Override
  synchronized public Channel create() throws IOException {
    count++;
    return connection.createChannel();
  }

  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<Channel>(channel);
  }
}
