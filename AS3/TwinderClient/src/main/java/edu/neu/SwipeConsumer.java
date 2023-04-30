package edu.neu;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class SwipeConsumer implements Runnable {

//  final static private int NUM_ITERATIONS = 2500;
  final static private int NUM_ITERATIONS = 2500;

  private CountDownLatch countDownLatch;
  private BlockingQueue<SwipeDetailsWrapper> queue;

  public SwipeConsumer(CountDownLatch countDownLatch, BlockingQueue<SwipeDetailsWrapper> queue) {
    this.countDownLatch = countDownLatch;
    this.queue = queue;
  }

  @Override
  public void run() {

    for (int i = 0; i < NUM_ITERATIONS; i++) {

      ApiClient client = new ApiClient();
//      client.setBasePath("http://localhost:8080/twinder_server_war_exploded/swipe/");
      client.setBasePath("http://54.187.235.101:8080/twinder-server_war/swipe/");
      SwipeApi apiInstance = new SwipeApi(client);
      SwipeDetailsWrapper swipeDetailsWrapper;
      try {
        swipeDetailsWrapper = queue.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      try {
        ApiResponse<Void> response;
        response = apiInstance.swipeWithHttpInfo(swipeDetailsWrapper.getBody(), swipeDetailsWrapper.getLeftOrRight());
//        if (response.getStatusCode() != 201) {
//          System.out.println(response.getStatusCode());
//        }
      } catch (ApiException e) {
        System.err.println("Exception when calling SwipeApi#swipeDetailsWrapper, e.getCode() = " + e.getCode());
        e.printStackTrace();
      }
    }
    countDownLatch.countDown();
  }


}
