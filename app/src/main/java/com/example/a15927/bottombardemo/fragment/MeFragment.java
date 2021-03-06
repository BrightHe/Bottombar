package com.example.a15927.bottombardemo.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a15927.bottombardemo.R;
import com.example.a15927.bottombardemo.meactivity.About_us;
import com.example.a15927.bottombardemo.meactivity.CollectionInf;
import com.example.a15927.bottombardemo.meactivity.LoginSetting;
import com.example.a15927.bottombardemo.meactivity.MeLogin;
import com.example.a15927.bottombardemo.meactivity.MyForBuy;
import com.example.a15927.bottombardemo.meactivity.MyShop;
import com.example.a15927.bottombardemo.meactivity.UserInfor;

public class MeFragment extends Fragment {
    private String TAG = "Test";
    //初始化
    private RelativeLayout Login;
    private ImageView image_plus;
    private TextView user,login_username,login_ps;
    private RelativeLayout  setting,forShop,forBuy,collection,about_us;

    //标志位
    private boolean login = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        //绑定控件，设置监听
        Login = (RelativeLayout) view.findViewById(R.id.login_me);
        image_plus = (ImageView)view.findViewById( R.id.image_plus ) ;
        user = (TextView)view.findViewById( R.id.user );
        login_username = (TextView)view.findViewById( R.id.login_username );
        login_ps = (TextView)view.findViewById( R.id.login_ps );
        forShop = (RelativeLayout) view.findViewById( R.id.my_shop );
        forBuy = (RelativeLayout) view.findViewById( R.id.my_request );
        collection = (RelativeLayout)view.findViewById( R.id.my_collect );
        about_us = (RelativeLayout)view.findViewById( R.id.about_us );
        setting = (RelativeLayout) view.findViewById( R.id.set_up );

        //取出从登录界面存储的登录是否成功的标志
        SharedPreferences User = getActivity().getSharedPreferences( "data", Context.MODE_PRIVATE );
        //如果未找到该值，则使用get方法中传入的默认值false代替
        login = User.getBoolean( "login", false );
        Log.i( TAG, "MeFragment: login is "+login );
        //如果已经登录成功
        if(login == true){
            //从登录界面data取出用户名
            String username = User.getString( "uname","" );
            String picDir = User.getString( "picDir","" );
            String ps = User.getString( "ps","" );
            Log.i( TAG, "MeFragment: picDir is  "+picDir );
            Log.i( TAG, "MeFragment: username is "+username );
            Log.i( TAG, "MeFragment: ps is "+ps );
            Glide.with( getActivity() ).load( picDir ).centerCrop().error( R.drawable.bg ).into( image_plus );
            user.setVisibility( View.GONE );
            login_username.setVisibility( View.VISIBLE );
            login_ps.setVisibility( View.VISIBLE );
            login_username.setText( username );
            if(ps.isEmpty()){
                login_ps.setText( "个性签名:这个人很懒,什么也没有留下。");
            }else{
                login_ps.setText( "个性签名:"+ps);
            }
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //没有登录成功
                if(login == false) {
                    //进入登录界面
                    Intent intent_login = new Intent( getActivity(), MeLogin.class );
                    startActivity( intent_login );
                }
                //登录成功
                else{
                    //进入用户详情界面
                    Intent intent_info = new Intent( getActivity(), UserInfor.class );
                    startActivity( intent_info );
                }
            }
        });

        forShop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login == true){
                    //进入我的店铺
                    Intent shop = new Intent( getActivity(), MyShop.class );
                    startActivity( shop );
                }else{
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText( getActivity(), "登录之后才能查看哦，请先登录账号！", Toast.LENGTH_SHORT ).show();
                        }
                    } );
                }
            }
        } );

        forBuy.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login == true){
                    //进入我的店铺
                    Intent forBuy = new Intent( getActivity(), MyForBuy.class );
                    startActivity( forBuy );
                }else{
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText( getActivity(), "登录之后才能查看哦，请先登录账号！", Toast.LENGTH_SHORT ).show();
                        }
                    } );
                }
            }
        } );

        collection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login == true){
                    //进入我的店铺
                    Intent collect = new Intent( getActivity(), CollectionInf.class );
                    startActivity( collect );
                }else{
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText( getActivity(), "登录之后才能查看哦，请先登录账号！", Toast.LENGTH_SHORT ).show();
                        }
                    } );
                }
            }
        } );

        about_us.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent as = new Intent( getActivity(), About_us.class );
                startActivity( as );
            }
        } );

        setting.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_set = new Intent( getActivity(), LoginSetting.class );
                startActivity( intent_set );
            }
        } );

        return view;
    }

}