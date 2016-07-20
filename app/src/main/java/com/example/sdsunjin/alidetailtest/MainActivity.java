package com.example.sdsunjin.alidetailtest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private ListView mListView;
    private String mUrl = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.lv_main);
        new NewsAsyncTask().execute(mUrl);
    }


    /*
    * 第一个参数params,是一个String类型的json数据请求网址
    * 第二个参数Progress，是中间过程，不需要时填Void
    * 第三个参数Result，是返回json请求的数据的封装后的对象，每个NewsBean都代表一行数据， 拿到这样一个List就可以传到Adapter中，最终将这样一个Adapter设置给listView，最后显示Json数据的内容信息；
    * 本demo在通过json数据的url网络获取json数据时使用的的AsyncTask异步加载，而在下载图片时使用的是子线程的方式
    * */
    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {
        //在doInBackground方法中传入json数据的url，返回json格式的数据，并构造一个NewsBean对象的List,params为请求参数数组，只有一个元素params[0]为json数据的请求URL
        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) { //将生成的newsBean设置给ListView
            super.onPostExecute(newsBeans);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsBeans); //NewsAdapter返回的是一个包含了数据源的item，在setAdapter中将这个item赋给ListView进行展示
            mListView.setAdapter(adapter);
        }

        private List<NewsBean> getJsonData(String url) { //通过json的url获取json数据：
            List<NewsBean> newsBeansList = new ArrayList<>();
            JSONObject jsonObject;
            //获取json格式的字符串，创建readStream()方法读取url溸对应的json字节流并返回json格式的字符串数据
            String jsonString = null; //该处通过URL()方法传入url后直接获取url连接的网路数据，返回数据类型为InputStream形式
            try {
                jsonString = readStream(new URL(url).openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jsonObject = new JSONObject(jsonString); //将获取的jsonString转换成jsonObject对象
                NewsBean newsBean;
                JSONObject mjsonObject;
                JSONArray jsonArray = jsonObject.getJSONArray("data"); //取出data所对应的JsonArray
                for (int i=0; i<jsonArray.length(); i++) { //遍历jsonArray取出里面的每一个jsonObject,并取出每一个jsonObject里面所对应的值
                    mjsonObject = jsonArray.getJSONObject(i);
                    newsBean = new NewsBean();
                    newsBean.newsTitle = mjsonObject.getString("name");
                    newsBean.newsIcon = mjsonObject.getString("picSmall");
                    newsBean.newsContent = mjsonObject.getString("description");

                    newsBeansList.add(newsBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newsBeansList;
        }

        private String readStream(InputStream inputStream) { //该方法输入字节流输出json格式的字符串
            InputStreamReader inputStreamReader;
            String result = "";
            try {
                String line = "";
                inputStreamReader = new InputStreamReader(inputStream, "UTF-8");//将字节流inputStream转换成字符流
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}
