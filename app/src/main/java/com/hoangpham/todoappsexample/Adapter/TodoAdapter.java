package com.hoangpham.todoappsexample.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hoangpham.todoappsexample.R;
import com.hoangpham.todoappsexample.models.TodoDTO;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private Activity mContext;
    private List<TodoDTO> mData;
    private boolean isSelected;
    private DatabaseReference myRef;
    private TodoDTO mRecentItem;
    private int mRemovePositon;
    private boolean isDeleted;
    public TodoAdapter(Activity mContext, List<TodoDTO> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.item_todo_layout,viewGroup,false);
        return new ViewHolder(v);
    }

    public void removeItem(int position) {
        isDeleted = true; // true
        mRecentItem = this.mData.get(position);
        mRemovePositon = position;
        this.mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
        showUndoSnackBar();
    }
    private void showUndoSnackBar() {
        View view = mContext.findViewById(R.id.todo_container);
        Snackbar snackbar = Snackbar.make(view, "Undo revome", Snackbar.LENGTH_SHORT);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDeleted = false; // false
                undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        mData.add(mRemovePositon, mRecentItem);
        notifyItemInserted(mRemovePositon);
    }

    private void setChecked(final String pos, final TodoDTO todoDTO, final boolean isChecked){
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child(mContext.getString(R.string.db_name_todo))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.getKey().equals(pos)){
                                todoDTO.setChecked(isChecked);

                                myRef.child(mContext.getString(R.string.db_name_todo))
                                        .child(pos).setValue(todoDTO);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setFavorite(final String pos ,final TodoDTO todoDTO,final boolean isChecked){
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child(mContext.getString(R.string.db_name_todo))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.getKey().equals(pos)){
                                todoDTO.setFavorite(isChecked);
                                myRef.child(mContext.getString(R.string.db_name_todo))
                                        .child(pos).setValue(todoDTO);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void deleteWork(){
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child(mContext.getString(R.string.db_name_todo)).setValue(mData);
    }




    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final TodoDTO todoDTO = mData.get(i);
        viewHolder.mTodo.setText(todoDTO.getWork());
        viewHolder.mCheckBox.setChecked(todoDTO.isChecked());
        viewHolder.mImage.setSelected(todoDTO.isFavorite());
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeItem(i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isDeleted) {
                            deleteWork();
                            isDeleted = false;
                        }
                    }
                }, 3000);

                return false;
            }
        });
        isSelected = todoDTO.isFavorite();
        viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelected = !isSelected;
                viewHolder.mImage.setSelected(isSelected);
                setFavorite(String.valueOf(i),todoDTO,isSelected);
                Toast.makeText(mContext, "Liked", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    setChecked(String.valueOf(i),todoDTO,b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTodo;
        CheckBox mCheckBox;
        ImageView mImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTodo = itemView.findViewById(R.id.todo_text_view);
            mCheckBox = itemView.findViewById(R.id.todo_checkbox);
            mImage = itemView.findViewById(R.id.to_do_btn_favorite);

        }
    }
}
