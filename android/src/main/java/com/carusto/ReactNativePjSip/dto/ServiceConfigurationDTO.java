package com.carusto.ReactNativePjSip.dto;

import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import org.json.JSONObject;
import org.pjsip.pjsua2.StringVector;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class ServiceConfigurationDTO {

    public String ua;
    public ArrayList<String> stun;
    private HashMap<String, Object> notificationsConfig;
    private boolean enableSilentMode;

    public String getUserAgent() {
        return ua;
    }

    public StringVector getStunServers() {
        StringVector serversVector = new StringVector();
        for (String server : stun) {
            serversVector.add(server);
        }
        return serversVector;
    }

    public HashMap<String, Object> getNotificationsConfig() {
        return notificationsConfig;
    }

    public void setNotificationsConfig(HashMap<String, Object> notificationsConfig) {
        this.notificationsConfig = notificationsConfig;
    }

    public boolean isSilentModeEnabled() {
        return enableSilentMode;
    }

    public void setSilentModeEnabled( boolean enabled) {
        this.enableSilentMode = enabled;
    }

    public boolean isUserAgentNotEmpty() {
        return ua != null && !ua.isEmpty();
    }

    public boolean isStunServersNotEmpty() {
        return stun != null && stun.size() > 0;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("ua", ua);
            json.put("notifications", notificationsConfig);
            json.put("enableSilentMode", enableSilentMode);

            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ServiceConfigurationDTO fromIntent(Intent intent) {
        ServiceConfigurationDTO c = new ServiceConfigurationDTO();
        Log.d("SERVICE_MAP","fromIntent");
        if (intent.hasExtra("ua")) {
            c.ua = intent.getStringExtra("ua");
        }

        if(intent.hasExtra("notifications")) {

            c.setNotificationsConfig( new HashMap<>((Map<String, Object>)intent.getSerializableExtra("notifications")) );
            Log.d("SERVICE_MAP", c.getNotificationsConfig().toString());
        }

        if(intent.hasExtra("enableSilentMode")) {
            c.enableSilentMode = intent.getBooleanExtra("enableSilentMode", false);
        }
        return c;
    }

    public static ServiceConfigurationDTO fromMap(Map conf) {
        ServiceConfigurationDTO c = new ServiceConfigurationDTO();
        Log.d("SERVICE_MAP","fromMap");
        if (conf.containsKey("ua")) {
            c.ua = conf.get("ua").toString();
        }

        if (conf.containsKey("stun")) {
            c.stun = (ArrayList) conf.get("stun");
        }

        if(conf.containsKey("notifications")) {


            c.setNotificationsConfig(  (HashMap<String, Object>) conf.get("notifications"));
        }

        if (conf.containsKey("enableSilentMode")) {
            c.enableSilentMode = (boolean) conf.get("enableSilentMode");
        }

        return c;
    }

    public static ServiceConfigurationDTO fromConfiguration(ReadableMap data) {
        ServiceConfigurationDTO c = new ServiceConfigurationDTO();

        if (data.hasKey("ua")) {
            c.ua = data.getString("ua");
        }

        return c;
    }

    public void updateConfigurationFromIntent(Intent intent) {
        if (intent.hasExtra("ua")) {
            this.ua = intent.getStringExtra("ua");
        }

        if(intent.hasExtra("notifications")) {

            this.setNotificationsConfig( new HashMap<>((Map<String, Object>)intent.getSerializableExtra("notifications")) );

        }

        if(intent.hasExtra("enableSilentMode")) {
            this.enableSilentMode = intent.getBooleanExtra("enableSilentMode", false);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceConfigurationDTO that = (ServiceConfigurationDTO) o;

        return ua != null ? ua.equals(that.ua) : that.ua == null;
    }

    @Override
    public int hashCode() {
        return ua != null ? ua.hashCode() : 0;
    }
}
