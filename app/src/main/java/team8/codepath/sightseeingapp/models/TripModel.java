package team8.codepath.sightseeingapp.models;

import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by floko_000 on 8/18/2016.
 */
@Parcel
public class TripModel {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("trips");

    public String id;
    public String name;
    public String totalLength;
    public String bannerPhoto;
    public String placeId;
    public ArrayList<String> places;
    public static JSONArray jsonArray;
    public String distance;

    public TripModel(){}

    public TripModel(String id, String name, String placeId, String totalLength, String bannerPhoto, ArrayList<String> places){
        this.id = id;
        this.name = name;
        this.totalLength = totalLength;
        this.bannerPhoto = bannerPhoto;
        this.placeId = placeId;
        if (places != null)
        this.places = places;
    }

    public String getBannerPhoto() {
        return bannerPhoto;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTotalLength() {
        return totalLength+"";
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
        result.put("places", places);
        return result;
    }

}
