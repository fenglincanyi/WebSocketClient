package com.heytap.wearable.oms.demo.phone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.heytap.wearable.oms.MessageClient;
import com.heytap.wearable.oms.Node;
import com.heytap.wearable.oms.NodeClient;
import com.heytap.wearable.oms.Wearable;

public class MainActivity extends AppCompatActivity {
    private static final String STRING_NULL = "NULL";

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

    /** 节点管理器 */
    private NodeClient nodeClient;
    /** 信息管理器 */
    private MessageClient messageClient;

    /** 防止过快点击 */
    private long lastClickTime;
    /** 点前连接的节点 */
    private Node node;

    /** 节点监听器 */
    private NodeClient.OnNodeChangedListener onNodeChangedListener;
    /** 信息监听器 */
    private MessageClient.OnMessageReceivedListener onMessageReceivedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_main);

        initUi();
        initWearable();
    }

    private void initUi() {
        this.statusTextView = findViewById(R.id.status);
        this.nodeTextView = findViewById(R.id.node);
        this.receivedPathTextView = findViewById(R.id.received_path);
        this.receivedDataTextView = findViewById(R.id.received_data);
        Button copyButton = findViewById(R.id.copy);

        this.pathTextView = findViewById(R.id.path);
        this.dataTextView = findViewById(R.id.data);
        this.codeTextView = findViewById(R.id.code);
        this.messageTextView = findViewById(R.id.message);
        this.sendButton = findViewById(R.id.send);
        this.sendButton.setEnabled(false);

        //将对方发送过来的信息复制到发送栏，手表太小，不方便输入
        copyButton.setOnClickListener(v -> {
            this.pathTextView.setText(this.receivedPathTextView.getText());
            this.dataTextView.setText(this.receivedDataTextView.getText());
        });

        //发送信息
        this.sendButton.setOnClickListener(v -> {
            if (Math.abs(System.currentTimeMillis() - this.lastClickTime) < 500L) {
                return;
            }
            this.lastClickTime = System.currentTimeMillis();

            if (this.node != null) {
                this.sendButton.setEnabled(false);
                this.sendButton.setText("SENDING");

                final String path = pathTextView.getText().toString();
                final String dataString = dataTextView.getText().toString();
                final byte[] data = TextUtils.isEmpty(dataString) ? null : dataString.getBytes();

                //发送信息，并将结果显示到UI上
                this.messageClient.sendMessage(this.node.getId(), path, data).setResultCallback(result -> {
                    this.codeTextView.setText(String.valueOf(result.getStatus().getStatusCode()));
                    this.messageTextView.setText(String.valueOf(result.getStatus().getStatusMessage()));
                    this.sendButton.setEnabled(true);
                    this.sendButton.setText("SEND");
                });
            }
        });
    }

    private void initWearable() {
        this.nodeClient = Wearable.getNodeClient(getApplicationContext());
        this.messageClient = Wearable.getMessageClient(getApplicationContext());

        //设置节点监听器
        this.onNodeChangedListener = new NodeClient.OnNodeChangedListener() {
            @Override
            public void onPeerConnected(Node node) {
                MainActivity.this.node = node;

                MainActivity.this.statusTextView.setText("CONNECTED");
                MainActivity.this.nodeTextView.setText(node.getId());
                MainActivity.this.sendButton.setEnabled(true);
            }

            @Override
            public void onPeerDisconnected(Node node) {
                MainActivity.this.node = null;

                MainActivity.this.statusTextView.setText(STRING_NULL);
                MainActivity.this.nodeTextView.setText(STRING_NULL);
                MainActivity.this.sendButton.setEnabled(false);
            }
        };
        this.nodeClient.addListener(this.onNodeChangedListener);

        //设置信息监听器
        this.onMessageReceivedListener = messageEvent -> {
            this.receivedPathTextView.setText(messageEvent.getPath());

            final byte[] data = messageEvent.getData();
            this.receivedDataTextView.setText(data == null || data.length == 0 ? "NULL" : new String(messageEvent.getData()));
        };
        this.messageClient.addListener(this.onMessageReceivedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.nodeClient.removeListener(this.onNodeChangedListener);
        this.messageClient.removeListener(this.onMessageReceivedListener);
    }
}