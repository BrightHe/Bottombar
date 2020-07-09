package com.example.a15927.bottombardemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a15927.bottombardemo.R;
import com.example.a15927.bottombardemo.functiontools.ItemGoods;
import com.example.a15927.bottombardemo.item.ItemGoodsActivity;

import java.util.List;

/**
 * Created by Administrator on 2019/4/4.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder>{
    private Context mContext;
    private List<ItemGoods> mGoodsList;

    //内部类ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goods_img;
        TextView goodsID;
        TextView goodsName;
        TextView goods_price;
        TextView goods_quality;
        TextView goods_unit;

        //获取布局中的实例，  这里的view是指RecyclerView的子项的最外层布局
        public ViewHolder(View view) {
            super( view );
            goods_img = (ImageView)view.findViewById( R.id.goods_image );
            goodsID = (TextView) view.findViewById( R.id.goodsID );
            goodsName = (TextView)view.findViewById( R.id.goods_name );
            goods_price = (TextView)view.findViewById( R.id.price );
            goods_quality = (TextView)view.findViewById( R.id.quality );
            goods_unit = (TextView)view.findViewById( R.id.unit );
        }
    }

    //构造函数
    //这个方法用于把要展示的数据源传进来，并赋值给一份全局变量mGoodsList,后续的操作都基于这个数据源
    public GoodsAdapter(Context context,List<ItemGoods> goodsList) {
        mContext = context;
        mGoodsList = goodsList;
    }

    //由于继承自RecyclerView.Adapter，需要重写下面的三个方法
    @Override
    //此方法用于创建ViewHolder的实例
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //动态加载布局 ，首先将goods_item布局加载进来，
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.goods_item,parent,false );
        //再创建ViewHolder的实例，并将加载的布局传入到构造函数中，
        final ViewHolder holder = new ViewHolder( view );
        //点击事件
        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Bundle data = new Bundle(  );
                ItemGoods itemGoods = mGoodsList.get( position );
                data.putSerializable( "goodsList",itemGoods );
                Intent intent = new Intent( mContext, ItemGoodsActivity.class );
                intent.putExtras( data );
                mContext.startActivity( intent );
            }
        } );
        //最后将ViewHolder的实例返回
        return holder;
    }

    @Override
    //此方法适用于对RecyclerView子项的数据进行赋值的，会在每个子项被滚到屏幕内的时候执行
    public void onBindViewHolder(ViewHolder holder, int position) {
        //通过position参数得到当前的子项实例
        ItemGoods goods = mGoodsList.get( position );
        //设置数据
        String img = goods.getGoodsImg();
        if(img == null){
            holder.goods_img.setImageResource( R.drawable.kimg );
        }
        else{
            Glide.with( mContext ).load( goods.getGoodsImg() ).centerCrop()
                    .error( R.drawable.ic_launcher ).into( holder.goods_img );
        }
        holder.goodsID.setText( goods.getGoodsID() );
        if(goods.getGoodsName() != null && goods.getGoodsName().length() > 7){
            holder.goodsName.setText( goods.getGoodsName().substring( 0,7 )+"..." );
        }else{
            holder.goodsName.setText( goods.getGoodsName() );
        }
        holder.goods_price.setText( ""+goods.getPrice() );
        holder.goods_quality.setText( ""+ goods.getQuality() );
        holder.goods_unit.setText( goods.getUnit() );
    }

    @Override
    //得到RecyclerView子项的数目
    public int getItemCount() {
        return mGoodsList.size();
    }

}
