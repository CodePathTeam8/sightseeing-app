package team8.codepath.sightseeingapp.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.UUID;

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
        addData();
    }

    public PlaceModel(String name, String placeId){
        this.name = name;
        this.placeId = placeId;
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


    // Mock Data
    public void addData(){
        PlaceModel place = new PlaceModel("Ample Hills Creamery", "ChIJ9QuctVVawokR2xUuRAu-bs4");
        PlaceModel place_two = new PlaceModel("Emack & Bolio's", "ChIJ9aeRDrlYwokR0OXIVGvP_sg");
        mDatabase.child("1").child("1").setValue(place);
        mDatabase.child("1").child("2").setValue(place_two);
    }


    // Output list of tweets from jsonarray.
    //public static ArrayList<PlaceModel> fromJSONArray(JSONArray jsonArray){

    public static ArrayList<PlaceModel> fromJSONArray(){
        ArrayList<PlaceModel> places = new ArrayList<>();
        // Iterate JSON array and create tweets
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject placeJson = jsonArray.getJSONObject(i);
                PlaceModel place = PlaceModel.fromJSON(placeJson);
                if (place != null){
                    places.add(place);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return places;
    }

    // Deserialize the JSON and build PlaceModel Objects
    // public static PlaceModel fromJSON(JSONObject jsonObject){
    public static PlaceModel fromJSON(JSONObject jsonObject){
        PlaceModel place = new PlaceModel();
            String uuid = UUID.randomUUID().toString();

        try {
            place.id = jsonObject.getInt("id");
            place.name = jsonObject.getString("name");
            place.averageStay = jsonObject.getInt("averageStay");
            place.hours = jsonObject.getString("hours");
            place.placeId = jsonObject.getString("place_id");
            mDatabase.child(uuid).setValue(place);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }




}
