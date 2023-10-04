package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        //读取SharedPreferences中保存的账号
        SharedPreferences  sharedPreferences = requireContext().getSharedPreferences("username", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        TextView profileName = view.findViewById(R.id.profile_name);

        if (username != null && !username.isEmpty()) {
            profileName.setText(username);
        }

        LinearLayout quit = view.findViewById(R.id.quit_layout);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }

        });


        // 设置背景
        view.setBackgroundColor(Color.WHITE); // 以白色背景为例
        // 其他初始化工作
        return view;


    }
}
