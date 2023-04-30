package edu.neu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class SwipeProducer implements Runnable{

//  final static private int NUM_ITERATIONS = 2500;
  final static private int NUM_ITERATIONS = 2500;
  private CountDownLatch countDownLatch;
  private BlockingQueue queue;

  public SwipeProducer(CountDownLatch countDownLatch, BlockingQueue queue) {
    this.countDownLatch = countDownLatch;
    this.queue = queue;
  }

  @Override
  public void run() {

    for (int i = 0; i < NUM_ITERATIONS; i++ ) {
      SwipeDetailsWrapper swipeDetailsWrapper = new SwipeDetailsWrapper();
      try {
        queue.put(swipeDetailsWrapper);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    countDownLatch.countDown();

  }
}
