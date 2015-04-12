package models;

import java.util.List;
import java.util.ArrayList;

/**
 * Defines a feature of a location.
 */
public class Feature {
  public final static int ST_UNKNOWN = 0;

  /**
   * Constructs a new feature.
   *
   * @param name Name of feature.
   * @param lowLabel Description for low scores of this feature.
   * @param highLabel Description for high scores of this feature.
   * @param info Description of what this feature is.
   */
  public Feature(String name, String lowLabel, String highLabel, String info) {
    this.name = name;
    this.info = info;
    this.userScore = ST_UNKNOWN;
    this.score = ST_UNKNOWN;
    this.lowLabel = lowLabel;
    this.highLabel = highLabel;
  }

  /**
   * Copy constructor.
   * @param feat Feature object to copy.
   */
  public Feature(Feature feat) {
    this.name = feat.name;
    this.info = feat.info;
    this.score = feat.score;
    this.userScore = feat.userScore;
    this.reliability = feat.reliability;
    this.lowLabel = feat.lowLabel;
    this.highLabel = feat.highLabel;
  }

  /**
   * Gets the name of the feature.
   * @return Returns name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets info about the feature.
   * @return Returns info.
   */
  public String getInfo() {
    return info;
  }

  /**
   * Gets the current score.
   * @return Returns score.
   */
  public int getScore() {
    return score;
  }

  /**
   * Gets the current user score.
   * @return Returns score.
   */
  public int getUserScore() {
    return userScore;
  }

  /**
   * Gets the current update award for this feature.
   * @return Returns award.
   */
  public int getAward() {
    return award;
  }

  /**
   * Gets the current reliability for this feature.
   * @return Returns reliability.
   */
  public int getReliability() {
    return reliability;
  }

  /**
   * Returns the label for the "low" end of the scale.
   * @return Low label.
   */
  public String getLowLabel() {
    return lowLabel;
  }

  /**
   * Returns the label for the "high" end of the scale.
   * @return High label.
   */
  public String getHighLabel() {
    return highLabel;
  }

  /**
   * Sets the current user score.
   * @param userScore New score for user.
   */
  public void setUserScore(int userScore) {
    this.userScore = userScore;
  }


  private String              name;       
  private String              info;
  private int                 userScore;
  private int                 score;
  private int                 award;
  private int                 reliability;
  private String              lowLabel;
  private String              highLabel;
}

