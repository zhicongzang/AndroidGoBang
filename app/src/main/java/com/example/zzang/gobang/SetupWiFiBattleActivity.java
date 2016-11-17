package com.example.zzang.gobang;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zzang.gobang.controls.ButtonTextViewTouchListener;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetupWiFiBattleActivity extends AppCompatActivity {

    private final static int SEARCH_WIFI_GAMES_PORT = 33333;
    private final static int WIFI_GAMES_PORT = 44444;

    private Map<String, WiFiGame> games;
    private List<String> IPAddressList;

    private ListView gameListView;
    private GameListAdapter gameListAdapter;
    private EditText nameEditText;
    private EditText IPAddressEditText;
    private String myIPAddress;
    private String myName = "Player";


    private ServerSocket serverSocket;
    private ExecutorService searchingThreadPool;
    private ExecutorService serverThreadPool;

    private boolean isListening = false;
    private Socket recieveSocket;

    private UpdateListViewHandler handler = new UpdateListViewHandler();

    private Toast toast;

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
        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SetupWiFiBattleActivity.this, WiFiBattleInfoActivity.class);
                WiFiGame game = games.get(IPAddressList.get(position));
                intent.putExtra("Game", game);
                intent.putExtra("IPAddress",myIPAddress);
                intent.putExtra("Name",myName);
                startActivity(intent);
            }
        });

        TextView reloadButton = (TextView) findViewById(R.id.reloadTextView);
        reloadButton.setOnTouchListener(new ButtonTextViewTouchListener() {
            @Override
            protected Resources resources() {
                return getResources();
            }
            @Override
            public void touchUpHandle(View v) {
                searchWiFiGames();
            }
        });
        TextView createGameTextView = (TextView) findViewById(R.id.createGameTextView);
        createGameTextView.setOnTouchListener(new ButtonTextViewTouchListener() {
            @Override
            protected Resources resources() {
                return getResources();
            }

            @Override
            public void touchUpHandle(View v) {
                Intent intent = new Intent(SetupWiFiBattleActivity.this, WiFiBattleInfoActivity.class);
                intent.putExtra("IPAddress",myIPAddress);
                intent.putExtra("Name",myName);
                startActivity(intent);
            }
        });
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.setText(myName);
        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    changeName();
                    return true;
                }

                return true;
            }
        });
        TextView changeNameTextView = (TextView)findViewById(R.id.changeNameTextView);
        changeNameTextView.setOnTouchListener(new ButtonTextViewTouchListener() {
            @Override
            protected Resources resources() {
                return getResources();
            }

            @Override
            public void touchUpHandle(View v) {
                changeName();
            }
        });
        IPAddressEditText = (EditText) findViewById(R.id.IPAddressEditText);
        IPAddressEditText.setText(myIPAddress);
        IPAddressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    searchGame();
                }
                return false;
            }
        });
        TextView searchTextView = (TextView)findViewById(R.id.searchTextView);
        searchTextView.setOnTouchListener(new ButtonTextViewTouchListener() {
            @Override
            protected Resources resources() {
                return getResources();
            }

            @Override
            public void touchUpHandle(View v) {
                searchGame();
            }
        });


        searchingThreadPool = Executors.newFixedThreadPool(10);
        serverThreadPool = Executors.newFixedThreadPool(2);

        try {
            serverSocket = new ServerSocket(SEARCH_WIFI_GAMES_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }

        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,250);
        startSearchingServer();
        searchWiFiGames();
    }

    private void searchWiFiGames() {
        StopSearchingWiFiGames();
        searchingThreadPool = Executors.newFixedThreadPool(10);
        toast.setText("Searching...");
        toast.show();
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
                        Socket socket = new Socket(searchIP, WIFI_GAMES_PORT);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(myIPAddress);
                    } catch (IOException e) { }
                }
            });
        }
    }

    private void startSearchingServer() {
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
                        WiFiGame game = JSON.parseObject((String) ois.readObject(), WiFiGame.class);
                        Log.d("WIFI Game", JSON.toJSONString(game));
                        if (!games.containsKey(game.getBlackIPAddress())) {
                            IPAddressList.add(game.getBlackIPAddress());
                        }
                        games.put(game.getBlackIPAddress(), game);
                        handler.sendMessage(new Message());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void stopSearchingServer() {
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
            serverThreadPool.shutdown();
            searchingThreadPool.shutdown();
            e.printStackTrace();
        }
    }

    private void StopSearchingWiFiGames() {
        searchingThreadPool.shutdownNow();
    }

    @Override
    public void finish() {
        stopSearchingServer();
        super.finish();
    }

    @Override
    protected void onStart() {
        searchWiFiGames();
        super.onStart();
    }

    @Override
    protected void onPause() {
        StopSearchingWiFiGames();
        super.onPause();
    }

    private void changeName() {
        String name = nameEditText.getText().toString();
        if (name.length() <= 6) {
            myName = name;
            toast.setText("Name changed.");
            toast.show();
        } else {
            nameEditText.setText(myName);
            toast.setText("Name must no longer than 6 chars..");
            toast.show();
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
    }

    private void searchGame() {
        final String IPAddress = IPAddressEditText.getText().toString();
        if (IPAddress.equals(myIPAddress)) {
            toast.setText("This is your IP Address.");
            toast.show();

        } else if (SetupWiFiBattleActivity.isIP(IPAddress)) {
            StopSearchingWiFiGames();
            searchingThreadPool = Executors.newFixedThreadPool(10);
            searchingThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket(IPAddress, WIFI_GAMES_PORT);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(myIPAddress);
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toast.setText("Cannot find game.");
                                toast.show();
                            }
                        });
                    }
                    while(!games.containsKey(IPAddress)){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) { }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SetupWiFiBattleActivity.this, WiFiBattleInfoActivity.class);
                            intent.putExtra("Game", games.get(IPAddress));
                            intent.putExtra("IPAddress",myIPAddress);
                            intent.putExtra("Name",myName);
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            IPAddressEditText.setText(myIPAddress);
            toast.setText("Invalid IP Address.");
            toast.show();
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(IPAddressEditText.getWindowToken(), 0);


    }

    private static boolean isIP(String addr) {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();
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
            nameTextView.setText(games.get(IPAddress).getBlackPlayerName());
            IPAddressTextView.setText(IPAddress);
            return row;
        }


    }


}
