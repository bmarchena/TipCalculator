package bryanmarchena.tipcalculator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener{


    private static final int REQUEST_MSG_PHONE = 1;

    //declaring variables for the widgets
    private EditText editTextBillAmount;
    private TextView textViewBillAmount;
    private TextView textViewseekBarLabel;
    private TextView textViewTipLabel;
    private TextView textViewTipAmount;
    private TextView textViewTotalLabel;
    private TextView textViewTotalAmount;
    private SeekBar seekBarTip;
    private TextView textViewPerPersonTotal;
    private double tip;
    private double total;
    private double perPersonTotal;


    //spinner and adapter
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private int numPeople = 1;

    //radio group and buttons
    private RadioGroup roundOptions;
    private RadioButton roundNo;
    private RadioButton roundTip;
    private RadioButton roundTotal;


    //declare the variables for the calculations
    private double billAmount = 0.0;
    private double percent = .15;

    //set the number formats to be used for the $ amounts , and % amounts
    private static final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat =
            NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add listeners to widgets
        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);
        editTextBillAmount.addTextChangedListener((TextWatcher)this);


        //getting references to views by ID
        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);
        textViewseekBarLabel = (TextView)findViewById(R.id.textView_seekBarLabel);
        textViewTipLabel =  (TextView)findViewById(R.id.textView_tipLabel);
        textViewTotalLabel = (TextView)findViewById(R.id.textView_totalLabel);
        textViewTipAmount = (TextView)findViewById(R.id.textView_Tip);
        textViewTotalAmount = (TextView)findViewById(R.id.textView_totalAmount);
        seekBarTip = (SeekBar)findViewById(R.id.tip_seekBar);
        spinner = (Spinner)findViewById(R.id.spinner_numPeople);
        adapter = ArrayAdapter.createFromResource(this,R.array.spinner_labels,R.layout.support_simple_spinner_dropdown_item);
        roundOptions = (RadioGroup)findViewById(R.id.radio_group);
        roundNo = (RadioButton)findViewById(R.id.radio_no);
        roundTip = (RadioButton)findViewById(R.id.radio_tip);
        roundTotal = (RadioButton)findViewById(R.id.radio_total);
        textViewPerPersonTotal = (TextView)findViewById(R.id.textView_perPersonTotal);

        //setting seekbar listener
        seekBarTip.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener)this);

        //initializing text values for tip and total
        textViewTipAmount.setText(currencyFormat.format(0));
        textViewTotalAmount.setText(currencyFormat.format(0));


        //setting adapter to spinner
        if(spinner != null){
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
        }



    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //overriding text changed method to obtain the bill amount
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        billAmount = Double.parseDouble(charSequence.toString()) / 100;
        textViewBillAmount.setText(currencyFormat.format(billAmount));
        calculate();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress / 100.0; //calculate percent based on seeker value
        calculate();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    // calculate and display tip and total amounts depending on round option
    private void calculate() {
        //no rounding
        if(roundNo.isChecked()) {
            tip = billAmount * percent;
            total = tip + billAmount;
            perPersonTotal = total / numPeople;
        }

        //tip rounded up to nearest dollar
        else if(roundTip.isChecked()){
            tip = billAmount * percent;
            tip = Math.ceil(tip);
            total = tip + billAmount;
            perPersonTotal = total / numPeople;
        }

        //total rounded up to nearest dollar
        else if (roundTotal.isChecked()){
            tip = billAmount * percent;
            total = tip + billAmount;
            total = Math.ceil(total);
            perPersonTotal = total / numPeople;
        }


        // updating the percentage of tip textview
        textViewseekBarLabel.setText(percentFormat.format(percent));

        //updating the tip amount textview
        textViewTipAmount.setText(currencyFormat.format(tip));
        //use the tip example to do the same for the Total
        textViewTotalAmount.setText(currencyFormat.format(total));

        //updating the per person total textview
        textViewPerPersonTotal.setText(currencyFormat.format(perPersonTotal));
    }


    //overriding spinner item select method to obtain the number of people
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        numPeople = Integer.parseInt(parent.getItemAtPosition(position).toString());
        calculate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void checkButton(View v) {
        calculate();
    }

    //inflating options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //overriding item selected method for options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //if user clicks info
        if (id == R.id.info_icon) {
            AlertDialog.Builder alertDialog =
                    new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle(R.string.info_title);
            alertDialog.setMessage(R.string.info_txt);
            alertDialog.show();
            return true;
        }

        //if user clicks share
        else if (id == R.id.share_icon){
            String msg = "Bill = " + currencyFormat.format(billAmount) + "\nTip = " + currencyFormat.format(tip)
                    + "\nTotal = " + currencyFormat.format(total) + "\n\nTotal Per Person = " + currencyFormat.format(perPersonTotal);
            
            Intent msgIntent = new Intent(Intent.ACTION_VIEW);
            msgIntent.setData(Uri.parse("sms:"));
            msgIntent.putExtra("sms_body", msg);
            startActivity(msgIntent);

            if (msgIntent.resolveActivity(getPackageManager()) != null);
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.SEND_SMS}, REQUEST_MSG_PHONE);
                } else {
                    startActivity(msgIntent);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
