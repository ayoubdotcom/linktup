package victor.forum;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookDialog;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by hp on 2016-07-21.
 */
public class messageReponseAdapter extends RecyclerView.Adapter<messageReponseAdapter.MessageViewHolder> implements OnCompleteListener<Uri>{
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Message> messageList;
    private List<Bitmap> bitmapList;
    private Bitmap media;
    File localFile;
    StorageReference islandRef;
    static private Uri uri;
    private ShareDialog shareDialog;


    public messageReponseAdapter(Context context, List<Message> messageList, ShareDialog sd)
    {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageList = messageList;
        Log.e("message","message Adapter Constructer");
        shareDialog = sd;

    }



    @Override
    public void onComplete(@NonNull Task<Uri> task) {
        Log.e("url de success",""+uri);
        this.setUri(uri);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        private TextView messageTextView;
        private TextView reponseTextView;
        private ImageView messageImageView;
        private TextView reply;
        private TextView share;


        MessageViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.recycler);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            reponseTextView = (TextView) itemView.findViewById(R.id.nbReponseTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            reply = (TextView) itemView.findViewById(R.id.replyTextView);
            share = (TextView) itemView.findViewById(R.id.shareTextView);

        }


    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        MessageViewHolder pvh = new MessageViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Bitmap image;
        Task<Uri> turi;
        Uri turi2 = null;
        final int p = position;


        holder.messageTextView.setText(messageList.get(position).getMesage());

        holder.reponseTextView.setText(messageList.get(position).getNbReponses() + "");




        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyStart = new Intent(context,reponseActivity.class);
                replyStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                replyStart.putExtra("key",messageList.get(p).getKey());
                context.startActivity(replyStart);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Tell in Box")
                            .setContentDescription(messageList.get(p).getMesage())
                            .setContentUrl(Uri.parse(messageList.get(p).getMediaUrl()))
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });

        //holder.reponseTextView.setText(messageList.get(position).getReponses().size());
        Log.e("url de message",messageList.get(position).getMediaUrl());

        Glide.with(context).load(messageList.get(position).getMediaUrl()).into(holder.messageImageView);

        /*StorageMetadata storageMetadata = forestRef.getMetadata().getResult();

        try
        {
            URL newurl = new URL(storageMetadata.getDownloadUrl()+"");
            image = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            holder.messageImageView.setImageBitmap(image);
        }
        catch (Exception ex)
        {
            Log.e("Exception",ex.getMessage());
        }*/


    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public Bitmap getMedia() {
        return media;
    }

    public void setMedia(Bitmap media) {
        this.media = media;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

}
