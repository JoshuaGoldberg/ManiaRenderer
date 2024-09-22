import java.awt.image.BufferedImage;

public class JudgementEvent {

  BufferedImage judgement;

  int timeLeft;

  boolean complete = false;

  public JudgementEvent(BufferedImage judgement, int timeLeft) {
    this.judgement = judgement;
    this.timeLeft = timeLeft;
  }

  public BufferedImage getJudgement() {
    return judgement;
  }

  public void updateTime() {
    timeLeft -= 10;

    if(timeLeft <= 0) {
      complete = true;
    }
  }


}
