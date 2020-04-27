package com.rain.liba_rainbaseadapter.base;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rain.liba_rainbaseadapter.listener.RainAdapterChildItemLongClickListener;
import com.rain.liba_rainbaseadapter.listener.RainAdapterChildItemOnClickListener;
import com.rain.liba_rainbaseadapter.listener.RainAdapterItemLongClickListener;
import com.rain.liba_rainbaseadapter.listener.RainAdapterItemOnClickListener;
import com.rain.liba_rainbaseadapter.listener.RainAdapterLoadMoreClickListener;
import com.rain.liba_rainbaseadapter.listener.RainAdapterLoadMoreListener;
import com.rain.liba_rainbaseadapter.widget.RainLoadMoreView;

import java.util.ArrayList;
import java.util.List;


/**
 * 支持添加HeaderView,FootView,EmptyView,上拉加载更多View,
 * <p>
 * 显示空视图的时候，不隐藏headView和footView
 * <p>
 * RecyclerView 万能适配器
 * Created by Rain on 2019/3/25 0025
 */
public abstract class RainBaseRvAdapter<T> extends RecyclerView.Adapter<RainBaseRvViewHolder> {

    public RainBaseRvAdapter(Activity mActivity, List<T> list) {
        this.mActivity = mActivity;
        if (list != null) {
            this.list = list;
        }
        setDefaultLoadMoreView();
    }

    protected Activity mActivity;

    /**
     * 头部的tag
     */
    private List<Integer> headerTags = new ArrayList<>();
    /**
     * 数据源
     */
    protected List<T> list = new ArrayList<>();
    /**
     * 头部的View集合
     */
    private List<View> headViews = new ArrayList<>();
    /**
     * 底部的View集合
     */
    private List<View> footViews = new ArrayList<>();
    /**
     * 底部的tag
     */
    private List<Integer> footTags = new ArrayList<>();

    //数据为空的Type
    private static final int EMPTYVIEWTYPE = -10000;
    //上拉加载Type
    private static final int LOADMORETYPE = -40000;
    /**
     * 标准头部viewType值
     */
    private static final int headerInt = 8000;
    /**
     * 标准头部viewType值
     */
    private static final int footInt = 9000;

    /**
     * 没有数据时的View,上拉加载更多的View
     */
    private View emptyView, loadMoreView;
    /**
     * 是否需要上拉加载更多
     */
    private boolean endLoadMore = false;
    /**
     * 开启上拉加载更多监听 用于上拉加载完成没有更多数据
     */
    private boolean openLoadMoreListener = false;
    /**
     * 是否打开emptyView
     */
    private boolean openEmptyView = false;

    //当前的位置
    protected int mCurrentPosition = -1;
    /**
     * handler处理延时操作
     */
    private Handler handler = new Handler();

    /**
     * 需要监听的子View的数组
     */
    private int[] childId;

    private boolean isListenerChildLongClick = false;

    private RainAdapterItemOnClickListener iOnClickListener;

    private RainAdapterItemLongClickListener iOnLongClickListener;

    private RainAdapterChildItemOnClickListener iOnChildClickListener;

    private RainAdapterChildItemLongClickListener iOnChildLongClickListener;

    private RainAdapterLoadMoreClickListener mLoadMoreResultListener;

    //监听加载更多的事件
    private RainAdapterLoadMoreListener iLoadMoreListener;

    /**
     * 总View个数
     *
     * @return
     */
    public int totalItemCount() {
        return headViews.size() + list.size() + footViews.size() + loadMoreViewCount();
    }

    /**
     * 上拉加载更多View的个数
     *
     * @return
     */
    private int loadMoreViewCount() {
        return hasLoadMore() ? 1 : 0;
    }

    /**
     * 头部和list数据的个数
     *
     * @return
     */
    private int headAndDataCount() {
        return headViews.size() + list.size();
    }

    /**
     * 获取头部视图的个数
     *
     * @return
     */
    private int headerCounts() {
        return headViews.size();
    }

    /**
     * 是否为空
     */
    private boolean isEmpty() {
        if (emptyView != null && openEmptyView) {
            if (list != null) {
                return list.size() == 0;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 没有数据时显示空view
     *
     * @param openEmptyView 是否开启空view
     */
    public void setOpenEmptyView(boolean openEmptyView) {
        this.openEmptyView = openEmptyView;
    }

    /**
     * 没有数据时显示空view
     *
     * @param openEmptyView 是否开启空view
     * @param openNotify    是否更新
     */
    public void setOpenEmptyView(boolean openEmptyView, boolean openNotify) {
        if (openNotify) {
            if (this.openEmptyView != openEmptyView) {
                this.openEmptyView = openEmptyView;
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 添加头部的视图
     *
     * @param view
     * @param isNotify 是否通知插入一个条目
     */
    public void addHeaderView(View view, boolean isNotify) {
        if (view == null) {
            throw new NullPointerException("the header view can not be null");
        }
        headViews.add(view);
        if (isNotify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 删除一个视图
     *
     * @param position
     */
    public void removeHeaderView(int position, boolean isNotify) {
        if (headViews.size() > position) {
            headViews.remove(position);
            headerTags.remove(position);
            if (isNotify) {
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 添加底部的视图
     *
     * @param view
     * @param isNotify 是否通知插入一个条目
     */
    public void addFootView(View view, boolean isNotify) {
        if (view == null) {
            throw new NullPointerException("the header view can not be null");
        }
        footViews.add(view);
        if (isNotify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 删除一个视图
     *
     * @param position
     */
    public void removeFootView(int position, boolean isNotify) {
        if (footViews.size() > position) {
            footViews.remove(position);
            footTags.remove(position);
            if (isNotify) {
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 删除所有footView
     */
    public void removeAllHeadView() {
        headViews.clear();
        headerTags.clear();
        notifyDataSetChanged();
    }

    /**
     * 删除所有footView
     */
    public void removeAllFootView() {
        footViews.clear();
        footTags.clear();
        notifyDataSetChanged();
    }

    /**
     * 设置空View
     *
     * @param view
     */
    public void setEmptyView(View view) {
        if (view != null)
            this.emptyView = view;
    }

    /**
     * 设置上拉加载View
     *
     * @param view
     */
    public void setLoadMoreView(View view) {
        if (view != null)
            this.loadMoreView = view;
    }

    private void setDefaultLoadMoreView() {
        if (null != mActivity) {
            this.loadMoreView = new RainLoadMoreView(mActivity);
        }
    }

    /**
     * 当前位置是否是上拉加载
     *
     * @param position
     * @return
     */
    private boolean isShowLoadMore(int position) {
        return hasLoadMore() && position >= getItemCount() - 1;
    }

    /**
     * 符合上拉加载的条件
     *
     * @return
     */
    private boolean hasLoadMore() {
        return loadMoreView != null && endLoadMore;
    }


    @Override
    public int getItemCount() {
        if (isEmpty()) {
            return headViews.size() + 1 + footViews.size();
        } else {
            return totalItemCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        mCurrentPosition = position;
        //如果是头部
        if (position < headViews.size()) {
            headerTags.add(position + headerInt);
            return position + headerInt;
        }
        //如果是底部
        if (position > headAndDataCount() - 1 && position < totalItemCount() - loadMoreViewCount()) {
            footTags.add(position + footInt);
            return position + footInt;
        }
        //如果没有数据
        if (isEmpty()) {
            return EMPTYVIEWTYPE;
        }
        //如果是上拉加载
        if (isShowLoadMore(position)) {
            return LOADMORETYPE;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RainBaseRvViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == EMPTYVIEWTYPE) {
            view = emptyView;
        } else if (headerTags.contains(viewType)) {
            view = headViews.get(viewType - headerInt);
        } else if (footTags.contains(viewType)) {
            view = footViews.get(viewType - footInt - headAndDataCount());
        } else if (viewType == LOADMORETYPE) {
            view = loadMoreView;
        } else {
            view = LayoutInflater.from(mActivity).inflate(getLayoutViewId(viewType), viewGroup, false);
        }

        RainBaseRvViewHolder viewHolder = new RainBaseRvViewHolder(view);

        //视图被创建的时候调用
        viewCreated(viewHolder, viewType);

        return viewHolder;
    }

    /**
     * 视图被创建的时候调用
     *
     * @param viewHolder
     * @param viewType
     */
    private void viewCreated(RainBaseRvViewHolder viewHolder, int viewType) {

    }

    /**
     * @param viewType 返回值就是根据这个值进行判断返回的对头部不起作用
     * @return
     */
    protected abstract int getLayoutViewId(int viewType);

    @Override
    public void onBindViewHolder(@NonNull RainBaseRvViewHolder holder, int position) {
        if (getItemViewType(position) == EMPTYVIEWTYPE) {
            return;
        }

        if (position < headViews.size()) {
            //如果是头部处理头部数据
            convertHeadViewData(holder, position);
        } else if (position > headAndDataCount() - 1 && position < totalItemCount() - loadMoreViewCount()) {
            //如果是底部处理底部数据
            convertFootViewData(holder, position - headAndDataCount() - 1);
        } else if (isShowLoadMore(position)) {
            if (null != loadMoreView) {
                if (loadMoreView instanceof RainLoadMoreView) {
                    ((RainLoadMoreView) loadMoreView).setAdapterLoadMoreClickListener(mLoadMoreResultListener);
                }
            }
            if (openLoadMoreListener) {
                //如果是上拉加载
                if (iLoadMoreListener != null) {
                    //防止Cannot call this method while RecyclerView is computing a layout or scrolling
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            iLoadMoreListener.onLoadMore();
                        }
                    }, 800);
                }
            }
        } else {
            //如果是数据的话
            final int dataPosition = position - headViews.size();

            if (null != iOnClickListener) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iOnClickListener.onClick(v, dataPosition);
                    }
                });
            }

            if (null != iOnLongClickListener) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        iOnLongClickListener.onClick(v, dataPosition);
                        return false;
                    }
                });
            }


            //监听子View
            if (null != childId) {
                if (childId.length > 0) {
                    for (Integer integer : childId) {
                        if (null != holder.getView(integer)) {
                            if (null != iOnChildClickListener) {
                                holder.getView(integer).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        iOnChildClickListener.onClick(v, dataPosition);
                                    }
                                });
                            }
                            if (null != iOnChildClickListener && isListenerChildLongClick) {
                                holder.getView(integer).setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        iOnChildLongClickListener.onClick(v, dataPosition);
                                        return false;
                                    }
                                });
                            }
                        }
                    }
                }
            }
            convertData(holder, list.get(dataPosition), position);
        }
    }

    /**
     * 设置动态监听子View
     *
     * @param iOnChildClickListener     子View 点击监听
     * @param iOnChildLongClickListener 子View 按钮长按监听
     * @param listenerId                子View的Id
     */
    public void setNeedListenerChildId(RainAdapterChildItemOnClickListener iOnChildClickListener
            , RainAdapterChildItemLongClickListener iOnChildLongClickListener, int[] listenerId) {
        isListenerChildLongClick = true;
        this.childId = listenerId;
        this.iOnChildClickListener = iOnChildClickListener;
        this.iOnChildLongClickListener = iOnChildLongClickListener;
    }

    /**
     * 设置动态监听子View
     *
     * @param iOnChildClickListener
     * @param listenerId
     */
    public void setNeedListenerChildId(RainAdapterChildItemOnClickListener iOnChildClickListener, int[] listenerId) {
        this.childId = listenerId;
        this.iOnChildClickListener = iOnChildClickListener;
    }

    /**
     * 实现列表的显示
     *
     * @param holder   RecycleView的ViewHolder
     * @param entity   实体对象
     * @param position 当前的下标
     */
    protected abstract void convertData(RainBaseRvViewHolder holder, T entity, int position);

    /**
     * 处理HeadView数据
     *
     * @param holder   RecycleView的ViewHolder
     * @param position 当前的下标
     */
    protected void convertHeadViewData(RainBaseRvViewHolder holder, int position) {

    }

    /**
     * 处理View数据
     *
     * @param holder   RecycleView的ViewHolder
     * @param position 当前的下标
     */
    protected void convertFootViewData(RainBaseRvViewHolder holder, int position) {

    }

    /**
     * item 点击监听
     *
     * @param iOnClickListener
     */
    public void setAdapterItemOnClickListener(RainAdapterItemOnClickListener iOnClickListener) {
        this.iOnClickListener = iOnClickListener;
    }

    /**
     * item 长按监听
     *
     * @param iOnLongClickListener
     */
    public void setiOnLongClickListener(RainAdapterItemLongClickListener iOnLongClickListener) {
        this.iOnLongClickListener = iOnLongClickListener;
    }

    /**
     * 上拉加载监听
     *
     * @param iLoadMoreListener
     */
    public void setAdapterLoadMoreListener(RainAdapterLoadMoreListener iLoadMoreListener) {
        this.iLoadMoreListener = iLoadMoreListener;
    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addData(List<T> list) {
        if (null != list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 更新数据
     *
     * @param list
     */
    public void upData(List<T> list) {
        if (null != list) {
            this.list = list;
            notifyDataSetChanged();
        }
    }

    /**
     * 获取列表数据个数（不包含head和foot）
     *
     * @return
     */
    public int getListCount() {
        if (null != list) {
            return list.size();
        }
        return 0;
    }

    /**
     * 移除指定item
     *
     * @param position
     */
    public void removeItem(int position) {
        if (null != this.list) {
            this.list.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除全部item
     *
     * @param
     */
    public void removeAllItem() {
        if (null != this.list) {
            for (int i = 0; i < this.list.size(); i++) {
                this.list.remove(i);
                notifyItemRemoved(i);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 关闭上拉加载未更新适配器
     */
    public void endLoadMore() {
        this.endLoadMore = false;
        this.openLoadMoreListener = false;
    }

    /**
     * 打开上拉加载未更新适配器
     */
    public void startLoadMore() {
        if (null != this.loadMoreView) {
            if (this.loadMoreView instanceof RainLoadMoreView) {
                ((RainLoadMoreView) this.loadMoreView).showLoadMore();
            }
        }
        this.endLoadMore = true;
        this.openLoadMoreListener = true;
    }

    //上拉数据加载完成显示loadmoreview,但关闭监听
    public void finishLoadMore() {
        if (null != this.loadMoreView) {
            if (this.loadMoreView instanceof RainLoadMoreView) {
                ((RainLoadMoreView) this.loadMoreView).showLoadFinish();
            }
        }
        this.endLoadMore = true;
        this.openLoadMoreListener = false;
    }

    /**
     * 上拉数据加载错误显示loadmoreview,但关闭监听
     */
    public void errorLoadMore() {
        if (null != this.loadMoreView) {
            if (this.loadMoreView instanceof RainLoadMoreView) {
                ((RainLoadMoreView) this.loadMoreView).showLoadError();
            }
        }
        this.endLoadMore = true;
        this.openLoadMoreListener = false;
    }

    /**
     * 设置点击上拉加载View监听
     *
     * @param mLoadMoreResultListener
     */
    public void setLoadMoreResultListener(RainAdapterLoadMoreClickListener mLoadMoreResultListener) {
        this.mLoadMoreResultListener = mLoadMoreResultListener;

    }
}
