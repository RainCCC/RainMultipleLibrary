package com.rain.liba_rainbaseadapter.base;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe # 通用ViewPager和Fragment使用适配器
 * <p>
 * Created by Rain on 2019/12/20
 */
public class RainNormalFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<NormalBean> mFragmentList = new ArrayList<>();

    /**
     * 常用的懒加载方式 BEHAVIOR_SET_USER_VISIBLE_HINT
     *
     * @param fragmentManager
     * @param fragmentList
     */
    @Deprecated
    public RainNormalFragmentPagerAdapter(FragmentManager fragmentManager, List<NormalBean> fragmentList) {
        super(fragmentManager);
        if (null != mFragmentList) {
            this.mFragmentList = fragmentList;
        }
    }

    /**
     * AndroidX方式的懒加载
     * behavior需要设置BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 则Fragment从不可见到可见将执行resume方法，Fragment不可见时，resume方法不执行
     *
     * @param fragmentManager
     * @param fragmentList
     * @param behavior      FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 则Fragment从不可见到可见将执行resume方法，Fragment不可见时，resume方法不执行
     */
    public RainNormalFragmentPagerAdapter(FragmentManager fragmentManager, List<NormalBean> fragmentList, int behavior) {
        super(fragmentManager, behavior);
        if (null != mFragmentList) {
            this.mFragmentList = fragmentList;
        }
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentList.get(position).getTitle();
    }

    public static class NormalBean implements Serializable {
        private Fragment fragment;//Fragment
        private String title;//标题

        public NormalBean(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
