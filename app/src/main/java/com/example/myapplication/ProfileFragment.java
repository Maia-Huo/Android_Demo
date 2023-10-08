package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    List<byte[]> bitmapBytesList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        //读取SharedPreferences中保存的账号
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("username", Context.MODE_PRIVATE);
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


        // 在这里添加获取ViewModel的逻辑
        DataShare dataShare = new ViewModelProvider(requireActivity()).get(DataShare.class);

        TextView textView = view.findViewById(R.id.property1);
        textView.setOnClickListener(view1 -> {
            dataShare.NewPersonData();

            Intent intent = new Intent(getActivity(), PersonShare.class);

            startActivity(intent);
        });


        // 设置背景
        view.setBackgroundColor(Color.WHITE); // 以白色背景为例
        // 其他初始化工作
        return view;


    }

}
