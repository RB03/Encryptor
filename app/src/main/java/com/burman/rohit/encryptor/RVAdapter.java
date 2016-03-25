package com.burman.rohit.encryptor;

import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.burman.rohit.encryptor.utils.FileHandler;
import com.burman.rohit.encryptor.utils.FileTypeUtils;
import com.burman.rohit.encryptor.utils.ModeHandler;
import com.burman.rohit.encryptor.utils.ViewProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Rohit on 2/8/2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    private static final String TAG = "RV_ADAPTER";
    private static ActionMode mActionMode;
    public ViewHolderClicks viewHolderClicks;
    private ArrayList<File> mFiles;
    private ArrayList<String> paths;
    private ModeHandler mModeHandler;
    private FileHandler mFileHandler;
    private ViewHolder viewHolder;
    private View v;

    public RVAdapter(ArrayList<File> titles, ModeHandler modeHandler) {
         Collections.sort(titles);
        this.mFiles =titles;
        mFileHandler= new FileHandler();
        this.mModeHandler = modeHandler;
    }

    public RVAdapter( ModeHandler modeHandler) {
        mFileHandler= new FileHandler();
        this.mModeHandler = modeHandler;
        mFiles=mFileHandler.getFiles(FileHandler.getDirectory(mModeHandler.getMODE()));
        Collections.sort(mFiles);
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if(mModeHandler.getMODE().equals(ModeHandler.ENCRYPTOR)) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_dec, parent,
                    false);
        }
        viewHolder = new ViewHolder(v,mModeHandler, viewHolderClicks);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        File currentFile = mFiles.get(position);
        holder.filename.setText(currentFile.getName());
        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);
        holder.mFileImage.setImageResource(fileType.getIcon());

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public void clickListener(ViewHolderClicks viewHolderClicks) {
        this.viewHolderClicks = viewHolderClicks;
    }

    public String getFilePath(int position) {
        return mFiles.get(position).getPath();
    }

    public void removeItem(int position) {
        mFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mFiles.size());
    }

    public void removeItem(File file) {
        int position = mFiles.indexOf(file);
        mFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mFiles.size());
    }

    public void addItem(File file) {
        mFiles.add(0, file);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, mFiles.size());

    }

    public void updateDataSet() {
        if (ContextActionMode.isAlive) ContextActionMode.kill();

        mFiles = mFileHandler.getFiles(FileHandler.getDirectory(mModeHandler.getMODE()));
        Collections.sort(mFiles);
        notifyDataSetChanged();
        notifyItemRangeChanged(0, getItemCount());

        //TODO update checklist
        //Temporary fix
    }

    public interface ViewHolderClicks {
        void onItemClicked(View v, int position);

        void onItemLongClicked(View v, int adapterPosition);

        void itemChecked(View v, int position, boolean isChecked);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
        private static final String TAG = "VIEW_HOLDER";
        private TextView filename;
        private ImageView imageView;
        private ImageView mFileImage;
        private CheckBox checkBox;
        private ViewHolderClicks viewClicks;

        public ViewHolder(View itemView, ModeHandler mModeHandler, ViewHolderClicks
                viewHolderClicks ) {
            super(itemView);
            viewClicks=viewHolderClicks;
            try {
                mFileImage = (ImageView) itemView.findViewById(R.id.fileicon);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkBox2);
            } catch (NullPointerException shit) {
              shit.printStackTrace();
            }

            imageView = ViewProvider.getLockIcon(itemView, mModeHandler.getMODE());
            filename= ViewProvider.getFilenameTextView(itemView, mModeHandler.getMODE());

            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);

            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            if(viewClicks!=null){
                viewClicks.onItemClicked(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (viewClicks!=null){
                viewClicks.onItemLongClicked(v,getAdapterPosition());
            }
            return  true;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            viewClicks.itemChecked(buttonView, getAdapterPosition(), isChecked);
        }
    }
}
