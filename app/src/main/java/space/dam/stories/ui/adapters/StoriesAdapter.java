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
import space.dam.stories.models.Story;
import space.dam.stories.ui.screens.story.StoryActivity;
import space.dam.stories.utils.StoryUtils;
import space.dam.stories.utils.ViewUtils;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private List<Story> stories = new ArrayList<>();

    public void setStories(List<Story> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    public Story getStory(int position) {
        return stories.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_item, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = stories.get(position);

        holder.creatorName.setText(story.getCreatorName());

        holder.title.setText(story.getTitle());
        holder.description.setText(story.getDescription());

        Context context = holder.storyType.getContext();

        StoryUtils.setStoryTypeStyle(context, holder.storyType, story.getType());

        String formatedCreationDate = StoryUtils.getFormatedCreationDate(story.getCreationDate());
        holder.creationDate.setText(formatedCreationDate);

        ViewUtils.updateProfilePhoto(story.getCreatorPhoto(), holder.creatorPhoto);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView creatorPhoto;
        private TextView creatorName;

        private TextView title;
        private TextView description;
        private TextView storyType;
        private TextView creationDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            creatorPhoto = itemView.findViewById(R.id.creatorPhoto);
            creatorName = itemView.findViewById(R.id.creatorName);

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
    }


}
