package space.dam.stories.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import space.dam.stories.R;
import space.dam.stories.models.User;
import space.dam.stories.ui.screens.profile.ProfileActivity;
import space.dam.stories.utils.ViewUtils;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.ViewHolder> {

    private List<User> subscriptions = new ArrayList<>();

    public void setSubscriptions(List<User> subscriptions) {
        this.subscriptions = subscriptions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subscription_item, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = subscriptions.get(position);
        ViewUtils.updateProfilePhoto(user.getPhoto(), holder.photo);
        holder.name.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private CircleImageView photo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.itemName);
            photo = itemView.findViewById(R.id.itemPhoto);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        openUser(v, position);
                    }
                }
            });
        }

        private void openUser(View v, int position) {
            Context context = v.getContext();
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(ProfileActivity.EXTRA_UID, subscriptions.get(position).getUid());
            context.startActivity(intent);
        }
    }

}
