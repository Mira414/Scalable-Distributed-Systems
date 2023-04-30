package edu.neu;

import io.swagger.client.model.SwipeDetails;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SwipeDetailsWrapper {

  private SwipeDetails body;
  private String LeftOrRight;
  private String swiper;
  private String swipee;
  private String comment;

  public SwipeDetailsWrapper() {
    this.body = new SwipeDetails();
    body.setSwiper(this.getSwiper());
    body.setSwipee(this.getSwipee());
    body.setComment(this.getComment());
  }

  public String getLeftOrRight() {
    int random = ThreadLocalRandom.current().nextInt(0, 2);
    if (random == 1) {
      this.LeftOrRight = "left";
    } else {
      this.LeftOrRight = "right";
    }
    return LeftOrRight;
  }

  public void setLeftOrRight(String leftOrRight) {
    LeftOrRight = leftOrRight;
  }

  public String getSwiper() {
    int randomSwiper = ThreadLocalRandom.current().nextInt(1, 5001);
    this.swiper = String.valueOf(randomSwiper);
    return swiper;
  }

  public void setSwiper(String swiper) {
    this.swiper = swiper;
  }

  public String getSwipee() {
    int randomSwipee = ThreadLocalRandom.current().nextInt(1, 1000001);
    this.swipee = String.valueOf(randomSwipee);
    return swipee;
  }

  public void setSwipee(String swipee) {
    this.swipee = swipee;
  }

  public String getComment() {
    int randomCommentLength = ThreadLocalRandom.current().nextInt(1, 257);
    this.comment = generatingRandomString(randomCommentLength);
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public SwipeDetails getBody() {
    return body;
  }

  public void setBody(SwipeDetails body) {
    this.body = body;
  }

  private String generatingRandomString(int length) {
    // generate l
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomLimitedInt = leftLimit + (int)
          (random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char) randomLimitedInt);
    }
    return buffer.toString();
  }
}
