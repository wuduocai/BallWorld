package com.ballworld.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.ballworld.entity.ChatMessage;
import com.ballworld.entity.ChatMessageAdapter;
import com.ballworld.util.HttpUtils;
import com.ballworld.util.Translate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmartChatActivity extends Activity {

    //是否用文言文
    CheckBox wyw;
    private ListView mMsgs;
    private ChatMessageAdapter mAdapter;//填充listview
    private List<ChatMessage> mDatas;//adapter内的数据
    private EditText mInputMsg;
    private ImageButton speak;
    private Button mSendMsg;//发消息控件

    //百度语音识别对话框
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mDialogListener = null;
    //应用授权信息 ，这里使用了官方SDK中的参数，如果需要，请自行申请，并修改为自己的授权信息
    private String API_KEY = "kCzxL7IWZtbmuyZ0TMCBfpZY";
    private String SECRET_KEY = "012c3374ffc0670cc07d64ccda7f1a16";

    //获取发送的信息
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 获取message
            ChatMessage fromMessge = (ChatMessage) msg.obj;
            mDatas.add(fromMessge);
            mAdapter.notifyDataSetChanged();
            mMsgs.setSelection(mDatas.size() - 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_samrt_chat);
        //获取CheckBox实例
        wyw = (CheckBox) this.findViewById(R.id.if_wyw);
        //初始化百度监听
        //initBaiduListener();
        //初始化控件
        initView();
        //初始化数据
        initDatas();
        // 初始化监听
        initListener();
    }

    /**
     * 初始化百度语音识别
     */
    public void initBaiduListener() {
        //初始化百度语音识别
        if (mDialog == null) {
            Bundle params = new Bundle();
            //设置API_KEY, SECRET_KEY
            params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);
            params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);
            //设置语音识别对话框为蓝色高亮主题
            params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);
            //实例化百度语音识别对话框
            mDialog = new BaiduASRDigitalDialog(this, params);
            //设置百度语音识别回调接口
            mDialogListener = new DialogRecognitionListener() {

                @Override
                public void onResults(Bundle mResults) {
                    ArrayList<String> rs = mResults != null ? mResults.getStringArrayList(RESULTS_RECOGNITION) : null;
                    if (rs != null && rs.size() > 0) {
                        //InputBox.setText(rs.get(0));
                        Toast.makeText(SmartChatActivity.this, rs.get(0),
                                Toast.LENGTH_SHORT).show();
                    }

                }

            };
        }
        mDialog.setDialogRecognitionListener(mDialogListener);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        speak.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SmartChatActivity.this, "正在努力实现中",
                        Toast.LENGTH_SHORT).show();
            }
        });
        mSendMsg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示回答
                showAnswer(mInputMsg.getText().toString());
            }
        });
    }

    /**
     * 初始化s数据
     */
    private void initDatas() {
        mDatas = new ArrayList<ChatMessage>();
        mDatas.add(new ChatMessage("你好，我是萌萌哒客服，有什么可以为您服务", ChatMessage.Type.INCOMING, new Date()));
        mAdapter = new ChatMessageAdapter(this, mDatas);
        mMsgs.setAdapter(mAdapter);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        speak = (ImageButton) findViewById(R.id.talk_msg);
        mMsgs = (ListView) findViewById(R.id.id_listview_msgs);
        mInputMsg = (EditText) findViewById(R.id.id_input_msg);
        mSendMsg = (Button) findViewById(R.id.id_send_msg);
    }

    /**
     * 1.显示问题
     * 2.获得智能机器人的回答,并显示
     */
    public void showAnswer(String question) {
        final String toMsg = question;
        if (TextUtils.isEmpty(toMsg)) {
            Toast.makeText(SmartChatActivity.this, "输入不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ChatMessage toMessage = new ChatMessage();
        toMessage.setDate(new Date());
        toMessage.setMsg(toMsg);
        toMessage.setType(ChatMessage.Type.OUTCOMING);
        mDatas.add(toMessage);
        mAdapter.notifyDataSetChanged();
        mMsgs.setSelection(mDatas.size() - 1);

        mInputMsg.setText("");

        new Thread() {
            public void run() {
                ChatMessage fromMessage = HttpUtils.sendMessage(toMsg);
                if (wyw.isChecked()) {//如果要翻译成文言文
                    fromMessage.setMsg(Translate.translate(fromMessage.getMsg(), Translate.AUTO, Translate.WYW));
                }
                Message m = Message.obtain();
                m.obj = fromMessage;
                mHandler.sendMessage(m);
            }

            ;
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
