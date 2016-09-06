package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.TripDetailActivity;
import team8.codepath.sightseeingapp.activities.TripDetailsActivity;
import team8.codepath.sightseeingapp.activities.TripListActivity;
import team8.codepath.sightseeingapp.classes.PhotoTask;
import team8.codepath.sightseeingapp.models.TripModel;

/**
 * Created by floko_000 on 8/18/2016.
 */
public class TripsRecyclerAdapter extends FirebaseRecyclerAdapter<TripModel,
        TripsRecyclerAdapter.ViewHolder> implements GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    GoogleApiClient mGoogleApiClient;

    public TripsRecyclerAdapter(int modelLayout, DatabaseReference ref,  GoogleApiClient googleApiClient) {
        super(TripModel.class, modelLayout, TripsRecyclerAdapter.ViewHolder.class, ref);
        mGoogleApiClient = googleApiClient;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public TripsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);



        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_trip, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, TripModel model, int position) {

    }

    @Override
    public void onBindViewHolder(TripsRecyclerAdapter.ViewHolder viewHolder, final int position) {
        final TripModel trip = getItem(position);

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
                Intent intent = new Intent(getContext(), TripDetailActivity.class);

                intent.putExtra("trip", Parcels.wrap(trip));
                getContext().startActivity(intent);
            }
        });
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




}
