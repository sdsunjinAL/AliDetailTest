package com.example.sdsunjin.alidetailtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sdsunjin on 16/7/16.
 */
public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;
    private LruCache<String, Bitmap> mCaches; //定义缓存对象，设置其key值为图片的url，value值为Bitmap型的图片
    private ListView mListView;

    public ImageLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory(); //获取最大内存值：
        int cacheSize = maxMemory / 4; //设置缓存大小占整个内存大小的1/4
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) { //获取缓存对象的大小，正确加载内存大小，如果不重写该方法，返回的是元素个数
                return value.getByteCount(); //返回的应该是每次调用缓存存入bitmap图片时的图片bitMap的大小；
            }
        };
    }

    // 将bitmap增加到缓存中，首先通过key值(图片url)判断缓存中是否存在当前图片
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url) {

        return mCaches.get(url);
    }

    public Handler mHandler = new Handler() { //定义一个handler对象，在子线程中通过sendMessage()方法将包含bitmap图片资源的message发送到主线程的handler对象中，在handler对象中将图片资源赋给imageView进行展示
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) { //判断如果当前的view的tag标签正确，则显示图片
            mImageView.setImageBitmap((Bitmap) msg.obj); //将子线程中通过
            }
        }
    };

    public void showImageByThread(String url, ImageView imageView) { //创建子线程下载图片

        mUrl = url;
        Bitmap bitmap = getBitmapFromCache(mUrl); //首先从缓存中查询是否有图片，如果没有则去子线程下载，如果有则将通过key从缓存获取的图片赋给对应的imageView进行展示；
        if (null == bitmap) {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Bitmap bitmap = getBitmapFromUrl(mUrl);
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    mHandler.sendMessage(message); //子线程中不能直接更改主线程UI，所以该处不能直接将下载获取的bitmap类型图片直接赋值给imageView，需要通过Handler中的sendMessage()发送给主线程，在主线程中赋给view进行展示
                }
            }.start();
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public Bitmap getBitmapFromUrl(String urlString) { //通过图片url下载bitmap型图片资源，
        Bitmap bitmap;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString); //将String类型的url转换为URL类型
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (null != bitmap) {
                addBitmapToCache(urlString, bitmap); //将下载的图片保存到缓存中
            }
            connection.disconnect();
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 加载listview可见范围内的所有图片
     *
     */
    public void loadImages(int start, int end) {
        for (int i=start; i<end; i++) {
            String url = NewsAdapter.URLS[i];
            //看是否能从缓存中取出对应的图片
            Bitmap bitmap = getBitmapFromCache(url);
            if (null == bitmap) {
                showImageByThread(url);
            }
        }
    }
}
