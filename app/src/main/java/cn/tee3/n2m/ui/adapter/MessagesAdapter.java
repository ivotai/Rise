package cn.tee3.n2m.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unicorn.rise.R;

import java.util.Collections;
import java.util.List;

import cn.tee3.avd.MChat;

public class MessagesAdapter extends BaseAdapter {
    Context context;
    String selfUserID;
    List<MChat.Message> messageBeans;

    public MessagesAdapter(Context context, String selfID, List<MChat.Message> messageBeans) {
        this.context = context;
        this.selfUserID = selfID;
        this.messageBeans = messageBeans;
        Collections.reverse(this.messageBeans);
    }

    @Override
    public int getCount() {
        return messageBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return messageBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getType(int position) {
        return selfUserID.equalsIgnoreCase(messageBeans.get(position).getFromId()) ? 1 : 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getType(position);
        ViewHolder holder = null;
        MChat.Message messageBean = messageBeans.get(position);
        if (type == 1) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_chat_left, null);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_chat_right, null);
        }
        if (convertView.getTag() == null) {
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvUserName);
            holder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
            holder.ivHead = (ImageView) convertView.findViewById(R.id.ivHead);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (null != holder) {
            holder.tvName.setText(messageBean.getFromName());
            holder.tvMessage.setText(messageBean.getMessage());
        }
        return convertView;
    }

    public void refresh(List<MChat.Message> messageBeans) {
        this.messageBeans = messageBeans;
        Collections.reverse(this.messageBeans);
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tvName;
        ImageView ivHead;
        TextView tvMessage;
    }
}
