package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;


import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.models.PlaceModel;


public class PlacesRecyclerAdapter extends FirebaseRecyclerAdapter<PlaceModel,
        PlacesRecyclerAdapter.ItemViewHolder> {

    Context context;

    public PlacesRecyclerAdapter(int modelLayout, DatabaseReference ref) {
        super(PlaceModel.class, modelLayout, PlacesRecyclerAdapter.ItemViewHolder.class, ref);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
        context = parent.getContext();
        return new ItemViewHolder(view);
    }

    @Override
    protected void populateViewHolder(ItemViewHolder holder, PlaceModel item, int position) {
        String itemDescription = item.getName();
        holder.tvPlaceName.setText(itemDescription);
        //holder.cvCategory.setBackgroundResource(getResId(item.getImage(), R.drawable.class));
    }


    class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            View.OnLongClickListener {

        TextView tvPlaceName;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);

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
}
