package team8.codepath.sightseeingapp.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by floko_000 on 8/18/2016.
 */
public class Place {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("places");

    public int id;
    public String name;
    public int averageStay;
    public String bannerPhoto;
    public String hours;
    public static JSONArray jsonArray;
    public String placeId;

    public Place(){
        addData();
    }

    public Place(String name, String placeId){
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

    public String getHours() {
        return hours;
    }
    public String getBannerPhoto() {
        return bannerPhoto;
    }


    // Mock Data
    public void addData(){
//        Place place = new Place("Ample Hills Creamery", "ChIJ9QuctVVawokR2xUuRAu-bs4");
//        Place place_two = new Place("Emack & Bolio's", "ChIJ9aeRDrlYwokR0OXIVGvP_sg");
//        mDatabase.child("1").setValue(place);
//        mDatabase.child("2").setValue(place_two);
    }


    // Output list of tweets from jsonarray.
    //public static ArrayList<Place> fromJSONArray(JSONArray jsonArray){

    public static ArrayList<Place> fromJSONArray(){
        ArrayList<Place> places = new ArrayList<>();
        // Iterate JSON array and create tweets
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject placeJson = jsonArray.getJSONObject(i);
                Place place = Place.fromJSON(placeJson);
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

    // Deserialize the JSON and build Tweet Objects
    // public static Place fromJSON(JSONObject jsonObject){
    public static Place fromJSON(JSONObject jsonObject){
        Place place = new Place();
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
