package space.dam.stories.ui.screens.subscribtions;


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
import space.dam.stories.models.User;
import space.dam.stories.ui.adapters.SubscriptionsAdapter;
import space.dam.stories.utils.ViewUtils;
import space.dam.stories.viewmodels.SubscriptionsViewModel;

/**
 * Список подписок
 */
public class SubscriptionsFragment extends Fragment {

    /**
     * View model
     */
    private SubscriptionsViewModel viewModel;

    /**
     * Views
     */
    private RecyclerView subscriptions;
    private SubscriptionsAdapter adapter;
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

    public SubscriptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        initAllViews(v);
        initViewModel();
        return v;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (viewModel== null) return;
        int status = viewModel.getStatus().getValue();
        if (!hidden &&
                status != SubscriptionsViewModel.STATUS_LOADED &&
                status != SubscriptionsViewModel.STATUS_NO_SUBSCRIPTIONS) viewModel.refresh();
    }

    private void initAllViews(View v) {
        subscriptions = v.findViewById(R.id.subscriptions);
        adapter = new SubscriptionsAdapter();
        subscriptions.setAdapter(adapter);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        appBarLayout = v.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);

        textNoSubscriptions = v.findViewById(R.id.textNoSubscriptions);
    }

    private void initViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(SubscriptionsViewModel.class);
        viewModel.init();
        viewModel.getSubscriptions().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null) adapter.setSubscriptions(users);
            }
        });
        viewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status == SubscriptionsViewModel.STATUS_LOADING) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (status == SubscriptionsViewModel.STATUS_ERROR) {
                    ViewUtils.showToast(getContext(), R.string.error);
                    return;
                }

                if (status == SubscriptionsViewModel.STATUS_NO_SUBSCRIPTIONS) {
                    textNoSubscriptions.setVisibility(View.VISIBLE);
                }
                else textNoSubscriptions.setVisibility(View.INVISIBLE);
            }
        });
    }

}