package com.example.pos_tagger;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText input;
    TextView output;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.ed_input_text);
        output = findViewById(R.id.tv_output_text);
        submit = findViewById(R.id.btn_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputText = input.getText().toString();

                // 분석 통신하기
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://13.124.187.219:5000")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitExService rService = retrofit.create(RetrofitExService.class);
                ReqData reqObj = new ReqData(inputText);
                Call<ResData> userCall = rService.getData(reqObj);
                userCall.enqueue(new Callback<ResData>() {
                    @Override
                    public void onResponse(Call<ResData> call, Response<ResData> response) {

                        ResData resData = response.body();
                        Object[] resObj = resData.getResult();
                        String resultText = "";

                        for(int i = 0; i < resObj.length; i++){

                            ArrayList tempList = (ArrayList) resObj[i];
                            String tempText = "";
                            for(int j = 0; j < tempList.size(); j++){
                                tempText += " " + tempList.get(j).toString();
                            }
                            resultText += tempText;
                        }
//                        Toast.makeText(getApplicationContext(), resultText
//                                , Toast.LENGTH_SHORT ).show();

                        if(output.getText() != "") output.setText("");

                        output.setText(resultText);
//                output.setText("통신 성공");

                    }
                    @Override
                    public void onFailure(Call<ResData> call, Throwable t) {
                        output.setText("통신 실패 : " + t);
                    }
                });
                // 결과값 출력

            }
        });




    }
}
