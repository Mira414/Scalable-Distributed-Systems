package edu.neu.model;

import com.google.gson.annotations.SerializedName;

public class SwipeDetailsWrapper {

  @SerializedName("swiper")
  private String swiper = null;
  @SerializedName("swipee")
  private String swipee = null;
  @SerializedName("comment")
  private String comment = null;
  @SerializedName("leftOrRight")
  private String LeftOrRight = null;

  public SwipeDetailsWrapper() {
  }

  public String getSwiper() {
    return swiper;
  }

  public void setSwiper(String swiper) {
    this.swiper = swiper;
  }

  public String getSwipee() {
    return swipee;
  }

  public void setSwipee(String swipee) {
    this.swipee = swipee;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getLeftOrRight() {
    return LeftOrRight;
  }

  public void setLeftOrRight(String leftOrRight) {
    LeftOrRight = leftOrRight;
  }
}
