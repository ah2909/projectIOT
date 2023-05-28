package com.example.demoiotdashboard.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.demoiotdashboard.R;

import com.example.demoiotdashboard.alert.Alerts;
import com.example.demoiotdashboard.mqtt.MQTTHelper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtAirTemp, txtLight;
    SwitchCompat buttonLED, buttonFAN;
    GraphView airLightGraph, airTemperatureGraph;

    Alerts alertDialog;

    LineGraphSeries seriesAirTemperature = new LineGraphSeries<DataPoint>();
    LineGraphSeries seriesLight = new LineGraphSeries<DataPoint>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAirTemp = findViewById(R.id.txtAirTemperature);
        txtLight = findViewById(R.id.txtLight);


//        txtAI = findViewById(R.id.txtAI);
        buttonLED = findViewById(R.id.buttonLED);
        buttonFAN = findViewById(R.id.buttonFAN);

        airLightGraph = (GraphView) findViewById(R.id.graph1);
        airLightGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        airLightGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
        airLightGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        airLightGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        airLightGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        airLightGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

        airTemperatureGraph = (GraphView) findViewById(R.id.graph2);
        airTemperatureGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        airTemperatureGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
        airTemperatureGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        airTemperatureGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        airTemperatureGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        airTemperatureGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);






        buttonLED.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        if (b == true) {
                            sendDataMQTT("Fusioz/feeds/button1", "1");
                        } else {
                            sendDataMQTT("Fusioz/feeds/button1", "0");
                        }
                    }
                });

        buttonFAN.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        if (b == true) {
                            sendDataMQTT("Fusioz/feeds/fan", "1");
                        } else {
                            sendDataMQTT("Fusioz/feeds/fan", "0");
                        }
                    }
                });


        startMQTT();
    }

    public Date convertLocalDateTime (LocalDateTime y){
       Instant instant = y.toInstant(ZoneOffset.of("+07:00"));
       Date out = Date.from(instant);
        return out;
    }




    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }
    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //Log.d("TEST",topic+ "***" +message.toString());
                if (topic.contains("light-sensor-history")){
                    double x = Double.parseDouble(message.toString());
                    LocalDateTime time = LocalDateTime.now();
                    Date y = convertLocalDateTime(time);
                    seriesLight.appendData(new DataPoint(y,x), true, 3, true);
                    airLightGraph.addSeries(seriesLight);
                    airLightGraph.onDataChanged(true, true);
                    txtLight.setText("Brightness: "  + message.toString() + "%");
                }


                else if(topic.contains("sensor3")){

                    double x = Double.parseDouble(message.toString());
                    LocalDateTime time = LocalDateTime.now();
                    Date y = convertLocalDateTime(time);
                    seriesAirTemperature.appendData(new DataPoint(y,x), true, 3, true);
                    airTemperatureGraph.addSeries(seriesAirTemperature);
                    airTemperatureGraph.onDataChanged(true, true);
                    txtAirTemp.setText("Air Temperature: " + message.toString() + "°C" );
                }




                else if(topic.contains("button1")){
                    if (message.toString().equals("1")){
                        buttonLED.setChecked(true);
                        Log.d("TEST",topic + " ON");
                    }else{
                        buttonLED.setChecked(false);
                        Log.d("TEST",topic + " OFF");
                    }
                }

                else if(topic.contains("fan")){
                    if(message.toString().equals("1")) {
                        buttonFAN.setChecked(true);
                        Log.d("TEST",topic + " ON");
                    }else{
                        buttonFAN.setChecked(false);
                        Log.d("TEST",topic + " OFF");
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }


}