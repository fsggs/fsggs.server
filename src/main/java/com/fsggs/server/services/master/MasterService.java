package com.fsggs.server.services.master;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class MasterService {
    static private final String gate = "/api/update.json";

    private class UpdateMasterServerTimeTask extends TimerTask {
        public void run() {
            MasterService.updateMasterServerTime();
        }
    }

    public void updateMasterServerTimeTask() {
        Timer timer = new Timer();
        timer.schedule(new UpdateMasterServerTimeTask(), 0, 30 * 1000);
    }

    static public void updateMasterServerTime() {
//        if (!Application.run) return;
//        String masterServerURL = Application.serverConfig.get("master_server_url") + gate;
//        String token = Application.serverConfig.get("master_server_token");
//
//        try {
//            URL url = new URL(masterServerURL);
//            Map<String, Object> params = new LinkedHashMap<>();
//            params.put("token", token);
//            StringBuilder postData = new StringBuilder();
//            for (Map.Entry<String, Object> param : params.entrySet()) {
//                if (postData.length() != 0) postData.append('&');
//                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//                postData.append('=');
//                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//            }
//            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//            conn.setDoOutput(true);
//            conn.getOutputStream().write(postDataBytes);
//
//            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//            in.close();
//            conn.disconnect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
