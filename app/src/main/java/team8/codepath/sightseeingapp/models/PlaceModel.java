package team8.codepath.sightseeingapp.models;

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
import java.util.UUID;

import team8.codepath.sightseeingapp.classes.PhotoTask;

/**
 * Created by floko_000 on 8/18/2016.
 */
@Parcel
public class PlaceModel {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("places");


    public int id;
    public String name;
    public int averageStay;
    public String bannerPhoto;
    public String hours;
    public int order;
    public static JSONArray jsonArray;
    public String placeId;

    public PlaceModel(){
    }

    public PlaceModel(String name, String placeId){
        this.name = name;
        this.placeId = placeId;
        this.bannerPhoto = bannerPhoto;
    }
    public String getName() {
        return name;
    }

    public String getPlaceId() {return placeId;}
    public int getAverageStay() {
        return averageStay;
    }
    public int getPlaceOrder() {
        return order;
    }
    public String getHours() {
        return hours;
    }
    public String getBannerPhoto() {
        return bannerPhoto;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("id", id);
        result.put("order", order);
        result.put("placeId", placeId);
        result.put("bannerPhoto", bannerPhoto);
        return result;
    }



}
