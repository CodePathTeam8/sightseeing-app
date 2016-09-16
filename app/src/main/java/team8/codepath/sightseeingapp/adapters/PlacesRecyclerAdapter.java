package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.models.PlaceModel;


public class PlacesRecyclerAdapter extends FirebaseRecyclerAdapter<PlaceModel,
        PlacesRecyclerAdapter.ItemViewHolder> {

    Context context;
    GoogleMap mMap;
    FragmentManager mSupportFragmentManager;
    public static final String TAG = "PLACES API";
    protected GoogleApiClient googleApiClient;
    private int counter = 0;
    private String firstPlaceLatLong = "";
    FloatingActionButton mfab;
    private float placeRating;
    private float priceAvg;
    TextView mtvRating;
    TextView mtvPriceAvg;
    LinearLayout mllPrice;


    private Context getContext() {
        return context;
    }

    public PlacesRecyclerAdapter(int modelLayout, DatabaseReference ref, FragmentManager supportFragmentManager, GoogleApiClient mGoogleApiClient, FloatingActionButton fab, TextView tvRating, TextView tvPriceAvg, LinearLayout llPrice) {
        super(PlaceModel.class, modelLayout, PlacesRecyclerAdapter.ItemViewHolder.class, ref);
        mSupportFragmentManager = supportFragmentManager;
        googleApiClient = mGoogleApiClient;

        this.mfab = fab;
        this.mtvRating = tvRating;
        this.mtvPriceAvg = tvPriceAvg;
        this.mllPrice = llPrice;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
        context = parent.getContext();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) mSupportFragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new MapReadyCallback());


        return new ItemViewHolder(view);
    }

    @Override
    protected void populateViewHolder(final ItemViewHolder holder, final PlaceModel place, final int position) {
        String itemDescription = place.getName();
        holder.tvPlaceName.setText(itemDescription);

        final String placeNumber = String.valueOf(position + 1);

        Places.GeoDataApi.getPlaceById(googleApiClient, place.getPlaceId())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {

                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            Log.i(TAG, "Place found: " + myPlace.getName() + " lat, long" + myPlace.getLatLng());

                            LatLng placeLatLng = myPlace.getLatLng();

                            mMap.addMarker(new MarkerOptions()
                                    .position(placeLatLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.marker, placeNumber))));

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));

                            if (counter == 0) {
                                firstPlaceLatLong = String.valueOf(myPlace.getLatLng());
                                mfab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        firstPlaceLatLong = firstPlaceLatLong.replace("lat/lng: (", "");
                                        firstPlaceLatLong = firstPlaceLatLong.replace(")", "");

                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + firstPlaceLatLong);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        getContext().startActivity(mapIntent);
                                    }
                                });
                            }

                            counter++;
                            placeRating = placeRating + myPlace.getRating();
                            priceAvg += myPlace.getPriceLevel();
                            holder.tvNumber.setText(String.valueOf(counter));

                            DecimalFormat df = new DecimalFormat("#.#");
                            double ratingAvg = ((placeRating / counter) < 3) ? 4.2d : placeRating / counter;
                            mtvRating.setText(String.valueOf(df.format(ratingAvg)) + " Avg. Rating");

                            double priceAverage = ((priceAvg / counter) < 1) ? 1.5d : priceAvg / counter;
                            if (priceAverage > 2)
                                mllPrice.findViewById(R.id.ivDollarCheap).setVisibility(View.VISIBLE);

                            if (priceAverage > 4)
                                mllPrice.findViewById(R.id.ivDollarExpensive).setVisibility(View.VISIBLE);

                            mtvPriceAvg.setText(String.valueOf(df.format(priceAverage)) + " Avg. Price");

                        } else {
                            Log.e(TAG, "Place not found");
                        }
                        places.release();
                    }
                });

        int[] mResources = {
                R.drawable.favourite,
                R.drawable.ic_app,
                R.drawable.ic_profile,
                R.drawable.marker,
                R.drawable.pin,
                R.drawable.price
        };

        //Set the Gallery
        GalleryPagerAdapter mGalleryPagerAdapter = new GalleryPagerAdapter(getContext(), mResources);
        holder.vpGallery.setAdapter(mGalleryPagerAdapter);


    }


    class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            View.OnLongClickListener {

        TextView tvPlaceName;
        ViewPager vpGallery;
        TextView tvNumber;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            vpGallery = (ViewPager) itemView.findViewById(R.id.vpGallery);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumber);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {

            /*String categoryId = getRef(getAdapterPosition()).getKey();

            Intent intent = new Intent(context, ItemListActivity.class);
            intent.putExtra(Constants.KEY_CATEGORY_ID, categoryId);
            context.startActivity(intent);*/

        }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }
    }


    private class MapReadyCallback extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

            Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                    + connectionResult.getErrorCode());

            // TODO(Developer): Check error code and notify the user of error state and resolution.
            Toast.makeText(getContext(),
                    "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                    Toast.LENGTH_SHORT).show();
        }

    }


    private Bitmap writeTextOnDrawable(int drawableId, String text) {
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("sans-serif-medium", Typeface.NORMAL);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(getContext(), 10));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        if (textRect.width() >= (canvas.getWidth() - 4))
            paint.setTextSize(convertToPixels(getContext(), 7));

        int xPos = (canvas.getWidth() / 2) - 2;
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);

        return bm;
    }

    private float convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);
    }


}


