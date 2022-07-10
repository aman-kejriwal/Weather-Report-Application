package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView temp1;
    TextView feel1;
    TextView main1;
    TextView description1;
    TextView pressure1,temp,text5;
    String main,description,sunset,sunrise;
    TextView sunset1,sunrise1;
    TextView humidity1;
    LocationManager locationManager;
    LocationListener listener;
    String cityname="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.inputcity);
//        res = (TextView) findViewById(R.id.Output);
        temp1=findViewById(R.id.temp1);
        feel1=findViewById(R.id.feel1);
        main1=findViewById(R.id.main1);
        description1=findViewById(R.id.description1);
        humidity1=findViewById(R.id.humidity1);
        pressure1=findViewById(R.id.pressure1);
        sunrise1=findViewById(R.id.sunrise1);
        sunset1=findViewById(R.id.sunset1);
        temp=findViewById(R.id.temp);
        text5=findViewById(R.id.text5);

//        weathinf=findViewById(R.id.weathinf1);

    }
    public void getWeather(View view) {
        String text= String.valueOf(editText.getText());
        editText.setText("");
        editText.setHint(text);
//        res.setVisibility(View.INVISIBLE);
        jsonget jsonget = new jsonget();
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + text + "&appid=192cf0b3d9d4f1b6f1b7a1577e534473";
        jsonget.execute(url);
        InputMethodManager img = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        img.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public class jsonget extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            Log.i("Starting", "Reached Here");
            String res = "";
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    res += (char) data;
                    data = reader.read();
                }
                Log.d("Aman", res);
                return res;

            } catch (Exception e) {
                Log.d("Error here", e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String weatherinfo = jsonObject.getString("weather");
                    JSONArray jsonArray = new JSONArray(weatherinfo);
                    String message = "";
                    Log.d("Insidde the loop", "I'M NOT IN");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.d("Insidde the loop", "I'M IN");
                        JSONObject jsonpart = jsonArray.getJSONObject(i);
                        Log.d("aman","lumar");
                        JSONObject sys=jsonObject.getJSONObject("sys");
                        long set=sys.getLong("sunset");
                        long rise=sys.getLong("sunrise");
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
                        Date time = new Date(set);
                        sunset=dateFormat.format(time);
                        Date time2 = new Date(rise);
                        sunrise=dateFormat.format(time2);
                        main = jsonpart.getString("main");
                        description = jsonpart.getString("description");
                        Log.d("Main get", main);
                        Log.d("description", description);
                        if ((!main.equals("")) || (!description.equals(""))) {
                            message += main + ": " + description + "\r\n";
                        }
                    }
                    String moreinfo = jsonObject.getString("main");
                    JSONObject moreinf = new JSONObject(moreinfo);
                    String temprature = moreinf.getString("temp");
                    String feelslike = moreinf.getString("feels_like");
                    temprature = String.format ("%,.1f",(Float.parseFloat(temprature)-273))+" °C";
                    feelslike = String.format ("%,.1f",(Float.parseFloat(feelslike)-273))+" °C";
                    String humidity=moreinf.getString("humidity");
                    String press = moreinf.getString("pressure");
                    if (!message.equals("")) {
//                        res.setText(message);
//                        res.setVisibility(View.VISIBLE);
                        temp1.setText(temprature);
                        feel1.setText(feelslike);
                        main1.setText(main+": ");
                        description1.setText(description);
                        humidity1.setText("Humidity: "+humidity);
                        pressure1.setText("Pressure: "+press);
                        sunrise1.setText("Sunrise: "+sunrise);
                        sunset1.setText("Sunset: "+sunset);
                        temp.setText("Temperature");
                        text5.setText("Feelslike");
                    }
                } catch (Exception e) {
                    Log.i("Error", e.toString());
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(MainActivity.this, "Invalid Input! Try again.", LENGTH_SHORT).show();
            }
        }
    }
}