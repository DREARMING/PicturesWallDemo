package com.mvp.pictureswalldemo;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public MyViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.iv);
    }

    public void setImage(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }


}
