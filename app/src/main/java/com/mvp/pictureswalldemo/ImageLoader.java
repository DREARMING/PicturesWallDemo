package com.mvp.pictureswalldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 爱的LUICKY on 2016/12/14.
 */

public class ImageLoader {
    private static LruCache<String,Bitmap> mLrucache;
    private static int reqestWidth;
    private static ImageLoader loader;

    private static ImageLoader getInstance(int maxSize){
        if(loader == null){
            loader = new ImageLoader(maxSize);
        }
        return loader;
    }

    public static ImageLoader getInstance(){
        if(loader == null){
            loader = new ImageLoader();
        }
        return loader;
    }

    private ImageLoader(int maxSize){
        if(mLrucache == null){
            int maxMemory = maxSize;
            if(maxMemory<=0) {
                maxMemory = (int) Runtime.getRuntime().maxMemory() / 4;
            }
            mLrucache = new LruCache<String,Bitmap>(maxMemory){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        }
    }
    private ImageLoader(){
        if(mLrucache == null){
            int maxMemory = (int) Runtime.getRuntime().maxMemory() / 4;
            mLrucache = new LruCache<String,Bitmap>(maxMemory){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        }
    }


    public  void addImageLrucache(String url,Bitmap bitmap){
        if(mLrucache.get(url) != null){
            mLrucache.put(url,bitmap);
        }
    }

    private boolean hasSDCard(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public  Bitmap getBitmapCache(String url){
        if(url == null) return null;
        Bitmap bitmap = mLrucache.get(url);
        if(bitmap != null) return bitmap;
        return null;
    }

    public String getBitmapFilePath(String url){
        if(hasSDCard()){
            String imgDir = Environment.getExternalStorageDirectory().getPath()+"/picturesCaches/";
            File file = new File(imgDir);
            if(!file.exists()){
                boolean isCreated = file.mkdir();
                if(!isCreated) return null;
            }
            String lastName = url.substring(url.lastIndexOf("/")+1);
            return imgDir+lastName;
        }
        return null;
    }

    public Bitmap getBitmapInFilePath(String url,int reqWidth){
        reqestWidth = reqWidth;
        return decodeBitmapInSampleSize(getBitmapFilePath(url),reqWidth);
    }

    class DownloadPictureTask extends AsyncTask<String,Void,Bitmap>{
        private ImageView imageView;

        public DownloadPictureTask(ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getImageFromInternet(params[0],reqestWidth);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


    public Bitmap getImageFromInternet(final String url, final int reqWidth){
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            URL Url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setReadTimeout(2000);
            connection.setConnectTimeout(5000);
            connection.connect();
            InputStream is = connection.getInputStream();
            fos = new FileOutputStream(getBitmapFilePath(url));
            bis = new BufferedInputStream(is);
            int len = 0;
            byte[] bytes = new byte[1024 * 4];
            while((len = bis.read(bytes)) != -1){
                fos.write(bytes,0,len);
            }

            Bitmap bitmap =  getBitmapInFilePath(url,reqWidth);
            mLrucache.put(url,bitmap);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bis!=null)
                    bis.close();
                if(fos!=null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }



    private int caculateInSampleSize(BitmapFactory.Options options,int req){
        int outWidth = options.outWidth;
        int sampleSize = 1;
        if(outWidth > req){
            sampleSize =  Math.round((float) outWidth / (float) req);
        }
        return sampleSize;
    }

    private Bitmap decodeBitmapInSampleSize(String filePath,int reqWidth){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);
        int sampleSize = caculateInSampleSize(options,reqWidth);
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(filePath,options);
    }
}
