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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAverageStay() {
        return averageStay;
    }

    public void setAverageStay(int averageStay) {
        this.averageStay = averageStay;
    }

    public String getBannerPhoto() {
        return bannerPhoto;
    }

    public void setBannerPhoto(String bannerPhoto) {
        this.bannerPhoto = bannerPhoto;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int id;
    public String name;
    public int averageStay;
    public String bannerPhoto;
    public String hours;
    public int order;
    public static JSONArray jsonArray;
    public String placeId;
    public double latitude;
    public double longitude;

    public PlaceModel(){
    }

    public PlaceModel(String name, String placeId){
        this.name = name;
        this.placeId = placeId;
        this.bannerPhoto = bannerPhoto;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("id", id);
        result.put("order", order);
        result.put("placeId", placeId);
        result.put("bannerPhoto", bannerPhoto);
        result.put("lattitude", latitude);
        result.put("longitude", longitude);
        return result;
    }



}
