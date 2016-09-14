package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.TripDetailActivity;
import team8.codepath.sightseeingapp.classes.PhotoTask;
import team8.codepath.sightseeingapp.models.TripModel;

/**
 * Created by meganoneill on 9/5/16.
 */
public class SearchRecyclerAdapter extends
        RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {
    private List<TripModel> trips;
    private Context context;
    GoogleApiClient mGoogleApiClient;

    public SearchRecyclerAdapter(Context applicationContext, ArrayList<TripModel> trips, GoogleApiClient googleApiClient){
        this.trips = trips;
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_trip, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final TripModel trip = trips.get(position);

        final ImageView bannerView = viewHolder.banner;

        // Populate subviews
        bannerView.setImageResource(android.R.color.transparent); // clear out old image for recycled view
        // Picasso.with(getContext()).load(trip.getBannerPhoto()).into(bannerView);
//        loadPlaceImage(trip.getPlaceId());

        String PhotoPlaceId = trip.getPlaceId();

        if(PhotoPlaceId == null){
            Log.d("debug", "hi");
        }

        new PhotoTask(200, 200, mGoogleApiClient) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                //ivPlacePhoto.setImageResource(R.drawable.background_fb_btn);
            }
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    bannerView.setImageBitmap(attributedPhoto.bitmap);

                }
            }
        }.execute(PhotoPlaceId);

        viewHolder.name.setText(trip.getName());
        //viewHolder.distance.setText(trip.getDistance());
        viewHolder.length.setText("Length: " + trip.getHumanReadableTotalLength());
        viewHolder.cvTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetailActivity.class);

                intent.putExtra("trip", Parcels.wrap(trip));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView banner;
        public TextView name;
        public TextView distance;
        public TextView length;
        public CardView cvTrip;

        public ViewHolder(View itemView) {
            super(itemView);
            banner = (ImageView) itemView.findViewById(R.id.ivTripBanner);
            name = (TextView) itemView.findViewById(R.id.tvTripName);
            distance = (TextView) itemView.findViewById(R.id.tvTripDistance);
            length = (TextView) itemView.findViewById(R.id.tvTripLength);
            cvTrip = (CardView) itemView.findViewById(R.id.cvTrip);
        }

    }
}
