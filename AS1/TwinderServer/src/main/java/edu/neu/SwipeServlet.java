package edu.neu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.client.model.SwipeDetails;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {

  private static final int SWIPEE_MAX = 1000000;
  private static final int SWIPEE_MIN = 1;
  private static final int SWIPER_MAX = 5000;
  private static final int SWIPER_MIN = 1;
  private static final int COMMENT_MAX_LENGTH = 256;

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

    System.out.println("doPost 1");
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


    JsonObject swipeInfo = new JsonObject();
    swipeInfo.addProperty("swipee", swipe.getSwipee());
    swipeInfo.addProperty("swiper", swipe.getSwiper());
    swipeInfo.addProperty("comment", swipe.getComment());
    swipeInfo.addProperty("left", leftOrRight);

    try (RPCClient swipeRpc = new RPCClient()) {
      String reply = swipeRpc.call(swipeInfo.toString());
      System.out.println(" [.] Got '" + reply + "'");
//        if (reply == "OK") {
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getWriter().write("success");
//        } else {
//          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//          response.getWriter().write("failed");
//        }

    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      e.printStackTrace();
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

