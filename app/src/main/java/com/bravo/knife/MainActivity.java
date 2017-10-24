package com.bravo.knife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.anno.bravo.BindView;
import com.bravo.inject.InjectView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    public TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectView.bind(this);
        if (tv != null) {
            tv.setText("success");
        }
    }
}
