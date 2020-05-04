package com.first.ui;
//
//import com.molot.lowlevel.rw.ReaderWriter;
//import com.molot.lowlevel.rw.ReaderWriterFactory;
//import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataProcessor;
//import com.molot.ui.setting.ParamsSettings;
//import com.molot.util.Params;

import com.first.lowLevel.Ethernet;
import com.first.lowLevel.HorizonDevice;
import com.first.lowLevel.command.HorizonCommands;
import com.first.ui.setting.ParamsSettings;
import com.first.util.Params;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingController implements ParamsSettings  {
    private List<ParamsSettings> settings = new ArrayList<>();
    private HorizonDevice horizonDevice;

   // private ReaderWriter readerWriter;
   // private ByteDataProcessor byteDataProcessor;
    private int currentByteIndex;
    Image Ok = new Image("/images/check.png");
    Image notOk = new Image("/images/close.png");
   // private String IP = Params.SETTINGS.getString("ethernet-ip-address");


    @FXML
    private TextField textFieldIP;

    @FXML
    private Button buttonChangeIP;

    @FXML
    private Button connectSettings;

    @FXML
    private ImageView testIpImageView;

    @FXML
    private Label freqRxLabel ;

    @FXML
    private Label freqTxLabel;

    @FXML
    private TextField textFieldPort;

    @FXML
    private ChoiceBox typeRxChoiceBox;

    @FXML
    private ChoiceBox typeTxChoiceBox;

    @FXML
    public void initialize() {

        System.out.println("initialize() setting");
        restoreAll(Params.SETTINGS);
        buttonChangeIP.setVisible(false);

        typeRxChoiceBox.getItems().add("Комплект");
        typeRxChoiceBox.getItems().add("P160");
        typeRxChoiceBox.getItems().add("Р155");
        typeRxChoiceBox.getItems().add("Р399");

        typeTxChoiceBox.getItems().add("Комплект");
        typeTxChoiceBox.getItems().add("P140");
        typeTxChoiceBox.getItems().add("ПКМ");

        testIP();

        typeRxChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String type = typeRxChoiceBox.getValue().toString();
                if(type == "Комплект"){
                    Platform.runLater(()->{
                        MainController.freqRx.setVisible(true);
                        MainController.freqRx.setText(Params.SETTINGS.getString("rx_freq", "44444"));
                        MainController.freqRxLab.setVisible(true);

                    });
                } else {
                    MainController.freqRx.setVisible(false);
                    MainController.freqRxLab.setVisible(false);
                    if(type == "P160" || type == "Р155" ){

                        Params.SETTINGS.putString("rx_freq", "121000");
                    }else if(type == "Р399") Params.SETTINGS.putString("rx_freq", "215000");
                }
            }
        });

        typeTxChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String type = typeTxChoiceBox.getValue().toString();
                if(type == "Комплект"){
                    Platform.runLater(()->{
                        MainController.freqTx.setVisible(true);
                        MainController.freqTx.setText(Params.SETTINGS.getString("tx_freq", "44444"));
                        MainController.freqTxLab.setVisible(true);
                    });
                } else {
                    MainController.freqTx.setVisible(false);
                    MainController.freqTxLab.setVisible(false);
                    if(type == "P140" || type == "ПКМ" ){
                        Params.SETTINGS.putString("tx_freq", "122000");

                    }
                }


            }
        });

        textFieldIP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                testIP();
            }
        });
    }

    public void testIP() {

        try {
            String ipAddress = textFieldIP.getText();
            InetAddress inet = InetAddress.getByName(ipAddress);

            if (inet.isReachable(500)) {
                testIpImageView.setImage(Ok);
                connectSettings.setVisible(true);
            } else {
                if(horizonDevice == null || !horizonDevice.isConnected() ){
                    connectSettings.setVisible(false);
                    buttonChangeIP.setVisible(false);

                }
                testIpImageView.setImage(notOk);
            }
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }
    }
    public void connectEth(ActionEvent actionEvent) {

        ConnectUnConnect();

    }
    public void setIP(ActionEvent actionEvent) throws InterruptedException {

        String ip = textFieldIP.getText();
        String mask = "255.255.255.0";
        int port = 80;
        String gateway = "192.168.0.1";

        horizonDevice.writeCommand(HorizonCommands.setEthernetParams(ip, mask, port, gateway));
        Params.SETTINGS.putString("ethernet-ip-address", textFieldIP.getText());
        Params.SETTINGS.save();
        //horizonDevice.close();
        String ip1 = Params.SETTINGS.getString("ethernet-ip-address", "192.168.0.5");
        textFieldIP.setText(ip1);
        ConnectUnConnect();
        Thread.sleep(2000);

       //inf("Встановлено");
        testIP();
        testIP();

    }
    private void ConnectUnConnect() {
        String con = "-fx-background-color: #00cd00";
        if (connectSettings.getStyle() != con) {
            connectToEthernet();
            Platform.runLater(()->{
                connectSettings.setText("Відключитись");
                connectSettings.setStyle("-fx-background-color: #00cd00");
                buttonChangeIP.setVisible(true);


            });

        } else {
            horizonDevice.close();
            Platform.runLater(()-> {
                connectSettings.setText("Підключитись");
                connectSettings.setStyle("-fx-background-color: #c0ae9d");
                buttonChangeIP.setVisible(false);


            });
        }
    }
    private void connectToEthernet() {
        try {

           // String ip = Params.SETTINGS.getString("ethernet-ip-address", "192.168.0.5");
            String ip = textFieldIP.getText();
            int port = 80;

            Map<String, String> options = new HashMap<>();
            options.put("ip", ip);
            options.put("port", Integer.toString(port));

            horizonDevice = new Ethernet();
            //horizonDevice.addEventListener(this);

            horizonDevice.init(options);

            if (!horizonDevice.isConnected()) {
               // JOptionPane.showMessageDialog(null, "Не вдається під'єднатись по вказаній IP адресі");
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            //JOptionPane.showMessageDialog(null, "Не вдається під'єднатись!");
        }
    }
    public void shutdown() {
        if (horizonDevice != null && horizonDevice.isConnected()) {
            horizonDevice.close();
        }
        saveAll(Params.SETTINGS);
        System.out.println("Stop setting");
    }
    private void inf(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(text);
        alert.setHeaderText(null);
        alert.setContentText("Значення");
        alert.showAndWait();
    }

    @Override
    public void saveAll(Params params) {
        String typeRx = typeRxChoiceBox.getValue().toString();
        Params.SETTINGS.putString("type_rx", typeRx);

        String typeTx = typeTxChoiceBox.getValue().toString();
        Params.SETTINGS.putString("type_tx", typeTx);

        //Save ethernet params 1
        Params.SETTINGS.putString("ethernet_ip", textFieldIP.getText());
        Params.SETTINGS.putString("ethernet_port", textFieldPort.getText());
    }

    @Override
    public void restoreAll(Params params) {

        //Restore Ethernet params 1
        textFieldIP.setText(Params.SETTINGS.getString("ethernet-ip-address", "192.168.0.1"));
        textFieldPort.setText(Params.SETTINGS.getString("ethernet-port", "80"));

        //Restore RX TX type device
        typeRxChoiceBox.setValue(Params.SETTINGS.getString("type_rx", "P160"));
        typeTxChoiceBox.setValue(Params.SETTINGS.getString("type_tx", "P160"));

        //Restore RX TX freq
//        freqRxTextField.setText(Params.SETTINGS.getString("rx_freq", "128000"));
       // freqTxTextField.setText(Params.SETTINGS.getString("tx_freq", "128000"));



        //Restore bit inversion
//        rxInverse.setSelected(Params.SETTINGS.getBoolean("rx_inverse", true));
//        txInverse.setSelected(Params.SETTINGS.getBoolean("tx_inverse", false));
//
//        //Save check virial device
//        checkVirial.setSelected(Params.SETTINGS.getBoolean("checkVirial", false));

        //Restore work speed
        //WorkSpeed workSpeed = WorkSpeed.valueOf (Params.SETTINGS.getString("modem_speed", WorkSpeed.speed250.name()));
        //	speedCombobox.setSelectedItem(workSpeed);
    }
}
