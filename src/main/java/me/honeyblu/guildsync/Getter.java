package me.honeyblu.guildsync;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Getter {

    void updateLiveDataAsync(final Guild guild) {
        guild.getServer().getScheduler().runTaskAsynchronously(guild, () -> {
            JsonArray data = getLiveData();
            if (data == null) {
                System.out.println("Server returned empty data. This may be an API / plugin issue. Have HoneyBlu look into this!");
                return;
            }
            guild.getServer().getScheduler().runTask(guild, () -> {
                guild.data = data;
                guild.forceUpdateAll();
            });
        });
    }

    private JsonArray getLiveData() {
        String url = "https://api.wynncraft.com/public_api.php?action=guildStats&command=Imperial";
        return getJson(url);
    }

    private JsonArray getJson(String url) {
        try {
            String URL = http(url);
            JsonObject jsonObject = new Gson().fromJson(URL, JsonObject.class);
            return jsonObject.get("members").getAsJsonArray();
        } catch (IOException ex) {
            System.out.println("Server returned non JSON data. This may be an API / plugin issue. Have HoneyBlu look into this!");
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