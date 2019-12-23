# RainBaseAdapter 

RainBaseAdapter是Android RecyclerView的万能适配器，很轻量，支持添加头部，尾部，空视图及其上拉加载更多等等

### 引入方式
```
compile 'com.rain:RainBaseAdapter:1.0.0'
```
基于AndroidX开发，使用前请确保代码已升级为AndroidX

### 创建适配器
使用非常方便，干净卫生
```
class MainAdapter(activity: Activity, list: MutableList<String>) : RainBaseRvAdapter<String>(activity, list) {

    /**
     * 设置数据
     */
    override fun convertData(holder: RainBaseRvViewHolder?, entity: String?, position: Int) {
        holder?.getTextView(R.id.vTvContent)?.text = entity
    }

    /**
     * adapter使用的layout
     */
    override fun getLayoutViewId(viewType: Int): Int {
        return R.layout.adapter_main
    }

}
```

### 添加HeadView
```
/**
     * 添加头部的视图
     *
     * @param view
     * @param isNotify 是否通知插入一个条目
     */
    public void addHeaderView(View view, boolean isNotify) {}
```

### 添加FootView
```
/**
     * 添加底部的视图
     *
     * @param view
     * @param isNotify 是否通知插入一个条目
     */
    public void addFootView(View view, boolean isNotify) {}
```

### 设置EmptyView
默认不打开空视图，如果设置了EmptyView并且list数据源为空时则显示空视图
```
 /**
     * 设置空View
     *
     * @param view
     */
    public void setEmptyView(View view) {}
    
    /**
     * 没有数据时显示空vie
     *
     * @param openEmptyView 是否开启空view （默认关闭空view）
     */
    public void setOpenEmptyView(boolean openEmptyView)
```

### 设置上拉加载更多
RainBaseAdapter默认提供了上拉加载更多的View，也可以自定义
```
    /**
     * 设置上拉加载View
     *
     * @param view
     */
    public void setLoadMoreView(View view) {}
    
    /**
     * 上拉加载监听
     *
     * @param iLoadMoreListener
     */
    public void setAdapterLoadMoreListener(RainAdapterLoadMoreListener iLoadMoreListener){}
    
     /**
     * 打开上拉加载未更新适配器
     */
    public void startLoadMore() {}
    
    /**
     * 关闭上拉加载未更新适配器
     */
    public void endLoadMore() {}
    
    /**
     * 上拉数据加载错误显示loadmoreview,但关闭监听
     */
    public void errorLoadMore() {}
    
    /**
     * 上拉数据加载完成显示loadmoreview,但关闭监听
     */
    public void finishLoadMore() {}
    
     /**
     * 设置点击上拉加载更多结果View监听
     *
     * @param mLoadMoreResultListener
     */
    public void setLoadMoreResultListener(RainAdapterLoadMoreClickListener mLoadMoreResultListener) {}
    
```
