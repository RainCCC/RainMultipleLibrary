package com.rain.customviewutil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rain.liba_rainbaseadapter.base.RainBaseRvAdapter
import com.rain.liba_rainbaseadapter.base.RainBaseRvViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val mList = arrayListOf<String>()

    private var mAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vRv.layoutManager = LinearLayoutManager(this)
        mAdapter = MainAdapter(this,mList)
        //添加头部视图
        mAdapter?.addHeaderView(LayoutInflater.from(this).inflate(R.layout.adapter_main_head, vRv, false), false)
        mAdapter?.addHeaderView(LayoutInflater.from(this).inflate(R.layout.adapter_main_head, vRv, false), false)
        //添加底部视图
        mAdapter?.addFootView(LayoutInflater.from(this).inflate(R.layout.adapter_main_foot, vRv, false), false)
        mAdapter?.addFootView(LayoutInflater.from(this).inflate(R.layout.adapter_main_foot, vRv, false), false)
        vRv.adapter = mAdapter

        //设置上拉加载更多监听
        mAdapter?.setAdapterLoadMoreListener {
            loadData()
        }

        mAdapter?.setAdapterItemOnClickListener { view, position ->

        }

        loadData()
    }

    private fun loadData() {
        GlobalScope.launch {
            for (i in 1..20) {
                mList.add("$i year")
            }
            delay(2000)
            launch(Dispatchers.Main) {
                Log.i("RainMain3", Thread.currentThread().toString())
                if (mList.size>100){
                    mAdapter?.finishLoadMore()
                }else{
                    mAdapter?.startLoadMore()
                }
                mAdapter?.upData(mList)
            }
        }
    }

    fun clearHead(view: View) {
        mAdapter?.removeHeaderView(0,true)
    }

    fun clearFoot(view: View) {
        mAdapter?.removeFootView(0,true)
    }
}
