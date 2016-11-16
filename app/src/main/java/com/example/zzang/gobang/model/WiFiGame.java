package com.example.zzang.gobang.model;

import java.io.Serializable;

/**
 * Created by zzc on 11/16/16.
 */

public class WiFiGame implements Serializable {
    private String gameName;
    private String IPAddress;
    private String playerName;

    public WiFiGame(String playerName, String gameName, String IPAddress) {
        this.playerName = playerName;
        this.gameName = gameName;
        this.IPAddress = IPAddress;
    }

    public  WiFiGame(String data) {
        String[] ss = data.split(":");
        gameName = ss[1];
        IPAddress = ss[3];
        playerName = ss[5];
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getGameName() {
        return gameName;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return "Game Name: " + gameName + " IP Address: " + IPAddress + " Player Name: " + playerName;
    }

    public String toDataString() {
        return "Game Name:" + gameName + ":IP Address:" + IPAddress + ":Player Name:" + playerName;
    }
}
