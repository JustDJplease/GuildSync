package me.honeyblu.guildsync;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class ApiFetcher {

    private GuildSync guildSync;

    ApiFetcher(GuildSync guildSync) {
        this.guildSync = guildSync;
    }

    void updateLivePlayerDataAsync(final String playername) {
        guildSync.getServer().getScheduler().runTaskAsynchronously(guildSync, () -> {
            JsonObject data = getLivePlayerData(playername);
            if (data == null) {
                guildSync.getLogger().severe("Server returned empty data. This may be an API / plugin issue. Have HoneyBlu look into this!");
                return;
            }
            guildSync.getServer().getScheduler().runTask(guildSync, () -> guildSync.updatePlayer(playername, data));
        });
    }

    void updateLiveGuildDataAsync() {
        guildSync.getServer().getScheduler().runTaskAsynchronously(guildSync, () -> {
            JsonArray data = getLiveGuildData();
            if (data == null) {
                guildSync.getLogger().severe("Server returned empty data. This may be an API / plugin issue. Have HoneyBlu look into this!");
                return;
            }
            guildSync.getServer().getScheduler().runTask(guildSync, () -> {
                guildSync.data = data;
                guildSync.forceUpdateAll();
            });
        });
    }

    private JsonArray getLiveGuildData() {
        String url = guildSync.getConfig().getString("api-request-url");
        return getJson(url);
    }

    private JsonObject getLivePlayerData(String playername) {
        String url = guildSync.getConfig().getString("api-request-player-url").replace("{playername}", playername);
        return getFullJson(url);
    }

    private JsonObject getFullJson(String url) {
        try {
            String URL = http(url);
            return new Gson().fromJson(URL, JsonObject.class);
        } catch (IOException ex) {
            guildSync.getLogger().severe("Server returned non JSON data. This may be an API / plugin issue. Have HoneyBlu look into this!");
            ex.printStackTrace();
            return null;
        }
    }

    private JsonArray getJson(String url) {
        try {
            String URL = http(url);
            JsonObject jsonObject = new Gson().fromJson(URL, JsonObject.class);
            return jsonObject.get("members").getAsJsonArray();
        } catch (IOException ex) {
            guildSync.getLogger().severe("Server returned non JSON data. This may be an API / plugin issue. Have HoneyBlu look into this!");
            ex.printStackTrace();
            return null;
        }
    }

    private String http(String string) throws IOException {
        StringBuilder msg = new StringBuilder();
        URL obj = new URL(string);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "MyAgent");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            if (msg.length() == 0) {
                msg = new StringBuilder(line);
            } else {
                msg.append("\n").append(line);
            }
        }
        in.close();
        return msg.toString();
    }
}
