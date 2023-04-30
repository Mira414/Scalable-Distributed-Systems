package edu.neu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import io.swagger.client.model.SwipeDetails;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServletWithoutChannelPool extends HttpServlet {

  private static final int SWIPEE_MAX = 1000000;
  private static final int SWIPEE_MIN = 1;
  private static final int SWIPER_MAX = 5000;
  private static final int SWIPER_MIN = 1;
  private static final int COMMENT_MAX_LENGTH = 256;

  private static final String EXCHANGE_NAME = "fanout_exchange";
  private static final String QUEUE_ONE = "queue_one";
  private static final String QUEUE_TWO = "queue_two";



  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException {
    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    // TODO: process url params in `urlParts`
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getWriter().write("incorrect parameters");
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      res.getWriter().write("It works!");
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    StringBuilder sb = new StringBuilder();
    String s;

    // validate path and value of left
    String[] requestPath = request.getPathInfo().split("/");
    String leftOrRight = requestPath[requestPath.length - 1];
    Set<String> validLeftOrRight = new HashSet<>();
    validLeftOrRight.add("left");
    validLeftOrRight.add("right");

    if (!validLeftOrRight.contains(leftOrRight)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      return;
    }

    SwipeDetails swipe = null;
    while ((s = request.getReader().readLine()) != null) {
      sb.append(s.trim());
    }
    swipe = gson.fromJson(sb.toString(), SwipeDetails.class);

    // 0. 路径上存在swipe - either “left” or “right”
    // 1. 存在swiper, swipee两个参数
//    2. swiper, swipee的值在范围内：
//    swiper - between 1 and 5000
//    swipee - between 1 and 1000000
//      3.如果存在comment，长度在256内
    if (swipe == null || !isNumber(swipe.getSwiper()) || !isNumber(swipe.getSwipee())
        || Integer.valueOf(swipe.getSwipee()) < SWIPEE_MIN
        || Integer.valueOf(swipe.getSwipee()) > SWIPEE_MAX
        || Integer.valueOf(swipe.getSwiper()) < SWIPER_MIN
        || Integer.valueOf(swipe.getSwiper()) > SWIPER_MAX
        || swipe.getComment() == null || swipe.getComment().length() > COMMENT_MAX_LENGTH) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    JsonObject swipeJson = new JsonObject();
    swipeJson.addProperty("swipee", swipe.getSwipee());
    swipeJson.addProperty("swiper", swipe.getSwiper());
    swipeJson.addProperty("comment", swipe.getComment());
    swipeJson.addProperty("leftOrRight", leftOrRight);

    // ========= two queues =======
    try {
      produce(swipeJson.toString());
    } catch (TimeoutException e) {
      throw new RuntimeException(e);
    }

    // ========= RPC model for lab6========
//    try (RPCClient swipeRpc = new RPCClient()) {
//      String reply = swipeRpc.call(swipeInfo.toString());
//      System.out.println(" [.] Got '" + reply + "'");
//      response.setStatus(HttpServletResponse.SC_CREATED);
//      response.getWriter().write("success");
//
//    } catch (Exception e) {
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      e.printStackTrace();
//    }
  }

  private void produce(String message) throws IOException, TimeoutException {
//    String message = String.join(" ", msg);
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
        Channel channel1 = connection.createChannel()) {

//      channel1.exchangeDelete(EXCHANGE_NAME);
      channel1.exchangeDeclare(EXCHANGE_NAME, "fanout");

      channel1.queueDeclare(QUEUE_ONE, true, false, false, null);
      channel1.queueDeclare(QUEUE_TWO, true, false, false, null);

      channel1.queueBind(QUEUE_ONE, EXCHANGE_NAME, "");
      channel1.queueBind(QUEUE_TWO, EXCHANGE_NAME, "");

      channel1.basicPublish(EXCHANGE_NAME, "",
          MessageProperties.PERSISTENT_TEXT_PLAIN,
          message.getBytes("UTF-8"));

//      channel2.queueDeclare(QUEUE_TWO, true, false, false, null);
//      channel2.basicPublish("", QUEUE_TWO,
//          MessageProperties.PERSISTENT_TEXT_PLAIN,
//          message.getBytes("UTF-8"));
//      System.out.println(" [x] Sent '" + message + "'");
    }

  }

  private boolean isNumber(String s) {
    if (s == null) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      if (!Character.isDigit(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private boolean isUrlValid(String[] urlPath) {
    return true;
  }
}

