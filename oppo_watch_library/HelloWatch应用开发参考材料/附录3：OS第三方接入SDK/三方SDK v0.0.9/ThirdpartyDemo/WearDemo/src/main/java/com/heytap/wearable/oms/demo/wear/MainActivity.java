package com.heytap.wearable.oms.demo.wear;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.heytap.wearable.oms.CapabilityClient;
import com.heytap.wearable.oms.MessageClient;
import com.heytap.wearable.oms.Node;
import com.heytap.wearable.oms.NodeClient;
import com.heytap.wearable.oms.SportClient;
import com.heytap.wearable.oms.Wearable;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String STRING_NULL = "NULL";

    /** 当前是否为蓝牙网络 */
    private TextView networkTextView;
    /** 心率 */
    private TextView heartRateTextView;
    /** 步数 */
    private TextView stepTextView;

    /** 连接的状态 */
    private TextView statusTextView;
    /** 连接的nodeId */
    private TextView nodeTextView;
    /** 收到对方发信息的path */
    private TextView receivedPathTextView;
    /** 收到对方发信息的body */
    private TextView receivedDataTextView;

    /** 发送信息的path */
    private TextView pathTextView;
    /** 发送信息的body */
    private TextView dataTextView;
    /** 发送信息回调的状态码 */
    private TextView codeTextView;
    /** 发送信息回调的状态 */
    private TextView messageTextView;

    /** 发送信息 */
    private Button sendButton;

    /** 检查手机是否安装应用结果 */
    private TextView checkInstalledTextView;
    /** 检查手机是否安装应用 */
    private Button checkInstalledButton;

    /** 打开应用市场结果 */
    private TextView marketTextView;
    /** 打开应用市场 */
    private Button marketButton;

    /** 打开手机应用结果 */
    private TextView appTextView;
    /** 打开手机应用 */
    private Button appButton;

    /** 要找开的地址 */
    private TextView browserVTextView;
    /** 打开浏览器结果 */
    private TextView browserRTextView;
    /** 打开浏览器 */
    private Button browserButton;

    /** 节点管理器 */
    private NodeClient nodeClient;
    /** 信息管理器 */
    private MessageClient messageClient;
    /** 手机能力管理器 */
    private CapabilityClient capabilityClient;
    /** 运动健康管理器 */
    private SportClient sportClient;

    /** 防止过快点击 */
    private long lastClickTime;
    /** 点前连接的节点 */
    private Node node;

    /** 节点监听器 */
    private NodeClient.OnNodeChangedListener onNodeChangedListener;
    /** 信息监听器 */
    private MessageClient.OnMessageReceivedListener onMessageReceivedListener;
    /** 蓝牙网络监听器 */
    private CapabilityClient.OnBluetoothNetProxyChangedListener onBluetoothNetProxyChangedListener;
    /** 运动健康回调器 */
    private SportClient.OnHeartRateChangedListener2 onHeartRateChangedListener;
    /** 步数回调器 */
    private SportClient.OnDailyActivityChangedListener onDailyActivityChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_main);

        initUi();
        initWearable();
    }

    private void initUi() {
        this.networkTextView = findViewById(R.id.network);
        this.heartRateTextView = findViewById(R.id.heartRate);
        this.stepTextView = findViewById(R.id.step);
        this.statusTextView = findViewById(R.id.status);
        this.nodeTextView = findViewById(R.id.node);
        this.receivedPathTextView = findViewById(R.id.received_path);
        this.receivedDataTextView = findViewById(R.id.received_data);

        //将对方发送过来的信息复制到发送栏，手表太小，不方便输入
        findViewById(R.id.copy).setOnClickListener(v -> {
            this.pathTextView.setText(this.receivedPathTextView.getText());
            this.dataTextView.setText(this.receivedDataTextView.getText());
        });

        //将对方发送过来的信息复制到browserVTextView，手表太小，不方便输入
        findViewById(R.id.copy_browser).setOnClickListener(v -> {
            this.browserVTextView.setText(this.receivedDataTextView.getText());
        });

        this.pathTextView = findViewById(R.id.path);
        this.dataTextView = findViewById(R.id.data);
        this.codeTextView = findViewById(R.id.code);
        this.messageTextView = findViewById(R.id.message);

        //发送信息
        this.sendButton = findViewById(R.id.send);
        this.sendButton.setEnabled(false);
        this.sendButton.setOnClickListener(v -> {
            if (Math.abs(System.currentTimeMillis() - this.lastClickTime) < 500L) {
                return;
            }
            this.lastClickTime = System.currentTimeMillis();

            if (this.node != null) {
                this.sendButton.setEnabled(false);
                this.sendButton.setText("SENDING");

                final String path = this.pathTextView.getText().toString();
                final String dataString = this.dataTextView.getText().toString();
                final byte[] data = TextUtils.isEmpty(dataString) ? null : dataString.getBytes();

                this.messageClient.sendMessage(this.node.getId(), path, data).setResultCallback(result -> {
                    this.codeTextView.setText(String.valueOf(result.getStatus().getStatusCode()));
                    this.messageTextView.setText(String.valueOf(result.getStatus().getStatusMessage()));
                    this.sendButton.setEnabled(true);
                    this.sendButton.setText("SEND");
                });
            }
        });

        //检查手机是否安装应用
        this.checkInstalledTextView = findViewById(R.id.check_installed_result);
        this.checkInstalledButton = findViewById(R.id.check_installed);
        this.checkInstalledButton.setEnabled(false);
        this.checkInstalledButton.setOnClickListener(v -> {
            if (Math.abs(System.currentTimeMillis() - this.lastClickTime) < 500L) {
                return;
            }
            this.lastClickTime = System.currentTimeMillis();
            this.checkInstalledButton.setEnabled(false);
            this.checkInstalledButton.setText("CHECKING");

            this.capabilityClient.checkInstalled(node.getId()).setResultCallback(result -> {
                this.checkInstalledButton.setEnabled(true);
                this.checkInstalledButton.setText("CHECK");

                if (result.isSuccess()) {
                    this.checkInstalledTextView.setText("INSTALLED");
                } else {
                    this.checkInstalledTextView.setText(result.getStatusMessage());
                }
            });
        });

        //打开应用市场
        this.marketTextView = findViewById(R.id.market_result);
        this.marketButton = findViewById(R.id.market);
        this.marketButton.setEnabled(false);
        this.marketButton.setOnClickListener(v -> {
            if (Math.abs(System.currentTimeMillis() - lastClickTime) < 500L) {
                return;
            }
            this.lastClickTime = System.currentTimeMillis();
            this.marketButton.setEnabled(false);
            this.marketButton.setText("OPENING");

            this.capabilityClient.tryInstall(node.getId()).setResultCallback(result -> {
                this.marketButton.setEnabled(true);
                this.marketButton.setText("OPEN");
                this.marketTextView.setText(result.getStatusMessage());
            });
        });

        //打开应用
        this.appTextView = findViewById(R.id.app_result);
        this.appButton = findViewById(R.id.app);
        this.appButton.setEnabled(false);
        this.appButton.setOnClickListener(v -> {
            if (Math.abs(System.currentTimeMillis() - this.lastClickTime) < 500L) {
                return;
            }
            this.lastClickTime = System.currentTimeMillis();
            this.appButton.setEnabled(false);
            this.appButton.setText("OPENING");

            this.capabilityClient.tryAwaken(this.node.getId(), "com.heytap.oms.phone.demo.Test", null).setResultCallback(result -> {
                this.appButton.setEnabled(true);
                this.appButton.setText("OPEN");
                this.appTextView.setText(result.getStatusMessage());
            });
        });

        //打开浏览器
        this.browserVTextView = findViewById(R.id.browser_value);
        this.browserRTextView = findViewById(R.id.browser_result);
        this.browserButton = findViewById(R.id.browser);
        this.browserButton.setEnabled(false);
        this.browserButton.setOnClickListener(v -> {
            if (Math.abs(System.currentTimeMillis() - this.lastClickTime) < 500L) {
                return;
            }
            this.lastClickTime = System.currentTimeMillis();
            String url = this.browserVTextView.getText().toString().toLowerCase(Locale.US);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                this.browserRTextView.setText("ERROR URL");
                return;
            }

            this.browserButton.setEnabled(false);
            this.browserButton.setText("OPENING");

            this.capabilityClient.tryOpenUrl(node.getId(), url).setResultCallback(result -> {
                this.browserButton.setEnabled(true);
                this.browserButton.setText("OPEN");
                this.browserRTextView.setText(result.getStatusMessage());
            });
        });
    }

    private void initWearable() {
        this.nodeClient = Wearable.getNodeClient(getApplicationContext());
        this.messageClient = Wearable.getMessageClient(getApplicationContext());
        this.capabilityClient = Wearable.getCapabilityClient(getApplicationContext());
        this.sportClient = Wearable.getSportClient(getApplicationContext());

        //设置节点监听器
        this.onNodeChangedListener = new NodeClient.OnNodeChangedListener() {
            @Override
            public void onPeerConnected(Node node) {
                MainActivity.this.node = node;

                MainActivity.this.statusTextView.setText("CONNECTED");
                MainActivity.this.nodeTextView.setText(node.getId());
                MainActivity.this.sendButton.setEnabled(true);
                MainActivity.this.checkInstalledButton.setEnabled(true);
                MainActivity.this.marketButton.setEnabled(true);
                MainActivity.this.appButton.setEnabled(true);
                MainActivity.this.browserButton.setEnabled(true);
            }

            @Override
            public void onPeerDisconnected(Node node) {
                MainActivity.this.node = null;

                MainActivity.this.statusTextView.setText(STRING_NULL);
                MainActivity.this.nodeTextView.setText(STRING_NULL);
                MainActivity.this.sendButton.setEnabled(false);
                MainActivity.this.checkInstalledButton.setEnabled(false);
                MainActivity.this.marketButton.setEnabled(false);
                MainActivity.this.appButton.setEnabled(false);
                MainActivity.this.browserButton.setEnabled(false);
            }
        };
        this.nodeClient.addListener(onNodeChangedListener);

        //设置信息监听器
        this.onMessageReceivedListener = messageEvent -> {
            this.receivedPathTextView.setText(messageEvent.getPath());

            final byte[] data = messageEvent.getData();
            this.receivedDataTextView.setText(data == null || data.length == 0 ? "NULL" : new String(messageEvent.getData()));
        };
        this.messageClient.addListener(this.onMessageReceivedListener);

        //设置蓝牙网络监听器
        this.onBluetoothNetProxyChangedListener = enable ->
                this.networkTextView.setText(enable ? "Bluetooth" : "Normal");
        this.capabilityClient.addBluetoothNetProxyChangedListener(onBluetoothNetProxyChangedListener);

        this.onHeartRateChangedListener = (hearRateEvent) ->
                this.heartRateTextView.setText(String.valueOf(hearRateEvent.value()));
        this.sportClient.addHeartRateListener(this.onHeartRateChangedListener, 5);

        this.onDailyActivityChangedListener = (stepEvent) ->
                this.stepTextView.setText(String.valueOf(stepEvent.step()));
        this.sportClient.addDailyActivityListener(onDailyActivityChangedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.nodeClient.removeListener(this.onNodeChangedListener);
        this.messageClient.removeListener(this.onMessageReceivedListener);

        this.capabilityClient.removeBluetoothNetProxyChangedListener(this.onBluetoothNetProxyChangedListener);
        this.sportClient.removeHeartRateListener(this.onHeartRateChangedListener);
        this.sportClient.removeDailyActivityListener(this.onDailyActivityChangedListener);
    }
}
