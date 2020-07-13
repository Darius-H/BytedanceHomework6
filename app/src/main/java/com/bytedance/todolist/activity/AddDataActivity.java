package com.bytedance.todolist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bytedance.todolist.R;
import com.bytedance.todolist.database.TodoListDao;
import com.bytedance.todolist.database.TodoListDatabase;
import com.bytedance.todolist.database.TodoListEntity;

import java.util.Date;

public class AddDataActivity extends AppCompatActivity {
    private EditText inputtext;
    private Button confirmbt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_data_layout);
        inputtext=findViewById(R.id.input_box);
        confirmbt=findViewById(R.id.confirm_button);
        confirmbt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                final String content=inputtext.getText().toString();
                final Date date=new Date(System.currentTimeMillis());
                new Thread() {
                    @Override
                    public void run() {
                        TodoListDatabase tdldb= TodoListDatabase.inst(getApplicationContext());
                        TodoListDao tdldao=tdldb.todoListDao();
                        TodoListEntity entity=new TodoListEntity(content,date);
                        tdldao.addTodo(entity);
                    }
                }.start();
                finish();
            }
        });

    }
}
