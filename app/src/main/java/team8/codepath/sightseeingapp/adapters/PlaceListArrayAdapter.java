package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.classes.PhotoTask;
import team8.codepath.sightseeingapp.models.PlaceModel;

/**
 * Created by meganoneill on 8/21/16.
 */
public class PlaceListArrayAdapter extends ArrayAdapter<PlaceModel> {
    GoogleApiClient mGoogleApiClient;

    public PlaceListArrayAdapter(Context context, List<PlaceModel> places, GoogleApiClient googleApiClient) {
        super(context, android.R.layout.simple_list_item_1, places);
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PlaceModel place = this.getItem(position);

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_place_list, parent, false);
        }

        String name = place.getName();
        String placeId = place.getPlaceId();

        TextView placeName = (TextView) convertView.findViewById(R.id.tvPlaceName);
        placeName.setText(name);

        TextView placeHours = (TextView) convertView.findViewById(R.id.tvPlaceId);
        placeHours.setText(placeId);

        TextView placePosition = (TextView) convertView.findViewById(R.id.tvPosition);
        placePosition.setText(position + "");

        final ImageView ivPlacePhoto = (ImageView) convertView.findViewById(R.id.ivPlacePhoto);

        final TextView tvPlacePhotoInfo = (TextView) convertView.findViewById(R.id.tvPlacePhotoInfo);

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.



        new PhotoTask(200, 200, mGoogleApiClient) {
                @Override
                protected void onPreExecute() {
                    // Display a temporary image to show while bitmap is loading.
                    ivPlacePhoto.setImageResource(R.drawable.background_fb_btn);
                }
                @Override
                protected void onPostExecute(AttributedPhoto attributedPhoto) {
                    if (attributedPhoto != null) {
                        // Photo has been loaded, display it.
                        ivPlacePhoto.setImageBitmap(attributedPhoto.bitmap);
                        // Display the attribution as HTML content if set.
                        if (attributedPhoto.attribution == null) {
                            tvPlacePhotoInfo.setVisibility(View.GONE);
                        } else {
                            tvPlacePhotoInfo.setVisibility(View.VISIBLE);
                            tvPlacePhotoInfo.setText(Html.fromHtml(attributedPhoto.bitmap.toString()));
                        }
                    }
                }
        }.execute(placeId);

        return convertView;
    }
}
