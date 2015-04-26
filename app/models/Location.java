package models;

import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;


/**
 * Defines a location.
 * A location contains all the static information necessary to describe a
 * location.
 */
@Entity
public class Location extends Model {
  /**
   * Constant ID for beaches.
   */
  public static final int LOC_BEACH = 1;

  private static final String DEFAULT_SCORE_VALUES = "Poor;Marginal;Average;Good;Excellent";


  @Id
  private long    id;
  private float   latitude;
  private float   longitude;
  private String  name;
  private String  description;
  private int     type;

  @OneToMany(mappedBy="location", cascade=CascadeType.PERSIST)
  private List<Feature>    features;

  @OneToMany(mappedBy="location", cascade=CascadeType.PERSIST)
  private List<Tag>        tags;

  @OneToMany(mappedBy="location", cascade=CascadeType.PERSIST)
  private List<Photo>      photos;

  @ManyToOne
  private User              creator;


  /**
   * Constructor.
   * @param name        Name of the beach.
   * @param description Description of the beach.
   * @param lat         Geographical latitude of the beach.
   * @param lng         Geographical longitude of the beach.
   */
  public Location(String name, String description, float lat, float lng) {
    this.name = name;
    this.description = description;
    this.latitude = lat;
    this.longitude = lng;
    this.type = LOC_BEACH;

    this.features = new ArrayList<Feature>();
    this.tags = new ArrayList<Tag>();


    // Every location has the same facilities, activities, tags, etc. Create
    // them all here.
    tags.add(new Tag("Park Lot", "This beach has a parking lot facility."));
    tags.add(new Tag("Dogs", "Dogs are allowed at this beach."));
    tags.add(new Tag("Boat Ramp", "A boat ramp is available."));
    tags.add(new Tag("Life Guard", "The beach is monitored by lifeguards."));
    tags.add(new Tag("Restrooms", "Public restrooms are present."));
    tags.add(new Tag("Showers", "Public showers are available."));
    tags.add(new Tag("Camping", "Camp sites are located at or nearby."));

    features.add(new Feature("Snorkeling", "Potential quality of snorkeling.", "", "", DEFAULT_SCORE_VALUES));
    features.add(new Feature("Surfing", "Potential quality of surfing.", "", "", DEFAULT_SCORE_VALUES));
    features.add(new Feature("Fishing", "Fishing quality around this beach.", "", "", DEFAULT_SCORE_VALUES));
    features.add(new Feature("Sand", "What's the beach is like.", "Rocky", "Fine Sand", "Rocky;Rocks and Sand;Course;Normal;Fine"));
  }

  public static Finder<Long, Location> find() {
    return new Finder<Long, Location>(Long.class, Location.class);
  }

  /**
   * Gets current unique ID of location.
   *
   * @return ID.
   */
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the longitude of the location.
   *
   * @return Longitude.
   */
  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  /**
   * Returns latitude of location.
   *
   * @return Latitude.
   */
  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  /**
   * Gets name of location.
   *
   * @return Location name.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Provides description string.
   *
   * @return Description.
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public List<Photo> getPhotos() {
    return photos;
  }

  public void setPhotos(List<Photo> photos) {
    this.photos = photos;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  /**
   * Finds a feature based on its name.
   * @return Feature object.
   */
  public Feature getFeature(String name) {

    // Look for the features in the facilities category.
    for (Feature feat : features) {
      if (feat.getName().equals(name)) {
        return feat;
      }
    }

    return null;
  }


  /**
   * Returns the total award count available for the current user
   * at the given location.
   *
   * @return Award count.
   */
  public int getTotalAwards() {
    return 0;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean hasParking() {
    return getFeature("Parking Facilities").getScore() == 2;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean hasRestrooms() {
    return getFeature("Restrooms").getScore() == 2;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean hasLifeguard() {
    return getFeature("Lifeguard").getScore() == 2;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean allowsDogs() {
    return getFeature("Dogs Allowed").getScore() == 2;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean hasBoatRamp() {
    return getFeature("Boat Ramp").getScore() == 2;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean hasDining() {
    return false;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean hasPublicTrans() {
    return false;
  }

  /**
   * Determine if feature is there.
   * @return Returns true if it is, false if not.
   */
  public boolean allowsCamping() {
    return getFeature("Camping").getScore() == 2;
  }

  /**
   * Returns the most popular/favorite image for this location.
   *
   * @return Path to image.
   */
  public String getImagePathMostPopular() {
    String imgPath;
    String fileName = name.toLowerCase();

    imgPath = "/assets/images/dbimg/" + fileName.replaceAll(" ", "-") + "-1.jpg";

    return imgPath;
  }

  /**
   * Determines if a given coordinate is within a specified distance of this
   * location object.
   * <p>
   * Coordinates from Google are in decimal degrees (DD).
   *
   * @param lat latitude
   * @param lng longitude
   * @return Distance from this location to the coordinates specified.
   */
  public float getDistanceFrom(float lat, float lng) {
    return (float) Math.sqrt(Math.pow(latitude - lat, 2.0f) + Math.pow(longitude - lng, 2.0f));
  }



}
