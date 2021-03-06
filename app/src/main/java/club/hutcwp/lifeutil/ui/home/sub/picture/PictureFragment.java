package club.hutcwp.lifeutil.ui.home.sub.picture;

import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.List;

import club.hutcwp.lifeutil.R;
import club.hutcwp.lifeutil.adpter.PhotoAdapter;
import club.hutcwp.lifeutil.databinding.FragmentGankGirlBinding;
import club.hutcwp.lifeutil.entitys.Photo;
import club.hutcwp.lifeutil.ui.MainActivity;
import club.hutcwp.lifeutil.ui.base.BaseFragment;
import hut.cwp.mvp.BindPresenter;

@BindPresenter(presenter = PicturePresenter.class)
public class PictureFragment extends BaseFragment<PicturePresenter, IPicture> implements IPicture {


    private PhotoAdapter adapter;


    private FragmentGankGirlBinding binding;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gank_girl;
    }

    @Override
    protected void lazyFetchData() {
        getDatasByType();
    }

    private void getDatasByType() {
        setRefreshing(true);
        if (getArguments().getInt("type") == 0) {
            getPresenter().getGank();
        } else {
            getPresenter().getServer();
        }
    }

    @Override
    protected void initViews() {
        binding = (FragmentGankGirlBinding) getBinding();
        //可能会出现空指针异常
        adapter = new PhotoAdapter(getContext(), null);
        binding.gridRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.gridRecycler.addItemDecoration(new SpacesItemDecoration(14));
        binding.gridRecycler.setAdapter(adapter);
        setting();
    }

    /**
     * 注意，因放置在initView方法的最后，以避免出现空指针
     */
    public void setting() {
        binding.swipRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        binding.swipRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().serRefresh(true);
                getDatasByType();
            }
        });

        binding.gridRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!binding.gridRecycler.canScrollVertically(1)) {
                    // 此操作先与getDatasByType（）
                    getPresenter().serRefresh(false);
                    getDatasByType();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    public void showSnack(String msg) {
        ((MainActivity) getActivity()).showSnack(msg);
    }

    @Override
    public void setRefreshing(boolean status) {
        binding.swipRefreshLayout.setRefreshing(status);
    }

    @Override
    public void setNewData(List<Photo> data) {
        adapter.setNewData(data);
    }

    @Override
    public void addNewData(List<Photo> data) {
        adapter.addDatas(data);
    }


    /**
     * Recycler的分割线
     */
    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }


}