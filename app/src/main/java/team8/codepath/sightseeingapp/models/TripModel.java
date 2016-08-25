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
public class TripModel {


    public int id;
    public String name;
    public long totalLength;
    public String bannerPhoto;
    public ArrayList<PlaceModel> places;
    public static JSONArray jsonArray;
    public String distance;

    public TripModel(){}

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


    public ArrayList<PlaceModel> getPlaces() {
        return places;
    }

    // Output list of tweets from jsonarray.
    //public static ArrayList<PlaceModel> fromJSONArray(JSONArray jsonArray){

    public static ArrayList<TripModel> fromJSONArray(){
        // Mock Data
        jsonArray = new JSONArray();
        try{
            jsonArray.put(new JSONObject("{\"id\":\"1\", \"name\":\"Best of Manhattan Ice Cream\", \"distance\":\"11.1 miles \", \"totalLength\": 12, \"bannerPhoto\": \"http://feliciarogersauthor.weebly.com/uploads/1/2/6/7/12672742/9911388_orig.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
            jsonArray.put(new JSONObject("{\"id\":\"2\", \"name\":\"Staten Island Treats\", \"distance\":\"6.5 miles \", \"totalLength\": 1.5, \"bannerPhoto\": \"http://67.media.tumblr.com/560d23f751255935a78173eb25941a5e/tumblr_nrp7ewTL1Z1ttdrv5o1_1280.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
            jsonArray.put(new JSONObject("{\"id\":\"3\", \"name\":\"Alphabet City Sweets\", \"distance\":\"3.1 miles \", \"totalLength\": 21, \"bannerPhoto\": \"http://www.billsseafood.com/wp-content/uploads/2015/04/Bills-Seafood-Restaurant-Westbrook-CT-Ice-Cream-Shop-and-Gift-Shop.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
            jsonArray.put(new JSONObject("{\"id\":\"4\", \"name\":\"Hell's Kitchen Delights\", \"distance\":\"2 miles \", \"totalLength\": 3, \"bannerPhoto\": \"http://www.cafeinteriordesign.com/gallery/ice-cream-shop/ice-cream-shop-20.jpg\", \"places\": ['Ice Cream Palace', 'Curlys', 'Ice Cream World']}"));
        } catch (JSONException e) {
        }
        ArrayList<TripModel> trips = new ArrayList<>();
        // Iterate JSON array and create tweets
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject tripJson = jsonArray.getJSONObject(i);
                TripModel trip = TripModel.fromJSON(tripJson);
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
    // public static PlaceModel fromJSON(JSONObject jsonObject){
    public static TripModel fromJSON(JSONObject jsonObject){
        TripModel trip = new TripModel();

        try {
            trip.id = jsonObject.getInt("id");
            trip.name = jsonObject.getString("name");
            trip.totalLength = jsonObject.getLong("totalLength");
            trip.bannerPhoto = jsonObject.getString("bannerPhoto");
            trip.distance = jsonObject.getString("distance");
            ArrayList<PlaceModel> listdata = new ArrayList<PlaceModel>();
            JSONArray jArray = jsonObject.getJSONArray("places");
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    PlaceModel p = new PlaceModel();
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
