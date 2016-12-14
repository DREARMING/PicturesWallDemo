package com.mvp.pictureswalldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.RuleBasedCollator;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static android.R.attr.bitmap;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class MyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ImageLoader loader;
    private String[] urls;
    private int loadNumOnceTime = 10;
    private int lastLoadNum = 0;

    public MyAdapter(Context context,String[] imagePaths){
        mContext = context;
        urls = imagePaths;
        loader = ImageLoader.getInstance();
        loadImages();
    }

    private void loadImages(){
        for(int i = lastLoadNum ; (i<lastLoadNum + loadNumOnceTime) && (i<urls.length);i++){

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(loader.getBitmapCache(urls[position]) == null){
            loadImages();
        }
        MyViewHolder holder1 = (MyViewHolder) holder;
        /*String url = urls[position];
        Bitmap bitmap = loader.getBitmapCache(url);
        if(bitmap == null){
            bitmap = loader.getBitmapInFilePath(url,100);
            if(bitmap == null){
                DownloadPictureTask task = new DownloadPictureTask(holder1.imageView);
                task.execute(url);
            }
        }*/
        holder1.setImage(null);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class DownloadPictureTask extends AsyncTask<String,Void,Bitmap> {
        private ImageView imageView;

        public DownloadPictureTask(ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = loader.getImageFromInternet(params[0],reqestWidth);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
