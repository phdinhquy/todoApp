package com.hoangpham.todoappsexample.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hoangpham.todoappsexample.R;
import com.hoangpham.todoappsexample.models.TodoDTO;

import java.util.ArrayList;
import java.util.List;

public class DialogFragmentAdd extends DialogFragment {
    private static final String TAG = "DialogFragmentAdd";
    private String DB_NAME_TODO ;
    private List<TodoDTO> lists;
    private DatabaseReference myRef;
    DataChangeListener mCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (DataChangeListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+ " must implement OnImageClickListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_todo,container,false);
        final TextView txtTodo = v.findViewById(R.id.txt_todo);
        Button btnConfirm = v.findViewById(R.id.btn_confirm);

        myRef = FirebaseDatabase.getInstance().getReference();
        DB_NAME_TODO = getResources().getString(R.string.db_name_todo);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoText = txtTodo.getText().toString();
                if (todoText.length() != 0 ){
                    addToDoList(todoText);

                    dismiss();
                }else {
                    Toast.makeText(getActivity(), "Chưa có công việc được thêm vào", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

        });
        return v;
    }

    private void addToDoList(final String todoText) {
       if (isAdded()){
           lists = new ArrayList<>();
           Query query = myRef.child(DB_NAME_TODO);
           query.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       Log.d(TAG, "onDataChange: ");
                       for (DataSnapshot ds : dataSnapshot.getChildren()){
                           TodoDTO todoDTO = ds.getValue(TodoDTO.class);
                           lists.add(todoDTO);
                       }
                       Log.d(TAG, "onDataChange: lists" + lists.size());
                       TodoDTO todoDTO = new TodoDTO(false,todoText,false);
                       mCallBack.updateRecyclerView(todoDTO);
                       lists.add(todoDTO);
                       myRef.child(DB_NAME_TODO).setValue(lists);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }
    }
    public interface DataChangeListener{
        void updateRecyclerView(TodoDTO todoDTO);
    }
}
