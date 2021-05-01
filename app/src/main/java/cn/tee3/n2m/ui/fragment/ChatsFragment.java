package cn.tee3.n2m.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.unicorn.rise.R;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.tee3.avd.MChat;
import cn.tee3.avd.Room;
import cn.tee3.n2m.ui.adapter.MessagesAdapter;
import cn.tee3.n2m.ui.util.N2MSetting;
import cn.tee3.n2m.ui.util.TextViewUtil;
import cn.tee3.n2m.ui.util.ToastUtil;

public class ChatsFragment extends Fragment implements View.OnClickListener {
    RelativeLayout layout;
    EditText etSend;
    Button btnSend;
    ListView lvChat;
    MessagesAdapter adapter;
    MChat mchat;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initChats();
        if (null == this.layout) {
            this.layout = (RelativeLayout) inflater.inflate(R.layout.fragment_chats, null);
            this.etSend = (EditText) this.layout.findViewById(R.id.etSend);
            this.btnSend = (Button) this.layout.findViewById(R.id.btnSend);
            this.lvChat = (ListView) this.layout.findViewById(R.id.lvChat);
            this.btnSend.setOnClickListener(this);

            this.adapter = new MessagesAdapter(
                    getActivity(), mchat.getUserManager().getSelfUserId(), mchat.getPublicHistoryMessage(true, 0, 20));
            this.lvChat.setAdapter(adapter);
        } else {
            ViewGroup vg = (ViewGroup) this.layout.getParent();
            if (vg != null) {
                vg.removeAllViewsInLayout();
            }
        }
        return this.layout;
    }

    private void initChats() {
        if (null != this.mchat) {
            return;
        }

        Room room = Room.obtain(N2MSetting.getInstance().getRoomId());
        this.mchat = MChat.getChat(room);
        this.mchat.setListener(new MChat.Listener() {
            @Override
            public void onPublicMessage(MChat.Message message) {
                if (!isResumed()) {
                } else {
                    refreshChatList();
                }
            }

            @Override
            public void onPrivateMessage(MChat.Message message) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (R.id.btnSend == v.getId()) {
            sendMessage();
        }
    }

    private void sendMessage() {
        if (!TextViewUtil.isNullOrEmpty(etSend)) {
            ToastUtil.showToast(getActivity(), R.string.noSendMessage);
            return;
        }
        String message = "";
        try {
            message = new String(etSend.getText().toString().getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mchat.sendPublicMessage(message);
        refreshChatList();
        etSend.setText("");
    }

    private void refreshChatList() {
        List<MChat.Message> msgs = mchat.getPublicHistoryMessage(true, 0, 20);
        adapter.refresh(msgs);
        lvChat.smoothScrollToPosition(adapter.getCount() - 1);
    }
}
