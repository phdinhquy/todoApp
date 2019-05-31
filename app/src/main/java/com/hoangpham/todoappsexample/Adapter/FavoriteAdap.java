package com.hoangpham.todoappsexample.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class FavoriteAdap extends RecyclerView.Adapter<FavoriteAdap.ViewHolder> {
    private Context mContext;
    private List<TodoDTO> mData;
    private  boolean isSelected ;

    private DatabaseReference myRef;
    public FavoriteAdap(Context mContext, List<TodoDTO> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.item_favorite_layout,viewGroup,false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final TodoDTO todoDTO = mData.get(i);
        viewHolder.mTodo.setText(todoDTO.getWork());
        viewHolder.mImage.setSelected(todoDTO.isFavorite());
        isSelected = todoDTO.isFavorite();
        viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelected = !isSelected;
                viewHolder.mImage.setSelected(isSelected);
                setFavorite(String.valueOf(i),todoDTO,isSelected);
                Toast.makeText(mContext, "selected", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTodo;
        ImageView mImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTodo = itemView.findViewById(R.id.favorite_text_view);
            mImage = itemView.findViewById(R.id.favorite_btn_favorite);

        }
    }
}