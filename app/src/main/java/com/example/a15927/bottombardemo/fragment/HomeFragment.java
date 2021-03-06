package com.example.a15927.bottombardemo.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a15927.bottombardemo.R;
import com.example.a15927.bottombardemo.Utils.AppStr;
import com.example.a15927.bottombardemo.Utils.TestAndVerify;
import com.example.a15927.bottombardemo.adapter.GoodsAdapter;
import com.example.a15927.bottombardemo.functiontools.Goods;
import com.example.a15927.bottombardemo.functiontools.ItemGoods;
import com.example.a15927.bottombardemo.functiontools.PostWith;
import com.example.a15927.bottombardemo.functiontools.UserBuy;
import com.example.a15927.bottombardemo.homeactivity.HomeSearch;
import com.google.gson.Gson;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements OnBannerListener {
    private String TAG = "Test";
    //轮播图
    private Banner banner;
    private ArrayList<String> list_path;
//    private ArrayList<String> list_title;
    private ImageView search_button;
    private RecyclerView recycler_home;
    private List<ItemGoods> goodsList = new ArrayList<>(  );
    private List<ItemGoods> moreGoodsList = new ArrayList<>(  );
    private int opTypeBuy  = 90004;
    private  String urlBuy = "http://192.168.0.6:8081/Proj31/buy";
    private SpringView springView_home;
    private View netFailedLayout,home_nothing;
    //分页状态
    public int refreshPage = 1;
    public int loadPage = 1;
    //当前分页  1------加载，  2-----------刷新
    protected int checkType = 1;
    //每页数目
    public int pageSize = 5;
    private String token;
    private int statue = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //在Fragment中需要依赖的Activity (getActivity()方法需要使用)
        SharedPreferences sp = getActivity().getSharedPreferences( "data",MODE_PRIVATE );
        String uname = sp.getString( "uname","" );
        token = sp.getString( "token","" );
        Log.i( "Test", "uname is  " +uname);
        Log.i( "Test", "token is  " +token );

        banner = (Banner) view.findViewById(R.id.banner);
        netFailedLayout = view.findViewById( R.id.layout_net_failed );
        home_nothing = view.findViewById( R.id.home_nothing );
        recycler_home = (RecyclerView) view.findViewById( R.id.recycler_home );
        springView_home = (SpringView) view.findViewById( R.id.springView_home );
        search_button = (ImageView)view.findViewById(R.id.image_search) ;
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入搜索界面
                switch (view.getId()){
                    case R.id.image_search :
                        Intent intent_search = new Intent(getActivity(), HomeSearch.class);
                        startActivity(intent_search);
                        break;
                    default:
                        break;
                }

            }
        });
        initGoods();
        //上拉刷新，下拉加载功能实现
        springView_home.setHeader( new DefaultHeader( getActivity() ) );
        springView_home.setFooter( new DefaultFooter( getActivity() ) );
        springView_home.setListener( new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                refreshPage = 1;
                checkType = 2;
                initGoods();
                springView_home.onFinishFreshAndLoad();
            }

            @Override
            public void onLoadmore() {
                if(moreGoodsList != null){
                    loadPage++;
                }else{
                    loadPage = 1;
                }
                checkType = 1;
                initGoods();
                springView_home.onFinishFreshAndLoad();
            }
        } );
        initView();
        return view;
    }

    /*
     * 获取数据
     */
    private void initGoods() {
        UserBuy userBuy = new UserBuy();
        userBuy.setOpType( opTypeBuy );
        userBuy.setToken( token );
        userBuy.setPageSize( pageSize );
        if(checkType == 1){
            userBuy.setPage( loadPage );
        }else{
            userBuy.setPage( refreshPage );
        }
        userBuy.setCheckType( checkType );
        userBuy.setCondition( statue );
        Gson gson_buy = new Gson();
        String userBuyJson = gson_buy.toJson( userBuy,UserBuy.class );
        //application全局变量
        AppStr appStr = (AppStr)getActivity().getApplication();
        appStr.setState( false );
        //发送Post
        PostWith.sendPostWithOkhttp( urlBuy, userBuyJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i( TAG, "onFailure: " );
                AppStr appStr = (AppStr)getActivity().getApplication();
                appStr.setState( true );
                getActivity().runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        recycler_home.setVisibility( View.GONE );
                        netFailedLayout.setVisibility( View.VISIBLE );
                        home_nothing.setVisibility( View.GONE );
                        String errorData = TestAndVerify.judgeError( getActivity() );
                        Toast.makeText( getActivity(), errorData, Toast.LENGTH_SHORT ).show();
                    }
                } );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG,"获取数据成功了");
                String responseData = response.body().string();
                Log.i( TAG, "onResponse: " +responseData);
                //开始解析返回数据
                Log.i( TAG, "开始解析数据" );
                Gson gson = new Gson();
                //把属性给到对应的对象中
                Goods goods = gson.fromJson( responseData,Goods.class );
                Log.i( TAG, "解析数据完毕" );
                int flag = goods.getFlag();
                Log.i( TAG, "flag " +flag);
                goodsList = goods.getGoodsList();
                AppStr appStr = (AppStr)getActivity().getApplication();
                appStr.setState( true );
                //flag判断
                if (flag == 200) {
                    if(goodsList.size() == 0){
                        if((loadPage == 1 || refreshPage == 1) && moreGoodsList.size() == 0){
                            getActivity().runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    recycler_home.setVisibility( View.GONE );
                                    netFailedLayout.setVisibility( View.GONE );
                                    home_nothing.setVisibility( View.VISIBLE );
                                    Toast.makeText( getActivity(), "暂时没有商品展示哦！", Toast.LENGTH_SHORT ).show();
                                }
                            } );
                        }else{
                            getActivity().runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    recycler_home.setVisibility( View.VISIBLE );
                                    netFailedLayout.setVisibility( View.GONE );
                                    home_nothing.setVisibility( View.GONE );
                                    Toast.makeText( getActivity(), "没有更多的内容了！", Toast.LENGTH_SHORT ).show();
                                }
                            } );
                        }
                    }else{
                        //刷新
                        if(checkType == 2){
                            for(int i = 0; i < moreGoodsList.size(); i++ ){
                                boolean repeat  = false;
                                for(int j = 0; j< goodsList.size(); j++){
                                    if(goodsList.get( j ).getGoodsID().equals( moreGoodsList.get( i).getGoodsID() )){
                                        repeat = true;
                                        break;
                                    }
                                }
                                if(repeat == false){
                                    goodsList.add( moreGoodsList.get( i ) );
                                }
                            }
                            moreGoodsList = goodsList;
                        }
                        //加载
                        if(checkType == 1){
                            for (int i = 0; i < goodsList.size(); i++) {
                                boolean repeat = false;
                                for (int j = 0; j < moreGoodsList.size(); j++) {
                                    if (moreGoodsList.get( j ).getGoodsID().equals( goodsList.get( i ).getGoodsID() )) {
                                        repeat = true;
                                        break;
                                    }
                                }
                                if (repeat == false) {
                                    moreGoodsList.add( goodsList.get( i ) );
                                }
                            }
                        }
                        //切换到主线程
                        getActivity().runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                Log.i( TAG, "run: 查询成功" );
                                recycler_home.setVisibility( View.VISIBLE );
                                netFailedLayout.setVisibility( View.GONE );
                                home_nothing.setVisibility( View.GONE );
                                if(checkType == 1){
                                    Toast.makeText( getActivity(), "加载成功", Toast.LENGTH_SHORT ).show();
                                }else{
                                    Toast.makeText( getActivity(), "刷新成功", Toast.LENGTH_SHORT ).show();
                                }
                                //LinearLayoutManager指定了recyclerView的布局方式，这里是线性布局
                                LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity() );
                                recycler_home.setLayoutManager( layoutManager );
                                GoodsAdapter adapter = new GoodsAdapter( getActivity(), moreGoodsList );
                                recycler_home.setAdapter( adapter );
                                if(checkType == 1){
                                    recycler_home.scrollToPosition( adapter.getItemCount()-1 );
                                }
                            }
                        } );
                    }
                } else if (flag == 30001) {
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText( getActivity(), "登录信息已失效,请再次登录！", Toast.LENGTH_SHORT ).show();
                        }
                    } );
                } else {
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText( getActivity(), "出现错误，查询失败", Toast.LENGTH_SHORT ).show();
                        }
                    } );
                }
            }
        } );
    }

    /*
     *加载轮播
     */
    private void initView() {
        //放图片地址的集合
        list_path = new ArrayList<>();
//        //放标题的集合
//        list_title = new ArrayList<>();

        list_path.add("https://pic-001-1259665619.cos.ap-chengdu.myqcloud.com/bottomBarDemo/goodsPic/20190723_055538.jpg");
        list_path.add("https://pic-001-1259665619.cos.ap-chengdu.myqcloud.com/bottomBarDemo/goodsPic/20190723_090613.jpg");
        list_path.add("https://pic-001-1259665619.cos.ap-chengdu.myqcloud.com/bottomBarDemo/goodsPic/20190722_074813.jpg");
        list_path.add("https://pic-001-1259665619.cos.ap-chengdu.myqcloud.com/bottomBarDemo/goodsPic/20190722_081759.jpg");
//        list_title.add("");
//        list_title.add("");
//        list_title.add("");
//        list_title.add("");
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器，图片加载器在下方
        banner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        banner.setImages(list_path);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        banner.setBannerAnimation(Transformer.Default);
//        //设置轮播图的标题集合
//        banner.setBannerTitles(list_title);
        //设置轮播间隔时间
        banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        banner.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        banner.setIndicatorGravity(BannerConfig.CENTER)
//                //以上内容都可写成链式布局，这是轮播图的监听。比较重要。方法在下面。
//                .setOnBannerListener(this)
                //必须最后调用的方法，启动轮播图。
                .start();
    }

    /*
     *自定义的图片加载器
     */
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).fitCenter().into(imageView);
        }
    }

    /*
     *轮播图的监听方法
     */
    @Override
    public void OnBannerClick(int position) {
//        Log.i("tag", "你点了第"+position+"张轮播图");
    }

}