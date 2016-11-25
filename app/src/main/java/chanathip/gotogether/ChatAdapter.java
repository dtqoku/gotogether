package chanathip.gotogether;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by neetc on 11/11/2016.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserMessage> userMessages;
    private Context context;
    private View parentView;


    ChatAdapter(Context context, List<UserMessage> dataset, View view) {
        userMessages = dataset;
        this.context = context;
        this.parentView = view;
    }
    public static class ViewHolderSelfMessage extends RecyclerView.ViewHolder {
        TextView Message;
        TextView Messagedetail;
        ImageView check;
        CardView cardView;

        ViewHolderSelfMessage(View view) {
            super(view);

            Message = (TextView) view.findViewById(R.id.txt_chat);
            Messagedetail = (TextView) view.findViewById(R.id.txt_chatdetail);
            check = (ImageView) view.findViewById(R.id.check);
            cardView = (CardView) view.findViewById(R.id.cv);
        }
    }

    public static class ViewHolderOtherMessage extends RecyclerView.ViewHolder {
        TextView Message;
        TextView Messagedetail;
        CardView cardView;

        ViewHolderOtherMessage(View view) {
            super(view);

            Message = (TextView) view.findViewById(R.id.txt_chat);
            Messagedetail = (TextView) view.findViewById(R.id.txt_chatdetail);
            cardView = (CardView) view.findViewById(R.id.cv);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ViewHolderSelfMessage(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_chat_self, parent, false));
            case 1:
                return new ViewHolderOtherMessage(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_char_other, parent, false));
        }
        return new ViewHolderSelfMessage(LayoutInflater.from(context)
                .inflate(R.layout.recycler_row_chat_self, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final UserMessage userMessage = userMessages.get(position);
        if (holder instanceof ChatAdapter.ViewHolderSelfMessage) {
            final ChatAdapter.ViewHolderSelfMessage viewHolderSelfMessage = (ChatAdapter.ViewHolderSelfMessage) holder;

            viewHolderSelfMessage.Message.setText(userMessage.message);
            viewHolderSelfMessage.Messagedetail.setText("send by " + userMessage.sender + "\nat " + userMessage.time);

            viewHolderSelfMessage.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderSelfMessage.Messagedetail.setVisibility(View.VISIBLE);
                }
            });
            viewHolderSelfMessage.Messagedetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderSelfMessage.Messagedetail.setVisibility(View.GONE);
                }
            });

            if(userMessage.isRead()){
                viewHolderSelfMessage.check.setVisibility(View.VISIBLE);
            }
            else {
                viewHolderSelfMessage.check.setVisibility(View.GONE);
            }

        } else if (holder instanceof ChatAdapter.ViewHolderOtherMessage) {
            final ChatAdapter.ViewHolderOtherMessage viewHolderOtherMessage = (ChatAdapter.ViewHolderOtherMessage) holder;

            viewHolderOtherMessage.Message.setText(userMessage.message);
            viewHolderOtherMessage.Messagedetail.setText("send by " + userMessage.sender + "\nat " + userMessage.time);

            viewHolderOtherMessage.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderOtherMessage.Messagedetail.setVisibility(View.VISIBLE);
                }
            });
            viewHolderOtherMessage.Messagedetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderOtherMessage.Messagedetail.setVisibility(View.GONE);
                }
            });

        }
    }
    @Override
    public int getItemViewType(int position) {
        final UserMessage userMessage = userMessages.get(position);
        if (userMessage.Type.equals("self")) {
            return 0;
        } else if (userMessage.Type.equals("notself")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return userMessages.size();
    }
}
