package com.cmpe277.integer_calculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Stack;
import java.lang.String;
import android.content.res.Configuration;
import android.text.InputType;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.support.v4.view.GravityCompat;
import android.app.Dialog;
import android.widget.NumberPicker;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Button Pad
    private Button add;
    private Button subtract;
    private Button multiply;
    private Button divide;
    private Button equal;
    private Button delete;
    private Button left_parenthesis;
    private Button right_parenthesis;
    private Button one;
    private Button two;
    private Button three;
    private Button four;

    //EditText
    private EditText showtext;
    private String OperateSum="";

    //Number Gen Related
    private int[] numberStore = new int[4];
    private int max = 9, min = 1;
    private String bt_one = "";
    private String bt_two = "";
    private String bt_three = "";
    private String bt_four = "";

    //Count
    private int succee_count = 0, attempt_count = 1, skip_count = 0, sec = 0;
    private TextView show_attempt;
    private TextView show_succee;
    private TextView show_skipped;
    private TextView show_timer;
    Thread timer;

    //Menu
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView()
    {
        one=(Button) findViewById(R.id.num_one);
        two=(Button) findViewById(R.id.num_two);
        three=(Button) findViewById(R.id.num_three);
        four=(Button) findViewById(R.id.num_four);
        add=(Button) findViewById(R.id.add);
        subtract=(Button) findViewById(R.id.subtract);
        multiply=(Button) findViewById(R.id.multiply);
        divide=(Button) findViewById(R.id.divide);
        left_parenthesis=(Button) findViewById(R.id.left_parenthesis);
        right_parenthesis=(Button) findViewById(R.id.right_parenthesis);
        delete=(Button) findViewById(R.id.delete);
        equal=(Button) findViewById(R.id.equal);

        show_attempt=(TextView) findViewById(R.id.attempt_num);
        show_attempt.setText(String.valueOf(attempt_count));
        show_succee=(TextView) findViewById(R.id.succeed_num);
        show_succee.setText(String.valueOf(succee_count));
        show_skipped=(TextView) findViewById(R.id.skipped_num);
        show_skipped.setText(String.valueOf(skip_count));
        show_timer=(TextView) findViewById(R.id.time_num);

        //timer Thread
        timer=new Thread() {
            @Override
            public void run() {
                while(!isInterrupted()){
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sec++;
                                String time_count = String.format("%02d:%02d", sec / 100, sec % 100);
                                show_timer.setText(time_count);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.start();

        //Text View
        showtext=(EditText) findViewById(R.id.result_text);
        showtext.setCursorVisible(false);

        //Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.show_me:
                                menuItem.setChecked(true);
                                mDrawerLayout.closeDrawers();

                                if (solve(numberStore[0], numberStore[1], numberStore[2], numberStore[3])) {
                                    AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("There is a solution. Please continue!")
                                            .setPositiveButton("Cotinue", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                } else {
                                    AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("Sorry, there are actually no solutions!")
                                            .setPositiveButton("Next Puzzle", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    OperateSum="";
                                                    showtext.setText(OperateSum);
                                                    resetNumber();
                                                    skip_count++;
                                                    show_skipped.setText(String.valueOf(skip_count));
                                                    attempt_count=1;
                                                    show_attempt.setText(String.valueOf(attempt_count));
                                                    sec = 0;
                                                    String time_count = String.format("%02d:%02d", sec / 100, sec % 100);
                                                    show_timer.setText(time_count);
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                return true;

                            case R.id.num_picker:
                                menuItem.setChecked(true);
                                mDrawerLayout.closeDrawers();
                                new_number_picker();
                                return true;
                        }
                        return true;
                    }
                });

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        } else {
            toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        }
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    private void initEvent() {

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);
        left_parenthesis.setOnClickListener(this);
        right_parenthesis.setOnClickListener(this);
        delete.setOnClickListener(this);

        //Done button
        equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update attempt_count
                attempt_count++;
                show_attempt.setText(String.valueOf(attempt_count));

                //evaluate result
                equal.setEnabled(false);
                if(!evresult(OperateSum)){
                    //Snackbar
                    android.support.design.widget.Snackbar.make(view, "Incorrect. Please try again!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                } else {
                    //Dialog
                    AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Binggo! "+OperateSum+"=24")
                            .setPositiveButton("Next Puzzle", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //reset input
                                    OperateSum="";
                                    showtext.setText(OperateSum);

                                    //reset numpad
                                    resetNumber();

                                    //update succee_count
                                    succee_count++;
                                    show_succee.setText(String.valueOf(succee_count));

                                    //reset attempt_count
                                    attempt_count=1;
                                    show_attempt.setText(String.valueOf(attempt_count));

                                    //reset timer
                                    sec = 0;
                                    String time_count = String.format("%02d:%02d", sec / 100, sec % 100);
                                    show_timer.setText(time_count);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        
        //generate 4 numbers at start
        resetNumber();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.game_reset:
                OperateSum="";
                showtext.setText(OperateSum);

                one.setEnabled(true);
                two.setEnabled(true);
                three.setEnabled(true);
                four.setEnabled(true);
                return true;

            case R.id.game_skipper:
                OperateSum="";
                showtext.setText(OperateSum);

                resetNumber();

                skip_count++;
                show_skipped.setText(String.valueOf(skip_count));

                attempt_count=1;
                show_attempt.setText(String.valueOf(attempt_count));

                sec = 0;
                String time_count = String.format("%02d:%02d", sec / 100, sec % 100);
                show_timer.setText(time_count);

                return true;

            case R.id.num_picker:
                new_number_picker();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Number Generator
    private void resetNumber() {
        do {
            randomGenerator();

        } while (!solve(numberStore[0], numberStore[1], numberStore[2], numberStore[3]));

        bt_one = Integer.toString(numberStore[0]);
        bt_two = Integer.toString(numberStore[1]);
        bt_three = Integer.toString(numberStore[2]);
        bt_four = Integer.toString(numberStore[3]);

        one.setText(bt_one);
        two.setText(bt_two);
        three.setText(bt_three);
        four.setText(bt_four);

        one.setEnabled(true);
        two.setEnabled(true);
        three.setEnabled(true);
        four.setEnabled(true);
    }

    private void setNumber() {
        bt_one = Integer.toString(numberStore[0]);
        bt_two = Integer.toString(numberStore[1]);
        bt_three = Integer.toString(numberStore[2]);
        bt_four = Integer.toString(numberStore[3]);

        one.setText(bt_one);
        two.setText(bt_two);
        three.setText(bt_three);
        four.setText(bt_four);

        one.setEnabled(true);
        two.setEnabled(true);
        three.setEnabled(true);
        four.setEnabled(true);

        //reset input
        OperateSum="";
        showtext.setText(OperateSum);

        //update skip_count
        skip_count++;
        show_skipped.setText(String.valueOf(skip_count));

        //reset attempt_count
        attempt_count=1;
        show_attempt.setText(String.valueOf(attempt_count));

        //reset timer
        sec = 0;
        String time_count = String.format("%02d:%02d", sec / 100, sec % 100);
        show_timer.setText(time_count);
    }

    private void randomGenerator(){
        for(int i = 0; i < 4; i++){
            numberStore[i] = (int)(Math.random() * ((max - min) + 1)) + min;
        }
    }

    private void new_number_picker(){

        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np1 = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np1.setMaxValue(9);
        np1.setMinValue(1);
        np1.setWrapSelectorWheel(false);
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
            }
        });
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMaxValue(9);
        np2.setMinValue(1);
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
            }
        });
        final NumberPicker np3 = (NumberPicker) d.findViewById(R.id.numberPicker3);
        np3.setMaxValue(9);
        np3.setMinValue(1);
        np3.setWrapSelectorWheel(false);
        np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
            }
        });
        final NumberPicker np4 = (NumberPicker) d.findViewById(R.id.numberPicker4);
        np4.setMaxValue(9);
        np4.setMinValue(1);
        np4.setWrapSelectorWheel(false);
        np4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
            }
        });
        b1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                numberStore[0] = (int)np1.getValue();
                numberStore[1] = (int)np2.getValue();
                numberStore[2] = (int)np3.getValue();
                numberStore[3] = (int)np4.getValue();
                setNumber();
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.num_one:
                OperateSum=AddSum(String.valueOf(bt_one).charAt(0));
                showtext.setText(OperateSum);
                break;
            case R.id.num_two:
                OperateSum=AddSum(String.valueOf(bt_two).charAt(0));
                showtext.setText(OperateSum);
                break;
            case R.id.num_three:
                OperateSum=AddSum(String.valueOf(bt_three).charAt(0));
                showtext.setText(OperateSum);
                break;
            case R.id.num_four:
                OperateSum=AddSum(String.valueOf(bt_four).charAt(0));
                showtext.setText(OperateSum);
                break;

            case R.id.add:
                OperateSum=AddSum('+');
                showtext.setText(OperateSum);
                break;
            case R.id.subtract:
                OperateSum=AddSum('-');
                showtext.setText(OperateSum);
                break;
            case R.id.multiply:
                OperateSum=AddSum('*');
                showtext.setText(OperateSum);
                break;
            case R.id.divide:
                OperateSum=AddSum('/');
                showtext.setText(OperateSum);
                break;
            case R.id.left_parenthesis:
                OperateSum=AddSum('(');
                showtext.setText(OperateSum);
                break;
            case R.id.right_parenthesis:
                OperateSum=AddSum(')');
                showtext.setText(OperateSum);
                break;
            case R.id.delete:
                if(OperateSum.length()>=1)
                {
                    char c = OperateSum.charAt(OperateSum.length()-1);
                    if (Character.isDigit(c)) {
                        if (one.getText().charAt(0) == c && (!one.isEnabled())){
                            one.setEnabled(true);
                        } else if (two.getText().charAt(0) == c && (!two.isEnabled())) {
                            two.setEnabled(true);
                        } else if (three.getText().charAt(0) == c && (!three.isEnabled())) {
                            three.setEnabled(true);
                        } else if (four.getText().charAt(0) == c && (!four.isEnabled())) {
                            four.setEnabled(true);
                        }
                    }
                    OperateSum=OperateSum.substring(0,OperateSum.length()-1);
                }
                showtext.setText(OperateSum);
            default:
                break;
        }
    }

    public String AddSum(char c)
    {
        if (Character.isDigit(c)){
            if (one.getText().charAt(0) == c && one.isEnabled()){
                one.setEnabled(false);
            } else if (two.getText().charAt(0) == c && two.isEnabled()) {
                two.setEnabled(false);
            } else if (three.getText().charAt(0) == c && three.isEnabled()) {
                three.setEnabled(false);
            } else if (four.getText().charAt(0) == c && four.isEnabled()) {
                four.setEnabled(false);
            }
        }
        OperateSum=OperateSum+String.valueOf(c);
        equal.setEnabled(true);
        return OperateSum;
    }

    //Validation Part
    public int calc(int op2, int op1, char ch) {
        switch(ch) {
            case '-': return op1 - op2;
            case '+': return op1 + op2;
            case '/': return op1 / op2;
            case '*': return op1 * op2;
        }
        return 0;
    }

    public boolean higherPriority(char op1, char op2) {
        if ((op1 =='*') || (op1 =='/')) {
            return true;
        }
        if ((op2 =='+') || (op2 =='-')) {
            return true;
        }
        return false;
    }

    public boolean evresult(String exp) {
        if(exp.length() == 0) {
            return false;
        }
        Stack<Integer> st = new Stack<>();
        Stack<Character> op = new Stack<>();
        int digit = 0;
        boolean hasDigit = false;
        for (int i = 0; i < exp.length(); i++) {
            if (Character.isDigit(exp.charAt(i))) {
                hasDigit = true;
                digit = digit*10 + (exp.charAt(i) - '0');
            } else {
                if(hasDigit) {
                    hasDigit = false;
                    st.push(digit);
                    digit = 0;
                }
                if (exp.charAt(i) == '(') {
                    op.push('(');
                } else if(exp.charAt(i) == ')') {
                    while (op.peek() != '(') {
                        st.push(calc(st.pop(), st.pop(), op.pop()));
                    }
                    op.pop();

                } else {
                    while (!op.isEmpty() && op.peek() != '(' && higherPriority(op.peek(), exp.charAt(i))) {
                        st.push(calc(st.pop(), st.pop(), op.pop()));
                    }

                    op.push(exp.charAt(i));
                }
            }
        }
        if(hasDigit)
            st.push(digit);
        while(!op.isEmpty()) {
            st.push(calc(st.pop(), st.pop(), op.pop()));
        }
        return st.peek() == 24;
    }

     //number validation checker from professor
    public static String getSolution(int a, int b, int c, int d) {
        int[] n = { a, b, c, d };
        char[] o = { '+', '-', '*', '/' };
        for (int w = 0; w < 4; w++) {
            for (int x = 0; x < 4; x++) {
                if (x == w)
                    continue;
                for (int y = 0; y < 4; y++) {
                    if (y == x || y == w)
                        continue;
                    for (int z = 0; z < 4; z++) {
                        if (z == w || z == x || z == y)
                            continue;
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                for (int k = 0; k < 4; k++) {
                                    String result = eval(n[w], n[x], n[y], n[z], o[i], o[j], o[k]);
                                    if (null != result) {
                                        return result;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String eval(int a, int b, int c, int d, char x, char y, char z) {
        try {
            if (bingo(eval(eval(eval(a, x, b), y, c), z, d))) {
                return "((" + a + x + b + ")" + y + c + ")" + z + d;
            }
            if (bingo(eval(eval(a, x, eval(b, y, c)), z, d))) {
                return "(" + a + x + "(" + b + y + c + "))" + z + d;
            }
            if (bingo(eval(a, x, eval(eval(b, y, c), z, d)))) {
                return "" + a + x + "((" + b + y + c + ")" + z + d + ")";
            }
            if (bingo(eval(a, x, eval(b, y, eval(c, z, d))))) {
                return "" + a + x + "(" + b + y + "(" + c + z + d + ")" + ")";
            }
            if (bingo(eval(eval(a, x, b), y, eval(c, z, d)))) {
                return "((" + a + x + b + ")" + y + "(" + c + z + d + "))";
            }
        } catch (Throwable t) {
        }
        return null;
    }

    private static boolean bingo(double x) {
        return Math.abs(x - 24) < 0.0000001;
    }

    private static double eval(double a, char x, double b) {
        switch (x) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            default:
                return a / b;
        }
    }

    private static boolean solve(int a, int b, int c, int d) {
        String result = getSolution(a, b, c, d);
        return null != result;
    }
}