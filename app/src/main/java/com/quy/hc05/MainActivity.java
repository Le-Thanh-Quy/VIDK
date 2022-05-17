package com.quy.hc05;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.quy.hc05.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    BluetoothSocket bluetoothSocket;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    MediaPlayer mp;
    private int minTemp = (int) Double.POSITIVE_INFINITY;
    private int maxTemp = (int) Double.POSITIVE_INFINITY * -1;
    private int minHumi = (int) Double.POSITIVE_INFINITY;
    private int maxHumi = (int) Double.POSITIVE_INFINITY * -1;
    int rd1;
    int rd2;
    boolean isSendWarning = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mainLayout.setVisibility(View.GONE);
        binding.layoutConnect.setVisibility(View.VISIBLE);
        AddEvent();
        PlaySound();
        ListHistoryFunc();
    }

    ListHistory listHistory;
    List<History> listData;

    private void ListHistoryFunc() {
        listData = new ArrayList<>();
        listHistory = new ListHistory(this, listData);
        binding.listHistory.setAdapter(listHistory);

        binding.addHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyy  HH:mm aa").format(Calendar.getInstance().getTime());
                listData.add(new History(timeStamp, String.valueOf(rd1), String.valueOf(rd2), String.valueOf(maxTemp), String.valueOf(minTemp), String.valueOf(maxHumi), String.valueOf(minHumi)));
                listHistory.notifyDataSetChanged();
            }
        });
    }

    private void AddEvent() {

        binding.btnUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendData(1);
                        EnableButton(1);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendData(0);
                        EnableButton(0);
                        break;
                }
                return true;
            }
        });

        binding.btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendData(2);
                        EnableButton(2);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendData(0);
                        EnableButton(0);
                        break;
                }
                return true;
            }
        });

        binding.btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendData(3);
                        EnableButton(3);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendData(0);
                        EnableButton(0);
                        break;
                }
                return true;
            }
        });

        binding.btnDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendData(4);
                        EnableButton(4);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendData(0);
                        EnableButton(0);
                        break;
                }
                return true;
            }
        });

        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnConnect.setEnabled(false);
                ConnectBlueTooth();
            }
        });
    }

    private void EnableButton(int state) {
        switch (state) {
            case 0:
                binding.btnDown.setEnabled(true);
                binding.btnUp.setEnabled(true);
                binding.btnLeft.setEnabled(true);
                binding.btnRight.setEnabled(true);
                break;
            case 1:
                binding.btnDown.setEnabled(false);
                binding.btnRight.setEnabled(false);
                binding.btnLeft.setEnabled(false);
                break;
            case 2:
                binding.btnDown.setEnabled(false);
                binding.btnUp.setEnabled(false);
                binding.btnRight.setEnabled(false);
                break;
            case 3:
                binding.btnDown.setEnabled(false);
                binding.btnUp.setEnabled(false);
                binding.btnLeft.setEnabled(false);
                break;
            case 4:
                binding.btnRight.setEnabled(false);
                binding.btnUp.setEnabled(false);
                binding.btnLeft.setEnabled(false);
                break;

        }
    }

    private void ConnectBlueTooth() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(btAdapter.getBondedDevices());

        BluetoothDevice hc05 = btAdapter.getRemoteDevice("00:21:09:01:15:95");
        System.out.println(hc05.getName());

        bluetoothSocket = null;
        try {
            bluetoothSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
            System.out.println(bluetoothSocket);
            bluetoothSocket.connect();
            System.out.println(bluetoothSocket.isConnected());
            ReceivedData();
            Toast.makeText(this, "Kết nối thành công!", Toast.LENGTH_SHORT).show();
            binding.btnConnect.setEnabled(true);
            binding.btnConnect.setVisibility(View.INVISIBLE);
            binding.layoutConnect.setVisibility(View.INVISIBLE);
            binding.mainLayout.setVisibility(View.VISIBLE);
            mp.stop();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Kết nối không thành công!", Toast.LENGTH_SHORT).show();
            binding.btnConnect.setEnabled(true);
            binding.btnConnect.setVisibility(View.VISIBLE);
        }
    }

    private void SendData(int sendData) {
        try {
            OutputStream outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(sendData);
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.btnConnect.setVisibility(View.VISIBLE);
                    binding.layoutConnect.setVisibility(View.VISIBLE);
                }
            });
            e.printStackTrace();
            PlaySound();
        }
    }


    private void ReceivedData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!bluetoothSocket.isConnected()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.btnConnect.setVisibility(View.VISIBLE);
                                binding.layoutConnect.setVisibility(View.VISIBLE);
                            }
                        });
                        PlaySound();
                        break;
                    } else {
                        InputStream inputStream = null;
                        try {
                            inputStream = bluetoothSocket.getInputStream();
                            BufferedReader br;
                            br = new BufferedReader(new InputStreamReader(inputStream));
                            try {
                                String resp = br.readLine();
                                String[] arr = resp.split(" ");

                                rd1 = Integer.parseInt(arr[0]);
                                binding.temperature.setText(String.valueOf(rd1));
                                if (minTemp > rd1) {
                                    minTemp = rd1;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.tvMinTemperature.setText(minTemp + "℃");
                                        }
                                    });
                                }
                                if (maxTemp < rd1) {
                                    maxTemp = rd1;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.tvMaxTemperature.setText(maxTemp + "℃");
                                        }
                                    });
                                }
                                rd2 = Integer.parseInt(arr[1]);
                                binding.humidity.setText(String.valueOf(rd2));
                                if (minHumi > rd2) {
                                    minHumi = rd2;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.tvMinHumidity.setText(minHumi + "%");
                                        }
                                    });
                                }
                                if (maxHumi < rd2) {
                                    maxHumi = rd2;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.tvMaxHumidity.setText(maxHumi + "%");
                                        }
                                    });
                                }
                                int rd3 = Integer.parseInt(arr[2]);

                                if (rd3 == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.btnUp.setVisibility(View.INVISIBLE);
                                            binding.warning.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    if (isSendWarning) {
                                        isSendWarning = false;
                                        SendData(0);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                EnableButton(0);
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.btnUp.setVisibility(View.VISIBLE);
                                            binding.warning.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                    isSendWarning = true;
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        } catch (IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.btnConnect.setVisibility(View.VISIBLE);
                                    binding.layoutConnect.setVisibility(View.VISIBLE);
                                }
                            });
                            e.printStackTrace();
                            PlaySound();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void PlaySound() {
        mp = MediaPlayer.create(MainActivity.this, R.raw.sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                mp = null;
            }
        });
        mp.setLooping(true);
        mp.start();
    }

}

