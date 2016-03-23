package dineshkumarsahu.GroupDiscussion;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Dinesh Kumar Sahu on 6/28/2015.
 */
public class Indi extends Activity {
    TextView tvView,text;
    ImageView img;
    EditText et;
    String cid;
    Button bt;
    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indi);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        text=(TextView)findViewById(R.id.text);
        et=(EditText)findViewById(R.id.etext);
        bt=(Button) findViewById(R.id.bt);
        tvView = (TextView) findViewById(R.id.textView);
        img=(ImageView)findViewById(R.id.img);
        Intent intent = getIntent();

        cid = intent.getStringExtra("cid");
        String topic=intent.getStringExtra("topic");
        String type=intent.getStringExtra("type");
        tvView.setText(topic);
        switch(type)
        {
            case "pol":
                img.setBackgroundResource(R.drawable.pol);
                break;
            case "soc":
                img.setBackgroundResource(R.drawable.soc);
                break;
            case "eco":
                img.setBackgroundResource(R.drawable.eco);
                break;
            case "cul":
                img.setBackgroundResource(R.drawable.cul);
                break;
            case "sci":
                img.setBackgroundResource(R.drawable.sci);
                break;
            case "edu":
                img.setBackgroundResource(R.drawable.edu);
                break;
            case "spo":
                img.setBackgroundResource(R.drawable.spo);
                break;
            case "abs":
                img.setBackgroundResource(R.drawable.abs);
                break;
                default:break;
        }
        new task().execute();
        bt.setOnClickListener(new View.OnClickListener() {
            InputStream is= null;
            @Override
            public void onClick(View v) {
                String comment=""+et.getText().toString();
                int k=new StringTokenizer(comment).countTokens();
                if(comment==""){
                    Toast.makeText(getApplicationContext(), "Please don't post only spaces",Toast.LENGTH_SHORT).show();
                }
                else if(k<20)
                {
                    Toast.makeText(getApplicationContext(), "Please enter your comment in not less than 20 words",Toast.LENGTH_SHORT).show();
                }
                else {
                    List<NameValuePair> nvp = new ArrayList<NameValuePair>(1);
                    nvp.add(new BasicNameValuePair("comment", comment));
                    nvp.add(new BasicNameValuePair("cid", cid));
                    try {
                        enableHttpResponseCache();
                        HttpClient hc = new DefaultHttpClient();
                        HttpPost hp = new HttpPost("http://dineshgd.comuv.com/file2.php");
                        hp.setEntity(new UrlEncodedFormEntity(nvp));
                        hp.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36");
                        HttpResponse hr = hc.execute(hp);
                        HttpEntity he = hr.getEntity();
                        is = he.getContent();


                    } catch (UnsupportedEncodingException e) {
                        throw new AssertionError("UTF-8 is unknown");
                    } catch (ClientProtocolException e) {
                        throw new AssertionError("cp");
                    } catch (IOException e) {
                        throw new AssertionError("ioe");
                    }
                    new task().execute();
                    et.setText("");
                }
            }
        });
    }
    class task extends AsyncTask<String, String, Void>
    {

        InputStream is = null ;
        String result = "";

        @Override
        protected Void doInBackground(String... params) {
            String url_select = "http://dineshgd.comuv.com/file3.php";

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_select);

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("cid",cid));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(param));
                httpPost.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                //read content
                is =  httpEntity.getContent();

            } catch (Exception e) {

                Log.e("log_tag", "Error in http connection " + e.toString());
                //Toast.makeText(MainActivity.this, "Please Try Again", Toast.LENGTH_LONG).show();
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line=br.readLine())!=null)
                {
                    sb.append(line+"\n");
                }
                is.close();
                result=sb.toString();

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error converting result "+e.toString());
            }

            return null;

        }
        protected void onPostExecute(Void v) {


            // ambil data dari Json database
            String a="";
            try {
                JSONArray Jarray = new JSONArray(result);
                for(int i=0;i<Jarray.length();i++)
                {
                    JSONObject Jasonobject = null;
                    //text_1 = (TextView)findViewById(R.id.txt1);
                    Jasonobject = Jarray.getJSONObject(i);

                    //get an output on the screen
                    //String id = Jasonobject.getString("id");
                    String data = Jasonobject.getString("comment");

                    a += data+"\n\n";
                }
                text.setText(a);

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error parsing data "+e.toString());
            }
        }
    }
}
