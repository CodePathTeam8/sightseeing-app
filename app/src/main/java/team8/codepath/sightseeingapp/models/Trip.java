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
public class Trip {


    public int id;
    public String name;
    public long totalLength;
    public String bannerPhoto;
    public ArrayList<Place> places;
    public static JSONArray jsonArray;
    public String distance;

    public Trip(){}

    public String getBannerPhoto() {
        return bannerPhoto;
    }

    public int getId() {
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


    public ArrayList<Place> getPlaces() {
        return places;
    }

    // Output list of tweets from jsonarray.
    //public static ArrayList<Place> fromJSONArray(JSONArray jsonArray){

    public static ArrayList<Trip> fromJSONArray(){
        // Mock Data
        jsonArray = new JSONArray();
        try{
            jsonArray.put(new JSONObject("{\"id\":\"1\", \"name\":\"Trip 1\", \"distance\":\"11.1 miles \", \"totalLength\": 12, \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
            jsonArray.put(new JSONObject("{\"id\":\"2\", \"name\":\"Trip 2\", \"distance\":\"6.5 miles \", \"totalLength\": 1.5, \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
            jsonArray.put(new JSONObject("{\"id\":\"3\", \"name\":\"Trip 3\", \"distance\":\"3.1 miles \", \"totalLength\": 21, \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
            jsonArray.put(new JSONObject("{\"id\":\"4\", \"name\":\"Trip 4\", \"distance\":\"2 miles \", \"totalLength\": 3, \"bannerPhoto\": \"http://www-tc.pbs.org/food/files/2012/07/History-of-Ice-Cream-1.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
        } catch (JSONException e) {
        }
        ArrayList<Trip> trips = new ArrayList<>();
        // Iterate JSON array and create tweets
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject tripJson = jsonArray.getJSONObject(i);
                Trip trip = Trip.fromJSON(tripJson);
                if (trip != null){
                    trips.add(trip);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return trips;
    }

    // Deserialize the JSON and build Tweet Objects
    // public static Place fromJSON(JSONObject jsonObject){
    public static Trip fromJSON(JSONObject jsonObject){
        Trip trip = new Trip();

        try {
            trip.id = jsonObject.getInt("id");
            trip.name = jsonObject.getString("name");
            trip.totalLength = jsonObject.getLong("totalLength");
            trip.bannerPhoto = jsonObject.getString("bannerPhoto");
            trip.distance = jsonObject.getString("distance");
            ArrayList<Place> listdata = new ArrayList<Place>();
            JSONArray jArray = jsonObject.getJSONArray("places");
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    Place p = new Place();
                    p.name = jArray.get(i).toString();
                    p.hours = "M-F 9-5";
                    listdata.add(p);
                }
            }
            trip.places = listdata;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trip;
    }




}
