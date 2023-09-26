package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;



public class TeacherHomeActivity extends AppCompatActivity {

    private LinearLayout courseListLayout;
    private EditText courseNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        // 初始化布局中的元素
        courseListLayout = findViewById(R.id.course_list_layout);
        courseNameEditText = findViewById(R.id.course_name_edit_text);

        // 找到并设置"添加课程"按钮的点击事件
        Button addCourseButton = findViewById(R.id.add_course_button);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用添加课程的方法
                addCourse();
            }
        });
    }

    // 添加课程的方法
    private void addCourse() {
        String courseName = courseNameEditText.getText().toString().trim();

        if (!courseName.isEmpty()) {
            // 添加课程条目并清除输入框
            addCourseEntry(courseName);
            courseNameEditText.getText().clear();
        } else {
            // 显示提示消息，要求输入课程名称
            Toast.makeText(this, "请输入课程名称", Toast.LENGTH_SHORT).show();
        }
    }

    // 添加课程条目的方法
    private void addCourseEntry(String courseName) {
        // 使用LayoutInflater加载课程条目布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View courseEntryView = inflater.inflate(R.layout.course_entry_layout, courseListLayout, false);

        TextView courseNameText = courseEntryView.findViewById(R.id.course_name_text);
        courseNameText.setText(courseName);

        Button startSignInButton = courseEntryView.findViewById(R.id.start_sign_in_button);
        Button viewHistoryButton = courseEntryView.findViewById(R.id.view_history_button);
        Button removeCourseButton = courseEntryView.findViewById(R.id.remove_course_button);

        startSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TeacherHomeActivity.this, "开始签到：" + courseName, Toast.LENGTH_SHORT).show();
            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TeacherHomeActivity.this, "查看签到历史：" + courseName, Toast.LENGTH_SHORT).show();
            }
        });

        removeCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCourse(courseEntryView);
            }
        });

        courseListLayout.addView(courseEntryView);
    }

    private void removeCourse(View courseEntryView) {
        courseListLayout.removeView(courseEntryView);
    }
}
