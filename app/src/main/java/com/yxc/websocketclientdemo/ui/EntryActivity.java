package com.yxc.websocketclientdemo.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yxc.websocketclientdemo.R;
import com.yxc.websocketclientdemo.im.MessageEvent;
import com.yxc.websocketclientdemo.util.SPUtils;
import com.yxc.websocketclientdemo.util.Util;

import org.greenrobot.eventbus.EventBus;

public class EntryActivity extends AppCompatActivity {

    private static final String KEY_WS_SCHEMA = "ws_schema";
    private static final String KEY_WS_HOST = "ws_host";
    private static final String KEY_WS_PORT = "ws_port";

    private TextView tvErrorTip;
    private EditText etWSSchema;
    private EditText etWSHost;
    private EditText etWSport;
    private Button btnConnect;
    private Button btnDisConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        initView();
        initListener();

        etWSHost.requestFocus();
    }

    private void initListener() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String schema = etWSSchema.getText().toString().trim();
                String host = etWSHost.getText().toString().trim();
                String port = etWSport.getText().toString().trim();
                if (TextUtils.isEmpty(schema) || TextUtils.isEmpty(host) || TextUtils.isEmpty(port)){
                    Toast.makeText(EntryActivity.this, "填写不完整", Toast.LENGTH_LONG).show();
                    tvErrorTip.setVisibility(View.VISIBLE);
                    return;
                }
                tvErrorTip.setVisibility(View.GONE);

                Util.ws = schema + host + (port.startsWith(":") ? port : ":" + port);

                startActivity(new Intent(EntryActivity.this, MainActivity.class));

                SPUtils.getDefault()
                        .putString(KEY_WS_SCHEMA, schema)
                        .putString(KEY_WS_HOST, host)
                        .putString(KEY_WS_PORT, port)
                        .commit();
            }
        });
        btnDisConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(true));
            }
        });
    }

    private void initView() {
        tvErrorTip = findViewById(R.id.tvErrorTip);
        etWSSchema = findViewById(R.id.etWSSchema);
        etWSHost = findViewById(R.id.etWSHost);
        etWSport = findViewById(R.id.etWSport);
        btnConnect = findViewById(R.id.btnConnect);
        btnDisConnect = findViewById(R.id.btnDisConnect);

        etWSSchema.setText(SPUtils.getDefault().getString(KEY_WS_SCHEMA, "ws://"));
        etWSHost.setText(SPUtils.getDefault().getString(KEY_WS_HOST));
        etWSport.setText(SPUtils.getDefault().getString(KEY_WS_PORT));
    }
}