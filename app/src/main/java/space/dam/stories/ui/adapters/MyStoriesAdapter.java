package space.dam.stories.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import space.dam.stories.R;
import space.dam.stories.models.Story;
import space.dam.stories.ui.screens.story.StoryActivity;
import space.dam.stories.utils.StoryUtils;

public class MyStoriesAdapter extends RecyclerView.Adapter<MyStoriesAdapter.ViewHolder> {

    private List<Story> stories = new ArrayList<>();

    public interface MenuListener {
        void onItemClick(String storyUid, int itemId);
    }

    private MenuListener menuListener;

    public void setStories(List<Story> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_story_item, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.uid = story.getUid();

        holder.title.setText(story.getTitle());
        holder.description.setText(story.getDescription());

        Context context = holder.storyType.getContext();

        StoryUtils.setStoryTypeStyle(context, holder.storyType, story.getType());

        String formatedCreationDate = StoryUtils.getFormatedCreationDate(story.getCreationDate());
        holder.creationDate.setText(formatedCreationDate);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        private String uid;

        private ImageView buttonSettings;

        private TextView title;
        private TextView description;
        private TextView storyType;
        private TextView creationDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            buttonSettings = itemView.findViewById(R.id.buttonStorySettings);
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStorySettings(v);
                }
            });

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            storyType = itemView.findViewById(R.id.storyType);
            creationDate = itemView.findViewById(R.id.creationDate);

            itemView.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        openStory(v, position);
                    }
                }
            });
        }

        private void openStory(View v, int position) {
            Context context = v.getContext();
            Intent intent = new Intent(context, StoryActivity.class);
            intent.putExtra(StoryActivity.EXTRA_UID, stories.get(position).getUid());
            context.startActivity(intent);
        }

        private void showStorySettings(View v) {
            PopupMenu menu = new PopupMenu(v.getContext(), v, Gravity.END);
            menu.inflate(R.menu.story_settings_menu);
            menu.setOnMenuItemClickListener(this);
            menu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (menuListener != null) menuListener.onItemClick(uid, item.getItemId());
            return true;
        }
    }

    public void setMenuListener(MenuListener listener) {
        menuListener = listener;
    }

}
