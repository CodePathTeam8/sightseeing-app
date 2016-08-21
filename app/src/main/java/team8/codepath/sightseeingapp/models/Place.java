package team8.codepath.sightseeingapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by floko_000 on 8/18/2016.
 */
@Parcel
public class Place {


    public int id;
    public String name;
    public int averageStay;
    public String bannerPhoto;
    public String hours;
    public static JSONArray jsonArray;

    public Place(){
        initializeJsonArray();
    }

    public String getName() {
        return name;
    }


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
    public static void initializeJsonArray(){
        jsonArray = new JSONArray();
        try{
            jsonArray.put(new JSONObject("{\"id\":\"1\", \"name\":\"Museum A\", \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"hours\": \"9am-10pm\"}"));
            jsonArray.put(new JSONObject("{\"id\":\"2\", \"name\":\"Ashleys Ice Cream\", \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"hours\": \"10am-1pm\"}"));
            jsonArray.put(new JSONObject("{\"id\":\"3\", \"name\":\"Yummy Ice Cream\", \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"hours\": \"9am-11pm\"}"));
            jsonArray.put(new JSONObject("{\"id\":\"4\", \"name\":\"Super Yummy Ice Cream\", \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"hours\": \"6am-10pm\"}"));
        } catch (JSONException e) {
        }
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

        try {
            place.id = jsonObject.getInt("id");
            place.name = jsonObject.getString("text");
            place.averageStay = jsonObject.getInt("averageStay");
            place.hours = jsonObject.getString("hours");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }




}
