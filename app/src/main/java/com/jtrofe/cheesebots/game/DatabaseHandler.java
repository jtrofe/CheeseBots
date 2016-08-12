package com.jtrofe.cheesebots.game;


import com.jtrofe.cheesebots.GameApp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by MAIN on 8/1/16
 */
public class DatabaseHandler {

    private GameApp gameApp;
    private String deviceId;
    public int CurrentPlace;

    public void SetDeviceId(String deviceId){
        // 9774d56d682e549c is a bad device ID that has something to do with a bug. Ignore it.
        if(deviceId.equals("9774d56d682e549c")){
            deviceId = "";
        }
        this.deviceId = deviceId;
    }

    public void SetApp(GameApp app){
        gameApp = app;
    }


    public DatabaseHandler(){}

    /**
     * Performs a GET HTTP request for the given url
     * @param getUrl HTTP url
     * @return Response text
     */
    private static String GET(String getUrl){
        String output;
        try{
            URL url = new URL(getUrl);

            HttpURLConnection httpCnn = (HttpURLConnection) url.openConnection();
            httpCnn.connect();
            httpCnn.setConnectTimeout(10000);
            httpCnn.setReadTimeout(10000);

            InputStream in = httpCnn.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(in));

            String line;
            output = "";

            while((line = rd.readLine()) != null){
                output += line;
            }

            rd.close();
        }catch(Exception e){
            output = "";
            e.printStackTrace();
        }
        return output;
    }

    /**
     * See if the current user has a high score and if so get their place.
     */
    public void GetPlace(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run(){
                String resp = GET("http://www.jtrofe.com/cheese_api/getPlace/" + deviceId);

                try{
                    CurrentPlace = Integer.parseInt(resp);
                }catch(NumberFormatException e){
                    CurrentPlace = -1;
                }
            }
        });

        t.start();
    }

    public String GetScores(){
        String url = "http://www.jtrofe.com/cheese_api/getScores/" + deviceId;

        return GET(url);
    }

    public String SubmitScore(final String name, final int score){
        String url = "http://www.jtrofe.com/cheese_api/submitScore/";

        String params = "Hello+my+name+is+";
        try {
            params += URLEncoder.encode(name, "UTF-8");
        }catch(UnsupportedEncodingException e){
            url += "";
        }

        params += "+%7C";
        params += "My+ID+is+" + deviceId + "%7C";
        params += "I+killed+" + score + "+robots%7C";
        url += params;
        System.out.println("Submitting to " + url);
        String resp = GET(url);

        System.out.println("Parsing GET response " + resp);



        String msg;
        try{
            CurrentPlace = Integer.parseInt(resp);

            if(CurrentPlace == 1){
                msg = "You got the gold!";
            }else if(CurrentPlace == 2){
                msg = "You got the silver!";
            }else if(CurrentPlace == 3){
                msg = "You got the bronze!";
            }else{
                msg = "Score submitted.";
            }
        }catch (NumberFormatException e){
            msg = "Error submitting score";
        }

        return msg;
    }
}
