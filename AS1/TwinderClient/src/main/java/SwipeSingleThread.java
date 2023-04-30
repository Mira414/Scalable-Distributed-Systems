import io.swagger.client.*;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.*;
import java.util.concurrent.ThreadLocalRandom;

public class SwipeSingleThread {

  public static void main(String[] args) {

    ApiClient client = new ApiClient();
    client.setBasePath("http://52.10.209.215:8080/twinder-server_war/");
//    client.setBasePath("http://localhost:8080/twinder_server_war_exploded/swipe/");
    SwipeApi apiInstance = new SwipeApi(client);
//    "http://localhost:8080/twinder_server_war_exploded/swipe"
//    apiInstance.
    SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details
    body.setSwipee("11");
    body.setSwiper("2222");
    body.setComment("lklklkk");
//    String leftorright = "leftorright_example"; // String | Ilike or dislike user
//    int randomLeftOrRight = ThreadLocalRandom.current().nextInt(0, 2);
//    int randomSwiper = ThreadLocalRandom.current().nextInt(1, 5001);
//    int randomSwipee = ThreadLocalRandom.current().nextInt(1, 1000001);
//    String randomComment = "";
    try {
//      body.setSwipee(String.valueOf(randomSwipee));
//      body.setSwiper(String.valueOf(randomSwiper));
//      body.setComment(randomComment);
      ApiResponse<Void> response = null;
//      if (randomLeftOrRight == 1) {
//        response = apiInstance.swipeWithHttpInfo(body, "left");
//      } else {
//        response = apiInstance.swipeWithHttpInfo(body, "right");
//      }
      response = apiInstance.swipeWithHttpInfo(body, "left");
      System.out.println(response.getStatusCode());
    } catch (ApiException e) {
      System.out.println("e.getCode() = " + e.getCode());
      System.err.println("Exception when calling SwipeApi#swipe");
//      e.printStackTrace();
    }
  }
}
