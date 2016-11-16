package com.example.zzang.gobang;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zzang.gobang.model.WiFiGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SetupWiFiBattleActivity extends AppCompatActivity {

    private final static int SEARCH_WIFI_GAMES_PORT = 33333;

    private Map<String, WiFiGame> games;
    private List<String> IPAddressList;

    private ListView gameListView;
    private GameListAdapter gameListAdapter;
    private String myIPAddress;


    private ServerSocket serverSocket;
    private ExecutorService searchingThreadPool;
    private ExecutorService serverThreadPool;

    private boolean isListening = false;
    private Socket recieveSocket;

    private UpdateListViewHandler handler = new UpdateListViewHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wifi_battle);
        games = new HashMap<>();
        IPAddressList = new ArrayList<>();

        myIPAddress = getIntent().getStringExtra("IPAddress");

        gameListView = (ListView) findViewById(R.id.gameListView);
        gameListAdapter = new GameListAdapter();
        gameListView.setAdapter(gameListAdapter);
        searchingThreadPool = Executors.newFixedThreadPool(5);
        serverThreadPool = Executors.newFixedThreadPool(2);
        try {
            serverSocket = new ServerSocket(SEARCH_WIFI_GAMES_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        startGameServer();
        searchWiFiGames();
    }

    private void searchWiFiGames() {
        String searchIPPrefix = myIPAddress.substring(0, myIPAddress.lastIndexOf(".")+1);
        for(int suffix=100; suffix<=225; suffix++) {
            final String searchIP = searchIPPrefix + String.valueOf(suffix);
            if (searchIP.equals(myIPAddress)) {
                continue;
            }
            searchingThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket(searchIP, SEARCH_WIFI_GAMES_PORT);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(myIPAddress);
                    } catch (IOException e) { }
                }
            });
        }
    }

    private void startGameServer() {
        if (isListening) {
            return;
        }
        isListening = true;
        serverThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while(isListening) {
                    try {
                        recieveSocket = serverSocket.accept();
                        ObjectInputStream ois = new ObjectInputStream(recieveSocket.getInputStream());
                        WiFiGame game = new WiFiGame((String) ois.readObject());
                        Log.d("WIFI Game", game.toString());
                        if (!games.containsKey(game.getIPAddress())) {
                            IPAddressList.add(game.getIPAddress());
                        }
                        games.put(game.getIPAddress(), game);
                        handler.sendMessage(new Message());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void stopGameServer() {
        if (!isListening) {
            return;
        }
        isListening = false;
        try {
            if(recieveSocket != null) {
                recieveSocket.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        stopGameServer();
        super.finish();
    }



    class UpdateListViewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            gameListAdapter.notifyDataSetChanged();
            super.handleMessage(msg);
        }
    }

    class GameListAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        @Override
        public int getCount() {
            return IPAddressList.size();
        }

        @Override
        public Object getItem(int position) {
            return games.get(IPAddressList.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            if ((convertView == null)) {
                if (inflater == null) inflater = (LayoutInflater) SetupWiFiBattleActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.game_list_item, parent, false);
            }

            TextView nameTextView = (TextView) row.findViewById(R.id.nameTextView);
            TextView IPAddressTextView = (TextView) row.findViewById(R.id.IPAddressTextView);

            String IPAddress = IPAddressList.get(position);
            nameTextView.setText(games.get(IPAddress).getGameName());
            IPAddressTextView.setText(IPAddress);
            return row;
        }
    }
}
