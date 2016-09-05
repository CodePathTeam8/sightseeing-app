package team8.codepath.sightseeingapp.models;


import org.json.JSONException;
import org.json.JSONObject;


public class UserModel {

    private String id;
    private String name;
    private String email;
    private String gender;
    private String locationId;
    private String locationName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public static UserModel fromJSON(JSONObject jsonObject){
        UserModel user = new UserModel();

        try {
            user.id = jsonObject.getString("id");
            user.name = jsonObject.getString("name");
            user.email = jsonObject.getString("email");
            user.gender = jsonObject.getString("gender");
            user.locationId = jsonObject.getJSONObject("location").getString("id");
            user.locationName = jsonObject.getJSONObject("location").getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}

