package team8.codepath.sightseeingapp.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floko_000 on 8/18/2016.
 */
@Parcel
public class TripModel {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("trips");

    public String id;
    public String name;
    public int totalLength;
    public String bannerPhoto;
    public String placeId;
    public String humanReadableTripLength ="";
    public ArrayList<String> places;
    public String distance;
    public List<String> tripTags;

    public TripModel(){}

    public TripModel(String id, String name, String placeId, int totalLength, String bannerPhoto, ArrayList<String> places, List<String> tripTags){
        this.id = id;
        this.name = name;
        this.totalLength = totalLength;
        this.bannerPhoto = bannerPhoto;
        this.placeId = placeId;
        this.tripTags = tripTags;
        if (places != null)
        this.places = places;
    }



    public String getPlaceId() {
        return placeId;
    }

    public List<String> getTripTags() {
        return tripTags;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getTotalLength() { return totalLength; }

    public String getHumanReadableTotalLength() {
        // If it's discrete # of days
        humanReadableTripLength = "";
        if (totalLength >= 24 && totalLength % 24 == 0){
           humanReadableTripLength = (totalLength / 24 + " days");
        }
        else {
            int daysCount = totalLength / 24;
            int hoursCount = ((daysCount * 24) - totalLength);
            if (hoursCount < 0 )
                hoursCount = hoursCount * -1;
            if (daysCount > 0){
                humanReadableTripLength += daysCount + " days ";
            }
            humanReadableTripLength += hoursCount + " hours";
        }
        return humanReadableTripLength;
    }

    public String getDistance() {
        return distance;
    }

    public ArrayList<String> getPlaces() {
        return places;
    }


    @Exclude
    public Map<String, Object> toMap(String key) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("totalLength", totalLength);
        result.put("bannerPhoto", bannerPhoto);
        result.put("placeId", placeId);
        result.put("id", key);
        result.put("tripTags", tripTags);
        result.put("places", places);
        return result;
    }

}
