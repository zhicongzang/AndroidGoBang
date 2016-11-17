package com.example.zzang.gobang.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by zzc on 11/16/16.
 */

public class WiFiGame implements Parcelable {
    private String blackIPAddress;
    private String blackPlayerName;
    private String whiteIPAddress;
    private String whitePlayerName;

    public WiFiGame() { }

    public WiFiGame(String blackIPAddress, String blackPlayerName, String whiteIPAddress, String whitePlayerName) {
        this.blackIPAddress = blackIPAddress;
        this.blackPlayerName = blackPlayerName;
        this.whiteIPAddress = whiteIPAddress;
        this.whitePlayerName = whitePlayerName;
    }

    public WiFiGame(String blackIPAddress, String blackPlayerName) {
        this.blackIPAddress = blackIPAddress;
        this.blackPlayerName = blackPlayerName;
    }

    protected WiFiGame(Parcel in) {
        blackIPAddress = in.readString();
        blackPlayerName = in.readString();
        whiteIPAddress = in.readString();
        whitePlayerName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(blackIPAddress);
        dest.writeString(blackPlayerName);
        dest.writeString(whiteIPAddress);
        dest.writeString(whitePlayerName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WiFiGame> CREATOR = new Creator<WiFiGame>() {
        @Override
        public WiFiGame createFromParcel(Parcel in) {
            return new WiFiGame(in);
        }

        @Override
        public WiFiGame[] newArray(int size) {
            return new WiFiGame[size];
        }
    };

    public String getBlackIPAddress() {
        return blackIPAddress;
    }

    public void setBlackIPAddress(String blackIPAddress) {
        this.blackIPAddress = blackIPAddress;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName = blackPlayerName;
    }

    public String getWhiteIPAddress() {
        return whiteIPAddress;
    }

    public void setWhiteIPAddress(String whiteIPAddress) {
        this.whiteIPAddress = whiteIPAddress;
    }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName = whitePlayerName;
    }

    @Override
    public String toString() {
        return "WiFiGame{" +
                "blackIPAddress='" + blackIPAddress + '\'' +
                ", blackPlayerName='" + blackPlayerName + '\'' +
                ", whiteIPAddress='" + whiteIPAddress + '\'' +
                ", whitePlayerName='" + whitePlayerName + '\'' +
                '}';
    }
}
