package report.days.myapplication;

import android.app.AlertDialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;


import android.net.Uri;
import android.os.Handler;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {


    private EditText editTextWorkNames;
    private LinearLayout LLWorkList;
    private LinearLayout LLEditData;
    private EditText editTextMailAddress;
    private Spinner MainSpinner;



    //全体編集
    private LinearLayout LLTeisyutsu;

    private Timer mTimer   = null;            //onClickメソッドでインスタンス生成
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ

    private String Nippou;
    private    RadioGroup mRadioGroup;
    private  SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainSpinner = (Spinner) findViewById(R.id.spinner);
        MainSpinner.setFocusable(false);
        LLTeisyutsu= findViewById(R.id.LLTeisyutsu);
        mRadioGroup=findViewById(R.id.radioGroupUwagakiWarikomi);

        sharedPreferences = getSharedPreferences(getString(R.string.DaysReport), MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //タイマー発動
        mTimer=new Timer(false);
        mTimer.schedule( new TimerTask(){
            @Override
            public void run() {
                // mHandlerを通じてUI Threadへ処理をキューイング
                mHandler.post( new Runnable() {
                    public void run() {

                        //カレンダー取得
                        Calendar cal = Calendar.getInstance();

                        //現在時を取得
                        int nt = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);

                        //
                        boolean act = false;

                        //時間表を取得
                        String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");
                        if (!oldData.equals("")) {
                            //時間表を分割
                            String[] sp = oldData.split("/");

                            //各時間ごとに処理

                                for (String sp2 : sp) {

                                    //その時間が現在より先ならば
                                    if (Integer.parseInt(sp2) > nt) {

                                        //時間表を空欄に置き換える
                                        oldData = oldData.replace(sp2 + "/", "");

                                        //作業名、作業数、終了時刻を削除
                                        editor.remove("Name" + sp2);
                                        editor.remove("Count" + sp2);
                                        editor.remove("End" + sp2);
                                        editor.putString(getString(R.string.TheTimeOfStart), oldData);

                                        act = true;
                                    }
                                }
                            if (act) {

                                //置き換え、削除があったら、アプライし、リストも作りなおす
                                editor.apply();
                                DeleteButtonsMake();
                            }
                        }
                    }
                });
            }
        }, 10000, 10000);



        //備考テキストボックス
        EditText editTextBikou=findViewById(R.id.editTextBikou);

        editTextBikou.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditTextのフォーカスが外れた場合
                if (hasFocus == false) {
                    // 処理を行う
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });

        //備考読み込み
        editTextBikou.setText(sharedPreferences.getString(getString(R.string.Bikou),getString(R.string.NullMozi)));

        //メールアドレスを記入
        editTextMailAddress = (EditText) findViewById(R.id.editTextMailAddress);
        editTextMailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditTextのフォーカスが外れた場合
                if (hasFocus == false) {
                    // 処理を行う
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });


        //作業名登録
        editTextWorkNames = findViewById(R.id.editTextWorkNames);
        LLWorkList = findViewById(R.id.LLWorkList);
        editTextWorkNames.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditTextのフォーカスが外れた場合
                if (hasFocus == false) {
                    // 処理を行う
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });


        //各作業内容編集
        LLEditData =  findViewById(R.id.LLEditData);




        //時スピナー
        AtomicReference<Spinner> spinnerZi = new AtomicReference<>(findViewById(R.id.spinnerZi));

        //分スピナー
        AtomicReference<Spinner> spinnerHun = new AtomicReference<>(findViewById(R.id.spinnerHun));


        //時スピナー
        AtomicReference<Spinner> spinnerEndZi = new AtomicReference<>(findViewById(R.id.spinnerEndZi));

        //分スピナー
        AtomicReference<Spinner> spinnerEndHun = new AtomicReference<>(findViewById(R.id.spinnerEndHun));


        //時分アダプター設定
        ArrayAdapter<String> adapterZi = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterZi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapterHun = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterHun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //分の文字列リスト
        String[] stringsHun = new String[60];
        //時の文字列リスト
        String[] stringsZi = new String[24];

        //文字列リストに値を格納
        for (int num = 0; num <= 59; num++) {
            if (num < 24) stringsZi[num] = "" + num;
            stringsHun[num] = "" + num;

        }
        //アダプタに登録
        adapterZi.addAll(stringsZi);
        adapterHun.addAll(stringsHun);

        //スピナーにアダプタを登録
        spinnerZi.get().setAdapter(adapterZi);
        spinnerHun.get().setAdapter(adapterHun);

        spinnerEndZi.get().setAdapter(adapterZi);
        spinnerEndHun.get().setAdapter(adapterHun);


        //設定中の作業数取得
        EditText editText3 =  findViewById(R.id.editText3);
        
        //現在の作業数をプレファレンスから取得
        editText3.setText(""+ sharedPreferences.getInt(getString(R.string.WarikomiSagyousuu), 0));

        
        editText3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditTextのフォーカスが外れた場合
                if (hasFocus == false) {
                    // 処理を行う
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });



        //作業終了ボタン
        Button button =  findViewById(R.id.buttonEnd);
        button.setOnClickListener(this::onClick);

        //変更ボタン
        Button button2 =  findViewById(R.id.buttonHenkou);
        button2.setOnClickListener(view -> {
            //編集モードのスピナー
            Spinner spinner = findViewById(R.id.spinnerWorkNameEdit);

            //選ばれているスピナー設定時刻
            String ZiStr = spinnerZi.get().getSelectedItem().toString();
            String HunStr = spinnerHun.get().getSelectedItem().toString();
            
            //スピナーによる設定時刻を数値化
            int nowTime = Integer.parseInt(ZiStr) * 100 + Integer.parseInt(HunStr);
            
            
            //選ばれているスピナー設定時刻
            String EndZiStr = spinnerEndZi.get().getSelectedItem().toString();
            String EndHunStr = spinnerEndHun.get().getSelectedItem().toString();

            
            //タグに登録されている、前作業の終了時刻、現作業の開始時刻、次作業の開始時刻に分割
            String[] sp=tagstr.split("/");

            //スピナーによる設定時刻を数値化
            int EndTime;
            int NextTime;

            boolean mitryou;
            
            //カレンダーの取得
            Calendar cal = Calendar.getInstance();

            //現時刻の取得
            int nt = cal.get(Calendar.HOUR_OF_DAY)*100+ cal.get(Calendar.MINUTE);


            //終了時刻が未設定
            if(!tagstr.equals("") && sharedPreferences.getInt("End"+sp[1],-1)==-1)
            {
                mitryou=true;//作業が終了していない
                EndTime = nt;//現時刻を、仮の終了時刻とする

            }else {//終了時刻が設定済み

                //終了時刻を設定
                EndTime = Integer.parseInt(EndZiStr) * 100 + Integer.parseInt(EndHunStr);
                mitryou=false;//作業が終了済み
            }
            if(sp.length<3)//次の作業が未設定
                NextTime=2400;
            else
                NextTime = Integer.parseInt(sp[2]);

            //作業数の文字列
            String workNumStr = editText3.getText().toString();
            int worknum = 0;
            if (checkNumOrText(workNumStr))//作業数が数値列
                worknum = Integer.parseInt(workNumStr);//数値化

            //前作業の終了時刻
            int beforeTime;
            if(sp[0].equals(""))beforeTime=0;
            else beforeTime=Integer.parseInt(sp[0]);

            //現作業の開始時刻
            int StartTime=Integer.parseInt(sp[1]);
            
            //編集長の作業を上書きするか、上書きせず割り込むか
            boolean uwagaki= (mRadioGroup.getCheckedRadioButtonId()==R.id.radioButtonUwagaki);
            
            //上書きしない場合
            if(!uwagaki)
            {
                //前作業は現作業の開始時刻となる
                beforeTime=StartTime;
            }


            //変更後の内容が、他の作業と被っている場合
            if(
                    (nowTime<=beforeTime || EndTime<=beforeTime || NextTime<=nowTime || NextTime<=EndTime || EndTime<nowTime)
                    )
            {
                //警告を出す
                new AlertDialog.Builder(this)
                        .setTitle("不正な登録内容です！")
                        .setMessage("・作業終了から作業開始までは1分以上開け、その後に次の作業を開始して下さい。\n・作業開始以後に作業終了させて下さい。\n・ここでは、現時刻及び日付をまたぐ作業は登録できません。")
                        .create().show();

            }
            else {
                //上書きする場合、編集中の作業を削除
                if(mRadioGroup.getCheckedRadioButtonId()==R.id.radioButtonUwagaki)
                {

                    Sakujo();
                }
                else
                {
                    //上書きしない場合、設定した開始時刻の一分前が編集中作業の終了時刻となる
                    editor.putInt("End"+StartTime,nowTime-1);
                    editor.apply();
                }

                //終了していない場合、記録も終了させない
                if (mitryou
                        ) SetData0(spinner.getSelectedItem().toString(), nowTime, worknum, -1);
                else
                    //終了している場合、終了時刻を記録
                    //データをセット
                    SetData0(spinner.getSelectedItem().toString(), nowTime, worknum, EndTime);
            }
        });
        //削除ボタン
        Button buttonSakujo =  findViewById(R.id.buttonSakujo);
        buttonSakujo.setOnClickListener(view -> {
            if(!tagstr.equals("")) {
                Sakujo();
   
            }

            //表を作り直す
            DeleteButtonsMake();

            //表に戻る
            EditToList();
        });

        //作業数１加算ボタン
        Button button3 = (Button) findViewById(R.id.buttonAddWork);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //テキストボックス
                EditText editText3 =findViewById(R.id.editText3);

                //テキストボックスの文字列
                String str = editText3.getText().toString();

                int now = 0;

                //テキストボックスがブランクなら1
                if (str.equals(""))
                    now = 0;
                else if (checkNumOrText(str)) {//テキストボックスの内容を確認
                    now = Integer.parseInt(str);//数値なら数値化


                }

                //テキストボックス
                EditText editTextAddWork =findViewById(R.id.editTextAddWork);

                //テキストボックスの文字列
                String strAdd = editTextAddWork.getText().toString();



                int add = 0;

                //テキストボックスがブランクなら1
                if (strAdd.equals(""))
                    add = 0;
                else if (checkNumOrText(strAdd)) {//テキストボックスの内容を確認
                    add = Integer.parseInt(strAdd);//数値なら数値化
                }
                //セット
                editText3.setText( ""+(now+add));
            }
        });
        //作業数１加算ボタン
        Button buttonDec = (Button) findViewById(R.id.buttonDecWork);
        buttonDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //テキストボックス
                EditText editText3 =findViewById(R.id.editText3);

                //テキストボックスの文字列
                String str = editText3.getText().toString();

                int now = 0;

                //テキストボックスがブランクなら1
                if (str.equals(""))
                    now = 0;
                else if (checkNumOrText(str)) {//テキストボックスの内容を確認
                    now = Integer.parseInt(str);//数値なら数値化


                }

                //テキストボックス
                EditText editTextAddWork =findViewById(R.id.editTextAddWork);

                //テキストボックスの文字列
                String strAdd = editTextAddWork.getText().toString();



                int add = 0;

                //テキストボックスがブランクなら1
                if (strAdd.equals(""))
                    add = 0;
                else if (checkNumOrText(strAdd)) {//テキストボックスの内容を確認
                    add = Integer.parseInt(strAdd);//数値なら数値化
                }

                now-=add;
                if(now<0)now=0;

                //セット
                editText3.setText( ""+(now));
            }
        });
        //編集せず戻るボタン
        Button ButtonCancel =  findViewById(R.id.ButtonCancel);
        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditToList();
                DeleteButtonsMake();
            }
        });
        
        //作業名の設定画面から、表へ移行するボタン
        Button buttonToWorkList = (Button) findViewById(R.id.ButtonToList);
        buttonToWorkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                //作業名編集画面を非表示
                LLWorkList.setVisibility(View.INVISIBLE);
                
                //作業編集画面を非表示
                LLEditData.setVisibility(View.INVISIBLE);
                String wn = editTextWorkNames.getText().toString();

                //作業名の末尾から、改行文字を全削除
                if(!wn.equals(""))
                    while (wn.substring(wn.length() - 1).equals("\n"))
                        wn = wn.substring(0, wn.length() - 2);

                //作業名の先頭から、改行文字を全削除
                if(!wn.equals(""))
                    while (wn.substring(0,1).equals("\n"))
                        wn = wn.substring(1,wn.length() );

                //作業名を保存
                editor.putString(getString(R.string.WorkName), wn);
                editor.apply();

                //作業名からスピナーを編集
                MakeWorkNameSpinner();

                //提出画面を表示
                LLTeisyutsu.setVisibility(View.VISIBLE);

                DeleteButtonsMake();
            }
            }
        );

        //作業名リストへ移行
        Button ButtonToNameList = (Button) findViewById(R.id.ButtonToNameList);
        ButtonToNameList.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //表と作業編集画面を非表示
                                                    LLTeisyutsu.setVisibility(View.INVISIBLE);

                                                    //作業名編集画面を表示
                                                    LLWorkList.setVisibility(View.VISIBLE);
                                                    LLEditData.setVisibility(View.INVISIBLE);

                                                    //編集中タグを削除
                                                    editor.putString("tag","");
                                                    editor.apply();
                                                }
                                            }
        );

        //全ての記録を削除
        Button buttonAllDelete = (Button) findViewById(R.id.ButtonDeleteAll);
        buttonAllDelete.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   new AlertDialog.Builder(MainActivity.this)
                                                           .setTitle("全ての記録を削除します")
                                                           .setMessage("記録と備考を削除しますか？")
                                                           .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                                   //開始時刻を拾い/で分割
                                                                   String[] sp = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "").split("/");
                                                                   for (String gotTime : sp) {
                                                                       if (!gotTime.equals("")) {
                                                                           //各時間の作業名、作業数、終了時刻を削除
                                                                           editor.remove("Name" + gotTime);
                                                                           editor.remove("Count" + gotTime);
                                                                           editor.remove("End" + gotTime);
                                                                       }
                                                                   }
                                                                   //すべての作業開始時刻と備考をヌルにする
                                                                   editor.putString(getString(R.string.TheTimeOfStart), "");
                                                                   editor.putString("bikou", "");
                                                                   editor.apply();

                                                                   //備考欄をヌルにする
                                                                   EditText editTextBikou=findViewById(R.id.editTextBikou);
                                                                   editTextBikou.setText("");

                                                                   //表へ移行
                                                                   EditToList();

                                                                   //表を作成
                                                                   DeleteButtonsMake();
                                                               }
                                                           })
                                                           .setNegativeButton("いいえ",null)
                                                           .create().show();
                                               }
                                           }
        );

        //メーラー起動ボタン
        Button buttonTeisyutsu = (Button) findViewById(R.id.buttonTeisyutsu);
        buttonTeisyutsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DeleteButtonsMake();
                if ((findViewById(R.id.LLBegin)).getVisibility() == View.VISIBLE) {
                    //メールアドレスを保存
                    editor.putString(getString(R.string.Mail), editTextMailAddress.getText().toString());
                    editor.apply();

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SENDTO);

                    //本日の日付
                    Calendar cal = Calendar.getInstance();

                    //備考
                    String bikou = ((EditText) findViewById(R.id.editTextBikou)).getText().toString();
                    Nippou = Nippou + "\n備考\n" + bikou;
                    intent.setType("text/plain");
                    intent.setData(Uri.parse("mailto:" + editTextMailAddress.getText().toString()));
                    intent.putExtra(Intent.EXTRA_SUBJECT, cal.get(Calendar.YEAR) + "年" + (1 + cal.get(Calendar.MONTH)) + "月" + cal.get(Calendar.DATE) + "日　日報");
                    intent.putExtra(Intent.EXTRA_TEXT, Nippou);
                    Nippou = "";
                    DeleteButtonsMake();
                    startActivity(Intent.createChooser(intent, null));

                }
                else
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("作業が終了していません！")
                            .setMessage("作業を終了させて下さい。")
                            .create().show();
            }
        });

        //作業名
        String sher = sharedPreferences.getString(getString(R.string.WorkName), "");

        editTextWorkNames.setText(sher);


        MakeWorkNameSpinner();
        //作業開始ボタン
        Button buttonBegin=findViewById(R.id.buttonBegin);
        buttonBegin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar cal = Calendar.getInstance();
                        //現在時刻
                        int nowTime =cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);

                        boolean sametime=false;
                        String str=sharedPreferences.getString(getString(R.string.TheTimeOfStart),"");

                        //すべての作業開始時刻に対応する作業終了時刻を参照
                        for(String str2:str.split("/"))
                        {
                            //現時刻と作業終了時刻が同じ作業があれば、ヒット
                            if(sharedPreferences.getInt("End"+str2,0)==nowTime)
                                sametime=true;
                        }


                        //作業開始時刻に現時刻と同じものがあるか、作業終了時刻と現時刻が同じ作業があれば
                        if(str.contains(""+nowTime) || sametime)
                        {
                            //注意を出す
                            new AlertDialog.Builder(MainActivity.this )
                                    .setTitle("NG")
                                    .setMessage("時刻が変わるまでお待ちください。")
                                    .create().show();
                        }
                        else//なければ、
                        {

                            //作業を登録し、開始
                            SetData0(MainSpinner.getSelectedItem().toString(), nowTime, 0, -1);
                        }
                        ScrollView scrollView=findViewById(R.id.ScrollView);
                        scrollView.scrollTo(0,scrollView.getBottom());

                    }
                }
        );

        //編集中のタグを拾う
        tagstr=sharedPreferences.getString("tag","");

        //作業名が一つも無ければ
        if(sher.equals(""))
        {
            //作業名編集が面を表示
            LLWorkList.setVisibility(View.VISIBLE);
            LLEditData.setVisibility(View.INVISIBLE);

            LLTeisyutsu.setVisibility(View.INVISIBLE);
        }
            else
        if(tagstr.equals("")) {
            //編集中のタグが無ければ、表を表示
            LLWorkList.setVisibility(View.INVISIBLE);
            LLEditData.setVisibility(View.INVISIBLE);

            LLTeisyutsu.setVisibility(View.VISIBLE);

            DeleteButtonsMake();
            MakeWorkNameSpinner();
        }
        else
        {
            //編集中の作業を表示
            LLWorkList.setVisibility(View.INVISIBLE);
            LLTeisyutsu.setVisibility(View.INVISIBLE);
            LLEditData.setVisibility(View.VISIBLE);
            EditData(tagstr);


        }
        //上書きにチェック
        RadioButton r= (findViewById(R.id.radioButtonUwagaki));
        r.setChecked(true);
    }

    //編集中のデータを削除
    private void Sakujo()
    {
        //タグを分割
        String[] sp=tagstr.split("/");

        //分割された中央の、編集中作業の開始時刻
        String DeleteTagstr=sp[1];

        //記録されているすべての作業開始時刻
        String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");

        //編集中の作業開始時刻をリプレイス
        String newdata = oldData.replace(DeleteTagstr + "/", "");

        //リプレイスした作業開始時刻を登録
        editor.putString(     getString(R.string.TheTimeOfStart), newdata);

        //作業名、作業数、作業終了時刻を削除
        editor.remove("Name" + DeleteTagstr);
        editor.remove("Count" + DeleteTagstr);
        editor.remove("End" + DeleteTagstr);

        //登録
        editor.apply();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    //作業編集画面からリストへ
    private void EditToList()
    {
        //表を表示
        LLTeisyutsu.setVisibility(View.VISIBLE);
        //作業編集画面を非表示
        LLEditData.setVisibility(View.INVISIBLE);
        MakeWorkNameSpinner();

        //タグを削除
        editor.remove("tag");
        editor.apply();

    }

    //データを登録
    private void SetData0(String sagyoumei, int hm, int sagyousuu,int endTime) {

        editor.remove("tag");
        String oldData = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");
        editor.putString("Name" + hm, sagyoumei);
        editor.putInt("Count" + hm, sagyousuu);
        editor.putInt("End" + hm, endTime);
        editor.putString(getString(R.string.TheTimeOfStart), oldData + hm + "/");

        editor.apply();
        LLEditData.setVisibility(View.INVISIBLE);
        LLTeisyutsu.setVisibility(View.VISIBLE);
        DeleteButtonsMake();
        Alert();
    }

    //strが数字だけならばtrue 文字が混入していればfalse;
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

    //作業名をスピナーに登録
    private void MakeWorkNameSpinner() {

        Spinner spneer2 = (Spinner) findViewById(R.id.spinnerWorkNameEdit);

        String workNameFinal =editTextWorkNames.getText().toString();


        String[] workName = workNameFinal.split("\n");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        for (String str : workName)
            if (!str.equals(""))
            {adapter.add(str);

            }

        spneer2.setAdapter(adapter);

        MainSpinner.setAdapter(adapter);

    }

    //表を作成
    private void DeleteButtonsMake() {


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
                data.setEndTime(sharedPreferences.getInt("End"+theTime,-1));
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

                timeM = datacount.getTheTime();
                timeM = (timeM / 100) * 60 + timeM % 100;


                timeM2 = datacount.getEndTime();

                timeM2 = (timeM2 / 100) * 60 + timeM2 % 100;


                timeM2 -= timeM;

                data3 = datacountSearch;
                while (data3 != null && !data3.getWorkName().equals(datacount.getWorkName()))
                    data3 = data3.getNextData();

                data3.AddTimeAndCount(timeM2, 0);

                    timeAll += timeM2;


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

      String tag="/";
        do {
            tableRow = new TableRow(this);
            TheTimeOfStartText = new TextView(this);
            NameText = new TextView(this);
            CountText = new TextView(this);

            TheTimeOfStartText.setGravity(Gravity.END);
            NameText.setGravity(Gravity.END);
            CountText.setGravity(Gravity.END);
            if (firstline) {
                Nippou = "作業名,作業時間,作業数,\n";

                TheTimeOfStartText.setText("　作業時間　");
                NameText.setText("　作業名");

                CountText.setText("　作業数");

                ButtonText2.setText("　編集　");
                firstline = false;


                tableRow.addView(TheTimeOfStartText);
                tableRow.addView(NameText);

                tableRow.addView(CountText);
                tableRow.addView(ButtonText2);

            } else {
                String str = KetaAwase("" + (data2.getTheTime() / 100) )+ ":" +KetaAwase("" +(data2.getTheTime() % 100)) + "-";
                if(data2.getEndTime()<0)str=str+"未定";
                else str =str+KetaAwase (""+(data2.getEndTime() / 100)) + ":" +KetaAwase (""+(data2.getEndTime() % 100) );


                Nippou = Nippou +  data2.getWorkName()+ "," + str + "," + data2.getCount() + "\n";

                TheTimeOfStartText.setText(str + "　");
                NameText.setText(data2.getWorkName());
                str = "" + data2.getCount();


                    CountText.setText(str);

                Button button2 = new Button(this);
                button2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                button2.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

                button2.setTag(data2.getTheTime());
                button2.setText(R.string.ButtonHenshu);

                tag=tag+data2.getTheTime()+"/";

                if(data2.getNextData()!=null)
                    tag=tag+data2.getNextData().getTheTime();

                button2.setTag(tag);

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tagstr="";
                        EditData(view.getTag().toString());

                        editor.putString("tag",view.getTag().toString());
                        editor.apply();
                    }
                });

                tableRow.addView(TheTimeOfStartText);
                tableRow.addView(NameText);
                tableRow.addView(CountText);
                tableRow.addView(button2);


                tag=""+data2.getEndTime()+"/";
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

            Nippou = Nippou + data3.getWorkName() + "," + data3.getTheTime() + "," + data3.getCount() + "\n";

            data3 = data3.getNextData();

        }
        LinearLayout spinner=findViewById(R.id.LLBegin);
        if(tag.equals("-1/"))
        {

            spinner.setVisibility(View.INVISIBLE);


            Button buttonEnd=findViewById(R.id.buttonEnd);
            buttonEnd.setVisibility(View.VISIBLE);

        }
        else
        {

            spinner.setVisibility(View.VISIBLE);

            Button buttonEnd=findViewById(R.id.buttonEnd);
            buttonEnd.setVisibility(View.INVISIBLE);

        }


        Nippou = Nippou + "\n全作業時間," + timeAll;
        MainSpinner.setFocusable(false);

        if (datacountSearch != null)
            datacountSearch.Erase();

        if (data2 != null)
            data2.Erase();


    }
    String tagstr;

    //作業編集画面を作成
   private void EditData(String tag)
    {
        boolean pressEditButton=tagstr.equals("");

        String[] tagdata=tag.split("/");





        tagstr=tag;
        LLEditData.setVisibility(View.VISIBLE);
        LLTeisyutsu.setVisibility(View.INVISIBLE);


        int backTime;
            if(tagdata[0].equals(""))backTime=0;
            else backTime=
        Integer.parseInt(tagdata[0]);

        tag=tagdata[1];

        int nextTime;
        if(tagdata.length<3 || tagdata[2].equals(""))nextTime=0;
        else nextTime=Integer.parseInt(tagdata[2]);

        String TargetWorkName = sharedPreferences.getString("Name" + tag, "");

        String[] searchPos = ((EditText) findViewById(R.id.editTextWorkNames)).getText().toString().split("\n");
        int count = 0;

            while (count<searchPos.length-1 && !searchPos[count].equals(TargetWorkName)) {
                count++;
            }


        Spinner spinner =  findViewById(R.id.spinnerWorkNameEdit);

        spinner.setSelection(count );

        spinner =  findViewById(R.id.spinnerZi);
        spinner.setSelection(Integer.parseInt(tag) / 100);
        spinner =  findViewById(R.id.spinnerHun);
        spinner.setSelection(Integer.parseInt(tag) % 100);

        EditText editText1 = (EditText) findViewById(R.id.editText3);
        int num,num2,numEnd;

            num=sharedPreferences.getInt("Count" + tag, 0);

         num2=sharedPreferences.getInt(getString(R.string.WarikomiSagyousuu), 0);

         numEnd=sharedPreferences.getInt("End"+tag, 0);

         if(pressEditButton)
             num2=num;

        editText1.setText("" + num2);

        TextView TextViewBefore = (TextView) findViewById(R.id.TextViewBefore);
        TextView TextViewBefore2 = (TextView) findViewById(R.id.TextViewBefore2);
        TextViewBefore.setText("作業名："+TargetWorkName


        );
        String str=""+
                KetaAwase(""+(Integer.parseInt(tag) / 100))+"時"+
               KetaAwase (""+Integer.parseInt(tag) % 100)+"分開始　";

        if(numEnd==-1){str=str+"未了　";
            findViewById(R.id.LLEditEndTime).setVisibility(View.INVISIBLE);
        }
        else {
            str = str +KetaAwase (""+(numEnd / 100)) + "時" +
                    KetaAwase(""+(numEnd % 100)) + "分終了　";
            findViewById(R.id.LLEditEndTime).setVisibility(View.VISIBLE);

            spinner =  findViewById(R.id.spinnerEndZi);
            spinner.setSelection(numEnd / 100);
            spinner =  findViewById(R.id.spinnerEndHun);
            spinner.setSelection((numEnd) % 100);


        }

        str=str+"作業数："+num;


        TextViewBefore2.setText(str);

        TextView textViewBack= findViewById(R.id.TextViewBack);

        str="前の仕事の終了時刻：";
        if(backTime==0)
            str=str+"なし";
        else str=str+KetaAwase (""+(backTime / 100)) + "時" +
            KetaAwase(""+(backTime % 100)) + "分";

        textViewBack.setText(str);

        TextView textViewNext=findViewById(R.id.TextViewNext);
        str="次の仕事の開始時刻：";
        if(nextTime<=0)
            str=str+"なし";
        else str=str+KetaAwase (""+(nextTime / 100)) + "時" +
                KetaAwase(""+(nextTime % 100)) + "分";

        textViewNext.setText(str);

    }

    //桁合わせ
    private String KetaAwase(String str)
    {
        if(str.length()==2)return str;
        else return " "+str;

    }


    @Override
    protected void onStop() {
        super.onStop();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//アプリが中断
        @Override
        protected void onPause()
        {
            super.onPause();
            SharedPreferences sharedPreferences=getSharedPreferences(getString(R.string.DaysReport),MODE_PRIVATE);

            SharedPreferences.Editor editor=sharedPreferences.edit();
            String e=editTextWorkNames.getText().toString();

            if(e.length()>0)
            while(e.substring(e.length()-1).equals("\n"))
                e=e.substring(0,e.length()-2);


            if(!e.equals(""))
                while (e.substring(0,1).equals("\n"))
                    e = e.substring(1,e.length() );


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

        //現在の作業が終了したボタン
    private void onClick(View view) {

        String list = sharedPreferences.getString(getString(R.string.TheTimeOfStart), "");
        Calendar cal = Calendar.getInstance();
        String[] sp = list.split("/");
        int nowTime = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);
        for (String theTimeStr : sp) {
            if (sharedPreferences.getInt("End" + theTimeStr, 0) == -1) {
                editor.putInt("End" + theTimeStr, nowTime);
            }

        }
        ScrollView scrollView=findViewById(R.id.ScrollView);
        scrollView.scrollTo(0,scrollView.getBottom());


        editor.apply();
        DeleteButtonsMake();
        new AlertDialog.Builder(this)
                .setTitle("OK")
                .setMessage("作業が終了しました。")
                .create().show();


    }
}
