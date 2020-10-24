package space.dam.stories.ui.screens.stories;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import space.dam.stories.R;
import space.dam.stories.models.Story;
import space.dam.stories.ui.adapters.StoriesAdapter;
import space.dam.stories.utils.ViewUtils;
import space.dam.stories.viewmodels.StoriesViewModel;

/**
 * Фрагмент с историями людей, на которых подписан пользователь
 */
public class StoriesFragment extends Fragment {

    /**
     * ViewModel
     */
    private StoriesViewModel viewModel;

    /**
     * Views
     */
    private RecyclerView stories;
    private StoriesAdapter adapter;
    private AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            swipeRefreshLayout.setEnabled(verticalOffset == 0);
        }
    };
    private TextView textNoSubscriptions;

    /**
     * Swipe to refresh layout
     */
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            viewModel.refresh();
        }
    };

    public StoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stories, container, false);
        initAllViews(v);
        initViewModel();
        return v;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (viewModel == null) return;
        int status = viewModel.getStatus().getValue();
        if (!hidden &&
                status != StoriesViewModel.STATUS_LOADED &&
                status != StoriesViewModel.STATUS_NO_SUBSCRIPTIONS) viewModel.loadStories();
    }

    private void initAllViews(View v) {
        stories = v.findViewById(R.id.stories);
        adapter = new StoriesAdapter();
        stories.setAdapter(adapter);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        appBarLayout = v.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);

        textNoSubscriptions = v.findViewById(R.id.textNoSubscriptions);
    }

    private void initViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(StoriesViewModel.class);
        viewModel.init();
        viewModel.getStories().observe(getViewLifecycleOwner(), new Observer<List<Story>>() {
            @Override
            public void onChanged(List<Story> stories) {
                adapter.setStories(stories);
            }
        });
        viewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status == StoriesViewModel.STATUS_LOADING) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (status == StoriesViewModel.STATUS_ERROR) {
                    ViewUtils.showToast(getActivity(), R.string.error);
                    return;
                }

                if (status == StoriesViewModel.STATUS_NO_SUBSCRIPTIONS) {
                    textNoSubscriptions.setVisibility(View.VISIBLE);
                }
                else textNoSubscriptions.setVisibility(View.INVISIBLE);
            }
        });
    }
}
