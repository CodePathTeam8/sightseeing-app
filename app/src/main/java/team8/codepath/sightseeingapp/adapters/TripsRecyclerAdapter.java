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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.TripDetailsActivity;
import team8.codepath.sightseeingapp.models.TripModel;

/**
 * Created by floko_000 on 8/18/2016.
 */
public class TripsRecyclerAdapter extends FirebaseRecyclerAdapter<TripModel,
        TripsRecyclerAdapter.ViewHolder> {

    private Context mContext;

    public TripsRecyclerAdapter(int modelLayout, DatabaseReference ref) {
        super(TripModel.class, modelLayout, TripsRecyclerAdapter.ViewHolder.class, ref);
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

    public View getView(int position, View convertView, ViewGroup parent) {

        TripModel trip = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_trip, parent, false);
        }

        // Get Subviews
        ImageView ivTripBanner = (ImageView) convertView.findViewById(R.id.ivTripBanner);
        TextView tvTripName = (TextView) convertView.findViewById(R.id.tvTripName);
        TextView tvTripDistance = (TextView) convertView.findViewById(R.id.tvTripDistance);
        TextView tvTripLength = (TextView) convertView.findViewById(R.id.tvTripLength);

        // Populate subviews
        ivTripBanner.setImageResource(android.R.color.transparent); // clear out old image for recycled view
        Picasso.with(getContext()).load(trip.getBannerPhoto()).into(ivTripBanner);
        tvTripName.setText(trip.getName());
        tvTripDistance.setText(trip.getDistance());
        tvTripLength.setText(trip.getTotalLength());


        // 5. Return the view to be inserted into the list
        return convertView;

    }

    @Override
    public void onBindViewHolder(TripsRecyclerAdapter.ViewHolder viewHolder, final int position) {
        final TripModel trip = getItem(position);

        ImageView bannerView = viewHolder.banner;

        // Populate subviews
        bannerView.setImageResource(android.R.color.transparent); // clear out old image for recycled view
        Picasso.with(getContext()).load(trip.getBannerPhoto()).into(bannerView);
        viewHolder.name.setText(trip.getName());
        viewHolder.distance.setText(trip.getDistance());
        viewHolder.length.setText(trip.getTotalLength());
        viewHolder.cvTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TripDetailsActivity.class);

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

}
