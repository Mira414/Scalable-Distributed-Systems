import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class SwipeMultiMain {

  final static private int TOTAL_PRODUCER_THREADS = 200;
  final static private int TOTAL_CONSUMER_THREADS = 200;
  final static private int QUEUE_CAPACITY = 5000;

  private int count = 0;

  public static void main(String[] args) throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(TOTAL_PRODUCER_THREADS + TOTAL_CONSUMER_THREADS);

    Long startTime = System.currentTimeMillis();
    System.out.println("start time: " + startTime);

    BlockingQueue<SwipeDetailsWrapper> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    // producer - generate data (body) - store body into queue
    for (int i = 0; i < TOTAL_PRODUCER_THREADS; i++) {
      new Thread(new SwipeProducer(completed, queue)).start();
    }

    // consumer - get data from queue and send data
    for (int i = 0; i < TOTAL_CONSUMER_THREADS; i++) {
      new Thread(new SwipeComsumer(completed, queue)).start();
    }
    completed.await();

    Long endTime1 = System.currentTimeMillis();
    System.out.println("end time:" + endTime1 +
        " diff: " + (endTime1 - startTime));
  }

}
