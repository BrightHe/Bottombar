package com.example.a15927.bottombardemo.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a15927.bottombardemo.R;
import com.example.a15927.bottombardemo.findactivity.DividerItemDecoration;
import com.example.a15927.bottombardemo.findactivity.FindBuy;
import com.example.a15927.bottombardemo.findactivity.FindSale;
import com.example.a15927.bottombardemo.findactivity.GoodsAdapter;
import com.example.a15927.bottombardemo.functiontools.DialogUIUtils;
import com.example.a15927.bottombardemo.functiontools.Goods;
import com.example.a15927.bottombardemo.functiontools.ItemGoods;
import com.example.a15927.bottombardemo.functiontools.PostWith;
import com.example.a15927.bottombardemo.functiontools.UserBuy;
import com.example.a15927.bottombardemo.views.PullToRefreshAndPushToLoadView6;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.a15927.bottombardemo.functiontools.DialogUIUtils.dismiss;

public class FindFragment extends Fragment implements View.OnClickListener{

    private String TAG = "Test";
    private TextView sale_button;
    private TextView buy_button;
    private ImageView image_add;

    //用来标记当前页面处于的状态   0---摊位  1---求购
    private static int statue = 0;

    private int opTypebuy  = 90004;
    private  String urlbuy = "http://47.105.185.251:8081/Proj31/buy";//http://192.168.2.134:8080/Proj20/buy

    //进度条一
    Dialog progressDialog;
    //进度条二
//    private LoadingDialog dialog;
//
//    private Handler mHandler = new Handler() {
//        public void dispatchMessage(android.os.Message msg) {
//            if (dialog != null && dialog.isShowing()) {
//                dialog.dismiss();
//            }
//        };
//    };


    //展示商品方式
    RecyclerView recyclerView;
    List<ItemGoods> Goodslist = new ArrayList<>();
    private PullToRefreshAndPushToLoadView6 pullToRefreshAndPushToLoadView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        //初始化组件
        init(view);

        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager( layoutManager );
        GoodsAdapter adapter = new GoodsAdapter( Goodslist );
        recyclerView.setAdapter( adapter );
        recyclerView.setHasFixedSize( true );
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( getActivity(),Color.DKGRAY,2,2 );
        dividerItemDecoration.setDrawBorderTopAndBottom( true );
        recyclerView.addItemDecoration( dividerItemDecoration );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        pullToRefreshAndPushToLoadView.setOnRefreshAndLoadMoreListener(new PullToRefreshAndPushToLoadView6.PullToRefreshAndPushToLoadMoreListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                load();
            }
        }, 0);
//        Intent intent_find = getActivity().getIntent();
//        String goodsGsonStr = intent_find.getStringExtra( "goodsInfo" );
//        Log.d( "Test", goodsGsonStr );
        return view;
    }


    public void init(View view){
        sale_button = (TextView) view.findViewById(R.id.sale);
        sale_button.setOnClickListener(this);
        buy_button = (TextView) view.findViewById( R.id.buy );
        buy_button.setOnClickListener( this );
        image_add = (ImageView) view.findViewById( R.id.image_add );
        image_add.setOnClickListener( this );
        pullToRefreshAndPushToLoadView = (PullToRefreshAndPushToLoadView6) view.findViewById(R.id.prpt);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);

        //设置默认状态 ：摊位
        sale_button.setEnabled( false );
        sale_button.setTextColor( Color.parseColor( "#0895e7" ) );
    }

    private void refresh() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pullToRefreshAndPushToLoadView.finishRefreshing();
            }
        }.start();
    }

    private void load() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pullToRefreshAndPushToLoadView.finishLoading();
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        //清除状态
        sale_button.setEnabled( true );
        sale_button.setTextColor( Color.WHITE );
        buy_button.setEnabled( true );
        buy_button.setTextColor( Color.WHITE );
        switch (view.getId()){
            case R.id.sale://摊位
                statue = 0;
                //设置所选状态
                sale_button.setEnabled( false );
                sale_button.setTextColor( Color.parseColor( "#0895e7" ) );

                break;

            case R.id.buy://求购
                statue = 1;
                //设置所选状态
                buy_button.setEnabled( false );
                buy_button.setTextColor( Color.parseColor( "#0895e7" ) );


                //进度框显示方法一
                progressDialog = DialogUIUtils.showLoadingDialog( getActivity(),"正在查询" );
                progressDialog.show();

//                //进度条显示方法二
//                dialog = new LoadingDialog(getActivity(),R.layout.tips_load);
//                //点击物理返回键是否可取消dialog
//                dialog.setCancelable(true);
//                //点击dialog之外 是否可取消
//                dialog.setCanceledOnTouchOutside(true);
//                //显示
//                dialog.show();

                //在求购界面查询商品
                enquire();

                break;
            case R.id.image_add:
                if(statue == 0){
                    sale_button.setEnabled( false );
                    sale_button.setTextColor( Color.parseColor( "#0895e7" ) );
                    Intent intent_sale = new Intent( getActivity(), FindSale.class );
                    startActivity( intent_sale );
                }
                else if(statue == 1){
                    buy_button.setEnabled( false );
                    buy_button.setTextColor( Color.parseColor( "#0895e7" ) );

                    //跳转到用户查询商品的界面
                    Intent intent_buy = new Intent( getActivity(), FindBuy.class );
                    startActivity( intent_buy );
                }
                else
                break;
            default:
                break;
        }
    }

    private void enquire() {
        //在Fragment中需要依赖的Activity (getActivity()方法需要使用)
        SharedPreferences sp = getActivity().getSharedPreferences( "data",MODE_PRIVATE );
        String uname = sp.getString( "uname","" );
        String token = sp.getString( "token","" );
        Log.i( "Test", "uname is  " +uname);
        Log.i( "Test", "token is  " +token );
        final UserBuy userBuy = new UserBuy();
        userBuy.setOpType( opTypebuy );
        userBuy.setToken( token );
        Gson gson_buy = new Gson();
        String userBuyJson = gson_buy.toJson( userBuy,UserBuy.class );
        //发送Post
        PostWith postWith = new PostWith();
        postWith.sendPostWithOkhttp( urlbuy, userBuyJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Test","获取数据失败了"+e.toString());
                getActivity().runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        //这里的上下文由于是Fragment，不能直接写，需要依赖活动
                        //取消进度框一
                        dismiss(progressDialog);
                        //取消进度条二
                        //mHandler.sendEmptyMessage(1);
                        Toast.makeText( getActivity(),"网络不给力！",Toast.LENGTH_SHORT ).show();
                    }
                } );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Test","获取数据成功了");
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
                Goodslist = goods.getGoodsList();
                Log.i( TAG, "goodsList" +Goodslist);
                //Flag判断
                if (flag == 200){
                    //切换到主线程
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            //取消进度框一
                            dismiss(progressDialog);
                            //取消进度条二
                            //mHandler.sendEmptyMessage(1);
                            Log.i( "Test", "run: 查询成功" );
                            Toast.makeText( getActivity(),"查询成功",Toast.LENGTH_SHORT ).show();
                            //LinearLayoutManager指定了recyclerView的布局方式，这里是线性布局
                            //与ListView类似的效果
                            LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity() );
                            recyclerView.setLayoutManager( layoutManager );
                            GoodsAdapter adapter = new GoodsAdapter( Goodslist );
                            recyclerView.setAdapter( adapter );
                        }
                    } );
                }
                else if(flag == 30001){
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            //取消进度框一
                            dismiss(progressDialog);
                            //取消进度条二
                            //mHandler.sendEmptyMessage(1);
                            Toast.makeText( getActivity(),"登录信息已失效,请再次登录",Toast.LENGTH_SHORT ).show();
                        }
                    } );
                }
                else{
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            //取消进度框一
                            dismiss(progressDialog);
                            //取消进度条二
                            //mHandler.sendEmptyMessage(1);
                            Toast.makeText( getActivity(),"出现错误，查询失败",Toast.LENGTH_SHORT ).show();
                        }
                    } );
                }
            }
        } );
     }

}


