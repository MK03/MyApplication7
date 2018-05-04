package report.days.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Integer.lowestOneBit;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mRadioGroup;
    private int NowCheck, NowDate;
    private EditText editText2;
    private LinearLayout LLAddData;
    private LinearLayout LLTeisyutsu;

    private String Nippou;
    private int SagyouKaishiZikoku;


    private EditText button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);

        EditText editTextBikou=(EditText)findViewById(R.id.editTextBikou);
        editTextBikou.setText(sharedPreferences.getString("Bikou",""));

        // RadioGroupをメンバ変数に保存しておく
        mRadioGroup = (RadioGroup) findViewById(R.id.radioButtons);
        mRadioGroup.setOnCheckedChangeListener(this);
        editText2 = (EditText) findViewById(R.id.editText2);
        LLAddData = (LinearLayout) findViewById(R.id.LLAddData);
        LLTeisyutsu = (LinearLayout) findViewById(R.id.LLTeisyutsu);
        RadioButton VE = (RadioButton) findViewById(R.id.radioButton3);

        NowCheck = 1;

        Spinner spneerZi = (Spinner) findViewById(R.id.spinnerZi);
        Spinner spneerHun = (Spinner) findViewById(R.id.spinnerHun);
        ArrayAdapter<String> adapterZi = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterZi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapterHun = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterHun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        String[] stringsHun = new String[60];
        String[] stringsZi = new String[24];


        for (int num = 0; num <= 59; num++) {
            if (num < 24) stringsZi[num] = "" + num;
            stringsHun[num] = "" + num;

        }
        adapterZi.addAll(stringsZi);

        adapterHun.addAll(stringsHun);

        spneerZi.setAdapter(adapterZi);
        spneerHun.setAdapter(adapterHun);


        EditText editText3 = (EditText) findViewById(R.id.editText3);
        editText3.setText("" + sharedPreferences.getInt(getString(R.string.WarikomiSagyousuu), 0));


        Button button = (Button) findViewById(R.id.buttonShinki);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);

                String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");
                String[] sp = oldData.split("/");
                int count = 0;
                String timer0 = "";
                for (String timer : sp) {
                    if (sharedPreferences.getInt("Count" + timer, 0) <= -1)
                        timer0 = timer;
                    else count++;

                }

                if (!timer0.equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("Count" + timer0, 0);
                    editor.apply();


                }
                tagstr="";

                Spinner spinner = (Spinner) findViewById(R.id.spinner);


                Calendar cal = Calendar.getInstance();

                int nowTime = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);


                SetData(spinner.getSelectedItem().toString(), nowTime, 0);

            }
        });
        Button button2 = (Button) findViewById(R.id.button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Spinner spinner = (Spinner) findViewById(R.id.spinner2);

                Spinner spinnerZi = (Spinner) findViewById(R.id.spinnerZi);
                Spinner spinnerHun = (Spinner) findViewById(R.id.spinnerHun);

                String ZiStr = spinnerZi.getSelectedItem().toString();
                String HunStr = spinnerHun.getSelectedItem().toString();


                int nowTime = Integer.parseInt(ZiStr) * 100 + Integer.parseInt(HunStr);

                EditText editTextSagyousuuNow = (EditText) findViewById(R.id.editText3);
                String workNumStr = editTextSagyousuuNow.getText().toString();
                int worknum = 0;
                if (checkNumOrText(workNumStr))
                    worknum = Integer.parseInt(workNumStr);


                SetData(spinner.getSelectedItem().toString(), nowTime, worknum);

            }
        });

    Button buttonSakujo = (Button) findViewById(R.id.buttonSakujo);
        buttonSakujo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Sakujo();

            DeleteButtonsMake();

            LLAddData.setVisibility(View.INVISIBLE);
            LLTeisyutsu.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("tag");
            editor.apply();
        }
    });
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EditText editText3 = (EditText) findViewById(R.id.editText3);
                String str = editText3.getText().toString();

                int now = 0;

                if (str.equals(""))
                    now = 1;
                else if (checkNumOrText(str)) {
                    now = Integer.parseInt(str);
                    now++;

                }
                editText3.setText("" + now);
            }
        });
        Button ButtonCancel = (Button) findViewById(R.id.ButtonCancel);
        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LLTeisyutsu.setVisibility(View.VISIBLE);
                LLAddData.setVisibility(View.INVISIBLE);

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("tag");
                editor.apply();

            }
        });


        Button buttonTeisyutsu = (Button) findViewById(R.id.buttonTeisyutsu);
        buttonTeisyutsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                EditText editTextMailAddress = (EditText) findViewById(R.id.editTextMailAddress);
                editor.putString(getString(R.string.Mail), editTextMailAddress.getText().toString());
                editor.apply();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);

                Calendar cal = Calendar.getInstance(); String bikou=((EditText)findViewById(R.id.editTextBikou)).getText().toString();
                Nippou = Nippou + "\n備考\n" + bikou;
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + editTextMailAddress.getText().toString()));
                intent.putExtra(Intent.EXTRA_SUBJECT, cal.get(Calendar.YEAR) + "年" + (1 + cal.get(Calendar.MONTH)) + "月" + cal.get(Calendar.DATE) + "日　日報");
                intent.putExtra(Intent.EXTRA_TEXT, Nippou);

//        startActivity(intent);
                startActivity(Intent.createChooser(intent, null));

            }
        });

        String sher = sharedPreferences.getString(getString(R.string.WorkName), "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editText2.setText(sher);




        final Handler handler = new Handler();
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();


                int lastDate = sharedPreferences.getInt(getString(R.string.LastDate), 0);
                Calendar cal = Calendar.getInstance();
                NowDate = cal.get(Calendar.YEAR) * 10000 + cal.get(Calendar.MONTH) * 100 + cal.get(Calendar.DATE);

                if (lastDate > 0 && lastDate < NowDate) {
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("日付が変更されました。")
                            .setMessage("昨日までの記録を全て削除しますか？")
                            .setPositiveButton("する", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    String[] sp = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "").split("\n");
                                    for (String gotTime : sp) {
                                        if (!gotTime.equals("")) {
                                            editor.remove("Name" + gotTime);
                                            editor.remove("Count" + gotTime);
                                        }
                                    }
                                    editor.putString(getString(R.string.TheTimeOfStart), "");
                                    LLAddData.setVisibility(View.INVISIBLE);

                                    LLTeisyutsu.setVisibility(View.VISIBLE);
                                    DeleteButtonsMake();
                                }
                            })
                            .setNegativeButton("しない", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create().show();





                }
                editor.putInt("LastDate", NowDate);

                editor.apply();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(r);





        MakeAddData();

        tagstr=sharedPreferences.getString("tag","");


        VE.setChecked(true);
        editText2.setVisibility(View.INVISIBLE);
        if(tagstr.equals("")) {

            LLAddData.setVisibility(View.INVISIBLE);

            LLTeisyutsu.setVisibility(View.VISIBLE);
        }
        else
        {LLTeisyutsu.setVisibility(View.INVISIBLE);
            LLAddData.setVisibility(View.VISIBLE);
            EditData(tagstr);


        }



    }

    private String Sagyoumei;
    private int HM;
    private int SagyouSuu;

    private void SetData(String sagyoumei, int hm, int sagyousuu) {


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);

        String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");

        if (oldData.contains("" + hm)) {
            HM = hm;
            tagstr=""+HM;
            Sagyoumei = sagyoumei;
            SagyouSuu = sagyousuu;
            new AlertDialog.Builder(this)
                    .setTitle("この時刻に、既に記録があります。")
                    .setMessage("上書きしますか？")
                    .setPositiveButton("上書きする", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Sakujo();

                            SetData0(Sagyoumei, HM, SagyouSuu);
                        }
                    })
                    .setNegativeButton("上書きしない", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create().show();
        } else {


            SetData0(sagyoumei, hm, sagyousuu);
        }

    }

    private void SetData0(String sagyoumei, int hm, int sagyousuu) {
        Sakujo();



        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("tag");
        String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");
        editor.putString("Name" + hm, sagyoumei);
        editor.putInt("Count" + hm, sagyousuu);
        editor.putString(getString(R.string.TheTimeOfStart), oldData + hm + "/");

        editor.apply();
        LLAddData.setVisibility(View.INVISIBLE);
        LLTeisyutsu.setVisibility(View.VISIBLE);
        DeleteButtonsMake();
        Alert();
    }

    private boolean checkNumOrText(String str) {
        if (str.equals("")) return false;
        char[] c = str.toCharArray();
        char c3;
        for (char c2 : c) {
            c3 = (char) (c2 - '0');
            if (c3 > 9) {
                return false;
            }


        }
        return true;

    }


    private void MakeAddData() {
        Spinner spneer = (Spinner) findViewById(R.id.spinner);
        Spinner spneer2 = (Spinner) findViewById(R.id.spinner2);

        String workNameFinal = "無\n" + editText2.getText().toString();


        String[] workName = workNameFinal.split("\n");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (String str : workName)
            if (!str.equals(""))
                adapter.add(str);


        spneer2.setAdapter(adapter);
        spneer.setAdapter(adapter);

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
        String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String wn;
        switch (NowCheck) {
            case 0:
                editText2.setVisibility(View.INVISIBLE);
                LLAddData.setVisibility(View.INVISIBLE);
                wn = editText2.getText().toString();
                if (wn.substring(wn.length() - 1).equals("\n"))
                    wn = wn.substring(0, wn.length() - 2);

                editor.putString(getString(R.string.WorkName), wn);
                editor.apply();
                /* */
                MakeAddData();

                break;

            case 2:
                LLTeisyutsu.setVisibility(View.INVISIBLE);
                break;

        }
        switch (checkedId) {
            case R.id.radioButton:
                editText2.setVisibility(View.VISIBLE);
                NowCheck = 0;
                break;


            case R.id.radioButton3:
                LLTeisyutsu.setVisibility(View.VISIBLE);
                NowCheck = 2;
                break;
        }
        if (NowCheck == 2) {

            DeleteButtonsMake();

        }
    }

    private void DeleteButtonsMake() {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
        EditText editText = (EditText) findViewById(R.id.editTextMailAddress);
        editText.setText(sharedPreferences.getString(getString(R.string.Mail), ""));

        String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");

        Data data = null;
        Data datacount = null;
        Data datacountSearch = null;
        String[] sp = oldData.split("/");
        String name;
        int time;
        int timeM;
        int count, count0 = 0;
        for (String theTime : sp) {
            if (!theTime.equals("")) {
                name = sharedPreferences.getString("Name" + theTime, "");
                count = sharedPreferences.getInt("Count" + theTime, 0);
                time = Integer.parseInt(theTime);
                data = new Data(name, time, count, data);
                datacount = datacountSearch;
                while (datacount != null && !datacount.getWorkName().equals(name)) {
                    datacount = datacount.getNextData();
                }
                if (datacount != null)
                    datacount.AddTimeAndCount(0, count);
                else datacountSearch = new Data(name, 0, count, datacountSearch);


                count0++;
            }
        }
        for (int count1 = 0; count1 < count0 - 1; count1++) {
            data = data.SortingData();
        }
        Data data2 = data;

        datacount = data;
        Data data3 = null;

        int timeM2, timeAll = 0;

        while (datacount != null) {
            if (datacount.getNextData() != null) {
                timeM = datacount.getTheTime();
                timeM = (timeM / 100) * 60 + timeM % 100;

                timeM2 = datacount.getNextData().getTheTime();
                timeM2 = (timeM2 / 100) * 60 + timeM2 % 100;

                timeM2 -= timeM;

                data3 = datacountSearch;
                while (data3 != null && !data3.getWorkName().equals(datacount.getWorkName()))
                    data3 = data3.getNextData();

                data3.AddTimeAndCount(timeM2, 0);
                if (!data3.getWorkName().equals("無"))
                    timeAll += timeM2;
            }

            datacount = datacount.getNextData();
        }

        /*  */
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayout);
        tableLayout.removeAllViews();
        TableRow tableRow;
        TextView TheTimeOfStartText;
        TextView NameText;
        TextView CountText;

        TextView ButtonText2 = new TextView(this);            ButtonText2.setGravity(Gravity.CENTER_HORIZONTAL);
        Button button;
        boolean flag = false;
        boolean firstline = true;
        Nippou = "";
        do {
            tableRow = new TableRow(this);
            TheTimeOfStartText = new TextView(this);
            NameText = new TextView(this);
            CountText = new TextView(this);

            TheTimeOfStartText.setGravity(Gravity.END);
            NameText.setGravity(Gravity.END);
            CountText.setGravity(Gravity.END);
            if (firstline) {
                Nippou = "開始時刻,作業名,作業数,\n";

                TheTimeOfStartText.setText("　開始時刻　");
                NameText.setText("　作業名");

                CountText.setText("　作業数");

                ButtonText2.setText("編集");
                firstline = false;


                tableRow.addView(TheTimeOfStartText);
                tableRow.addView(NameText);

                tableRow.addView(CountText);
                tableRow.addView(ButtonText2);

            } else {
                String str = "" + (data2.getTheTime() / 100) + "時" + (data2.getTheTime() % 100) + "分";
                Nippou = Nippou + str + "," + data2.getWorkName() + "," + data2.getCount() + "\n";

                TheTimeOfStartText.setText(str + "　");
                NameText.setText(data2.getWorkName());
                str = "" + data2.getCount();

                if (str.equals("-1"))

                    CountText.setText(R.string.mitei);

                else
                    CountText.setText(str);

                Button button2 = new Button(this);
                button2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                button2.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

                button2.setTag(data2.getTheTime());
                button2.setText(R.string.ButtonHenshu);
                button2.setTag(data2.getTheTime());
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tagstr="";
                        EditData(view.getTag().toString());
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tag",view.getTag().toString());
                        editor.apply();
                    }
                });

                tableRow.addView(TheTimeOfStartText);
                tableRow.addView(NameText);
                tableRow.addView(CountText);
                tableRow.addView(button2);


                if (data2 != null)
                    data2 = data2.getNextData();
            }
            if (flag)
                tableRow.setBackgroundColor(Color.YELLOW);
            else
                tableRow.setBackgroundColor(Color.GREEN);

            flag = !flag;
            tableLayout.addView(tableRow);

        } while (data2 != null);

        Nippou = Nippou + "\n作業名,作業時間(分),作業数,\n";
        data3 = datacountSearch;
        while (data3 != null) {
            if(!data3.getWorkName().equals("無") )
            Nippou = Nippou + data3.getWorkName() + "," + data3.getTheTime() + "," + data3.getCount() + "\n";

            data3 = data3.getNextData();

        }


        Nippou = Nippou + "\n全作業時間," + timeAll;


        if (datacountSearch != null)
            datacountSearch.Erase();

        if (data2 != null)
            data2.Erase();
    }
    String tagstr;
    private void Sakujo()
    {
        if(!tagstr.equals("")) {

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");

            String newdata = oldData.replace(tagstr + "/", "");
            editor.putString(

                    getString(R.string.TheTimeOfStart), newdata);

            editor.remove("Name" + tagstr);
            editor.remove("Count" + tagstr);

            editor.apply();


            DeleteButtonsMake();
        }

}
    private void EditData(String tag)
    {
        boolean pressEditButton=tagstr.equals("");


        tagstr=tag;
        LLAddData.setVisibility(View.VISIBLE);
        LLTeisyutsu.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
        String TargetWorkName = sharedPreferences.getString("Name" + tag, "");

        String[] searchPos = ((EditText) findViewById(R.id.editText2)).getText().toString().split("\n");
        int count = 0;
        if(!TargetWorkName.equals("無"))
            while (!searchPos[count].equals(TargetWorkName)) {
                count++;
            }
        else count=-1;

        Spinner spinner = (Spinner) findViewById(R.id.spinner2);

        spinner.setSelection(count + 1);

        spinner = (Spinner) findViewById(R.id.spinnerZi);
        spinner.setSelection(Integer.parseInt(tag) / 100);
        spinner = (Spinner) findViewById(R.id.spinnerHun);
        spinner.setSelection(Integer.parseInt(tag) % 100);

        EditText editText1 = (EditText) findViewById(R.id.editText3);
        int num,num2;

            num=sharedPreferences.getInt("Count" + tag, 0);

         num2=sharedPreferences.getInt(getString(R.string.WarikomiSagyousuu), 0);

         if(pressEditButton)
             num2=num;

        editText1.setText("" + num2);

        TextView TextViewBefore = (TextView) findViewById(R.id.TextViewBefore);

        TextViewBefore.setText("変更前記録："+
                (Integer.parseInt(tag) / 100)+"時"+
                (Integer.parseInt(tag) % 100)+"分　"+TargetWorkName+" 作業数："+num

        );


    }

    @Override
    protected void onStop() {
        super.onStop();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


        @Override
        protected void onPause()
        {
            super.onPause();
            SharedPreferences sharedPreferences=getSharedPreferences(getString(R.string.DaysReport),MODE_PRIVATE);
            String oldData=sharedPreferences.getString(getString(R.string.TheTimeOfStart),"");
            SharedPreferences.Editor editor=sharedPreferences.edit();
            String e=editText2.getText().toString();


            if(e.substring(e.length()-1).equals("\n"))
                e=e.substring(0,e.length()-2);

            editor.putString(getString(R.string.WorkName),e);




            EditText editText3=(EditText)findViewById(R.id.editText3);
            int i=0;
            String str=editText3.getText().toString();
            if(checkNumOrText(str)) i = Integer.parseInt(str);

            editor.putInt(getString(R.string.WarikomiSagyousuu), i);
             editText3=(EditText)findViewById(R.id.editTextBikou);
            editor.putString("Bikou", editText3.getText().toString());
            editor.apply();
        }

        private void Alert()
        {
            new AlertDialog.Builder(this)
                    .setTitle("OK")
                    .setMessage("登録しました。")
                    .create().show();

        }

    }
