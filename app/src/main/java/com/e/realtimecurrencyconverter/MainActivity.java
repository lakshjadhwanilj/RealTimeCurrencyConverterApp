package com.e.realtimecurrencyconverter;

import android.os.AsyncTask;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText amountText;
    ListView countryList;
    ArrayList<String> countryNames;
    String convertTo;
    TextView ansText;
    String symbol = "";

    public class CurrencyDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls){

            String result = "";

            try {

                URL currencyURL = new URL(urls[0]);

                HttpURLConnection currencyURLConnection = (HttpURLConnection) currencyURL.openConnection();

                InputStream in = currencyURLConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int currencyData = reader.read();

                while (currencyData != -1) {

                    char current = (char) currencyData;

                    result += current;

                    currencyData = reader.read();

                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

                return "Failed";

            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                String currency = jsonObject.getString("rates");

                Log.i("Rates: ", currency);

                JSONObject jObject = new JSONObject(currency);

                String convertedCurrency = jObject.getString(convertTo);

                Log.i("Rates", convertedCurrency);

                Double amount = Double.parseDouble(convertedCurrency);

                Double amountReceived = Double.parseDouble(amountText.getText().toString());

                ansText = (TextView) findViewById(R.id.ansText);

                String ans =  "Exchange Rate: " + String.valueOf(new DecimalFormat("0.00").format(amount))+ "\r\n" + "Total: " + symbol + String.valueOf(new DecimalFormat("0.00").format(amount * amountReceived));
                ansText.setText(ans);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }
    }

    public void convertMoney(View view){

        CurrencyDownloadTask task = new CurrencyDownloadTask();

        task.execute("https://openexchangerates.org/api/latest.json?app_id=49cbe47bf7b34346b09e860fc39128b0");

        switch (convertTo) {

            case "INR" :
                symbol = "₹";
                break;
            case "EUR":
                symbol = "€";
                break;
            case "GBP" :
                symbol = "£";
                break;
            case "JPY":
                symbol = "¥";
                break;
            case "AUD":
                symbol = "A$";
                break;
            case "CNY" :
                symbol = "¥";
                break;
            case "CAD":
                symbol = "C$";
                break;
            case "NZD" :
                symbol = "$";
                break;
            case "SGD":
                symbol = "S$";
                break;
            case "ZAR":
                symbol = "R";
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountText = (EditText) findViewById(R.id.amountText);

        countryList = (ListView) findViewById(R.id.countryList);

        countryNames = new ArrayList<String>();

        countryNames.add("Indian Ruppee <INR>");
        countryNames.add("Euro <EUR>");
        countryNames.add("British Pound <GBP>");
        countryNames.add("Japanese Yen <JPY>");
        countryNames.add("Australian Dollar <AUD>");
        countryNames.add("Chinese Yuan <CNY>");
        countryNames.add("Canadian Dollar <CAD>");
        countryNames.add("New Zealand <NZD>");
        countryNames.add("Singapore Dollar <SGD>");
        countryNames.add("South African Rand <ZAR>");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countryNames);

        countryList.setAdapter(arrayAdapter);

        countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                convertTo = countryNames.get(position);

                Pattern p = Pattern.compile("<(.*?)>");

                Matcher m = p.matcher(convertTo);

                while (m.find()) {

                    convertTo = m.group(1);
                    
                }

                System.out.println(convertTo);
            }
        });

    }
}
