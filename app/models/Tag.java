package models;

import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

import com.avaje.ebean.Expr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.Application;

import java.util.List;
import java.util.ArrayList;


/**
 * Defines a feature of a location.
 */
@Entity
public class Tag extends Model implements UpdateInterface {

  public final static String PARKING = "Parking";
  public final static String RESTROOMS = "Restrooms";
  public final static String LIFEGUARD = "Lifeguard";
  public final static String DOGS = "Dogs Allowed";
  public final static String BOATRAMP = "Boat Ramp";
  public final static String PUBLICTRANS = "Public Transportation";
  public final static String CAMPING = "Camping";
  public final static String SHOWERS = "Showers";


  public final static int UNKNOWN = 0;
  public final static int NO = 1;
  public final static int YES = 2;

  // @Column(columnDefinition = "TEXT")
  // make a string the text type which is unlimited in size (supposedly)
  //
  @Id
  private long    id;

  private String  name;
  private String  info;
  private int     yes;
  private int     no;
  private int     accuracy;

  @ManyToOne
  Location        location;


  /**
   * Constructs a new feature.
   * @param name      Name of feature.
   * @param info      Description of what this feature is.
   */
  public Tag(String name, String info) {
    this.name = name;
    this.info = info;
    this.yes = 0;
    this.no = 0;
    this.accuracy = 0;
  }

  /**
   * Query routine for Ebeans.
   * @return Returns a Finder object for the query.
   */
  public static Finder<Long, Tag> find() {
    return new Finder<Long, Tag>(Long.class, Tag.class);
  }
  
  /**
   * Gets ID.
   * @return Returns ID.
   */
  public long getId() {
    return id;
  }

  /**
   * Sets ID.
   * @param id Value to set ID to.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the name of the feature.
   * @return Returns name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name of Tag.
   * @param name Name to use.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets info about the feature.
   * @return Returns info.
   */
  public String getInfo() {
    return info;
  }

  /**
   * Sets the info text for the tag.
   * @param info Info string.
   */
  public void setInfo(String info) {
    this.info = info;
  }

  /**
   * Gets the current yes count.
   * @return Returns yes count.
   */
  public int getYes() {
    return yes;
  }

  /**
   * Sets the yes value.
   * @param yes New "yes" count.
   */
  public void setYes(int yes) {
    this.yes = yes;
  }

  /**
   * Get no count.
   * return No count.
   */
  public int getNo() {
    return no;
  }

  /**
   * Sets no count.
   * @param no No count.
   */
  public void setNo(int no) {
    this.no = no;
  }

  /**
   * Gets location this tag is associated with.
   * @return Location.
   */
  public Location getLocation() {
    return location;
    }

  /**
   * Sets location associated with this tag.
   * @param location New location to associate with.
   */
  public void setLocation(Location location) {
    this.location = location;
  }

  /**
   * Gets the current accuracy for this feature.
   * @return Returns accuracy.
   */
  public int getAccuracy() {
    return accuracy;
  }

  /**
   * Sets accuracy value.
   * @param accuracy New accuracy value.
   */
  public void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }

  /**
   * Collect data relevant to the associated object for the client.
   * @param data Json object to populate.
   */
  public void fetchUpdate(ObjectNode data) {
    Account     account = Application.getCurrentAccount();
    UserUpdate  uu;
    int         userScore = 0;
    int         score;

    if (account != null) {
      // Need to find a user update from this user, for this feature, and
      // a FEATURE type.
      uu = UserUpdate.find().where().and( 
          Expr.eq("account.id", account.getId()), Expr.and(
            Expr.eq("parentId", id), Expr.eq("type", UserUpdate.TAG)
            )
          ).findUnique();
      
      if (uu != null) {
        userScore = uu.getScore();
      }
    }
    
    data.put("score", Integer.toString(getValue()));
    data.put("userScore", Integer.toString(userScore));
    data.put("accuracy", Integer.toString(accuracy));
    data.put("scoreList", new String(Integer.toString(no) + ";" + Integer.toString(yes)));
  }

  /**
   * Gets an enumerated value of the tag based on the yes/no counts.
   * @return Returns YES, NO, or UNKNOWN based on counts.
   */
  public int getValue() {
    int st;

    if (yes > no) {
      st = YES;
    }
    else if (no > yes) {
      st = NO;
    }
    else {
      st = UNKNOWN;
    }

    return st;
  }
   

  /**
   * Updates a tag with a yes/no score from a specific user.
   * @param account User updating the tag.
   * @param score Score to update tag with.
   */
  public void update(Account account, int score) {
    UserUpdate  uu;

    // Find user updates where user matches and parentId of uu matches this tag.
    uu = UserUpdate.find().where().and( Expr.eq("account.id", account.getId()), 
        Expr.and(Expr.eq("parentId", id), Expr.eq("type", UserUpdate.TAG)) 
        ).findUnique();

    if (uu != null) {
      if (uu.getScore() == Tag.YES) {
        yes -= 1;
      }
      else {
        no -= 1;
      }

      uu.setScore(score);
    }
    else {
      uu = new UserUpdate(account, UserUpdate.TAG, id, score);
      account.addUpdate(uu);
    }

    if (score == Tag.YES) {
      yes += 1;
    }
    else {
      no += 1;
    }

    calcReliability();
    save();
    uu.save();
    account.save();
  }


  /**
   * Calculates the accuracy of the tag.
   */
  private void calcReliability() {
    float percent;

    if (yes + no == 0 || yes == no) {
      accuracy = 0;
    } 
    else {
      float n = (float)yes;
  
      if (yes < no) {
        n = (float)no;
      }

      percent = n/(float)(yes + no);
      if (percent < 0.60f) accuracy = 1;
      else if (percent < 0.70f) accuracy = 2;
      else if (percent < 0.80f) accuracy = 3;
      else if (percent < 0.90f) accuracy = 4;
      else if (percent < 0.95f) accuracy = 5;
      else accuracy = 6;
    }
  }

}


