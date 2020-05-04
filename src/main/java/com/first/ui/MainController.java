package com.first.ui;
//
//import com.molot.Core;
//import com.molot.lowlevel.rw.PackageData.SendData;
//import com.molot.lowlevel.rw.ReaderWriter;
//import com.molot.lowlevel.rw.ReaderWriterFactory;

import com.first.Core;
import com.first.PackageData.SendData;
import com.first.lowLevel.HorizonDevice;
import com.first.lowLevel.command.HorizonCommands;
import com.first.lowLevel.enums.WorkMode;
import com.first.ui.setting.ParamsSettings;
import com.first.util.Params;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController implements ParamsSettings {

    //public static ReaderWriter newReaderWriter;

    int numberCorespon;
    private int currentSpeed;

    private double[] tmpValues;
    byte[] buf;


    public byte index = 0;
    public int flagPoint = 0;

    public boolean frequencyFlag = false;
    public boolean filterFlag = false;
    public boolean serverFlag = false;
    public int freq = 0;
    public int filter = 0;

    private int step_count = 0;

    private int step = 0;
    String point = "1024";
    javafx.scene.image.Image image1;

    double scaleGra = 1;
    int flagDraw = 0;
    public static int frequencyWinradio = 0;
    public static int filterWinradio = 0;

    private HorizonDevice[] horizonDevice;
    private HorizonDevice horizonDevice1;
    private boolean running = true;
    private String typeRx;
    private String typeTx;
    private String mode;

    @FXML
    private TextArea messageHistory;

    @FXML
    private TextField correspondentNumber;

    @FXML
    private TextField freq1;

    @FXML
    private ChoiceBox<String> command;

    @FXML
    private ChoiceBox<String> modeOperation;


    @FXML
    public Label freqRxLabel;

    @FXML
    private TextField freqRxTextField;

    @FXML
    private Label freqTxLabel;

    @FXML
    private TextField freqTxTextField;

    @FXML
    private TextField serviceMessage;
    @FXML
    private Button connectButton;

    public static TextField freqTx;
    public static TextField freqRx;
    public static Label freqTxLab;
    public static Label freqRxLab;


    @FXML
    public void initialize() {
        System.out.println("initialize()");
        MainController.freqTx = freqTxTextField;
        MainController.freqRx = freqRxTextField;
        MainController.freqTxLab = freqTxLabel;
        MainController.freqRxLab = freqRxLabel;

        freqRxTextField.setVisible(false);
        freqRxLabel.setVisible(false);
        freqTxTextField.setVisible(false);
        freqTxLabel.setVisible(false);

        restoreAll(Params.SETTINGS);


        for (int i = 1; i <= 9; i++) {
            command.getItems().add(String.valueOf(i));
        }

        modeOperation.getItems().add(String.valueOf(WorkMode.DBPSK100));
        modeOperation.getItems().add(String.valueOf(WorkMode.DBPSK250));
        modeOperation.getItems().add(String.valueOf(WorkMode.DBPSK500));
        modeOperation.getItems().add(String.valueOf(WorkMode.DBPSK1000));


        if(typeRx.equals("Комплект")){
            freqRxTextField.setVisible(true);
            freqRxTextField.setText(Params.SETTINGS.getString("rx_freq", "44444"));
            freqRxLabel.setVisible(true);
        }
        if(typeTx.equals("Комплект") ){
            freqTxTextField.setVisible(true);
            freqTxTextField.setText(Params.SETTINGS.getString("tx_freq", "44444"));
            freqTxLabel.setVisible(true);

        }

        modeOperation.setValue(mode);
        Core.getInstance().setRunning(true);
       // new UpdateUIThread().start();


        modeOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String type = modeOperation.getValue().toString();
                type = type.replace("-", "");
                Params.SETTINGS.putString("work_mode", type);

            }
        });

        freqRxTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Params.SETTINGS.putString("rx_freq", freqRxTextField.getText());
                if(horizonDevice != null){
                    horizonDevice[0].writeCommand(HorizonCommands.setRxDemodulationFrequency(Integer.parseInt(freqRxTextField.getText())));
                }

            }
        });

        freqTxTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Params.SETTINGS.putString("tx_freq", freqTxTextField.getText());
                if(horizonDevice != null){
                    horizonDevice[0].writeCommand(HorizonCommands.setTxModulationFrequency(Integer.parseInt(freqTxTextField.getText())));
                }
            }
        });

    }



    public void connecting(ActionEvent actionEvent) {

        String con = "-fx-background-color: #00cd00";
        if (connectButton.getStyle() != con) {
            horizonDevice = Core.getInstance().init();
            Platform.runLater(()->{
                connectButton.setText("Відключитись");
                connectButton.setStyle("-fx-background-color: #00cd00");

            });

        } else {
            connectButton.setDisable(true);
            //Save work speed
//            WorkSpeed workSpeed = (WorkSpeed) speedCombobox.getSelectedItem();
//            Params.SETTINGS.putString("modem_speed", workSpeed.name());

            Params.SETTINGS.save();
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Core.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                Platform.runLater(()-> {
                    connectButton.setText("Підключитись");
                    connectButton.setDisable(false);
                    connectButton.setStyle("-fx-background-color: #c0ae9d");
                });

            }).start();

            return;

        }


    }

    public void openFile(ActionEvent actionEvent) throws IOException {
        Core.setGeneratorIQServerModeTx();
    }

    public void sendingMessage(ActionEvent actionEvent) {
        if (correspondentNumber.getText().matches("\\d{3}")) {
            SendData sendData = new SendData();
            sendData.setNumberCorespondent(correspondentNumber.getText());
            if (command.getValue() != null) {
                sendData.setCommand(Integer.parseInt(command.getValue()));
            }
            boolean freqBoolean = validation(freq1);
            boolean serviceMessageBoolean = validation(serviceMessage);
            String freqString = freq1.getText();
            String serviceMessageString = serviceMessage.getText();
            if (freqBoolean && serviceMessageBoolean && command.getValue() == null) {
                inf2();
            } else if (freqBoolean) {
                if (serviceMessageBoolean) {
                    if (command.getValue() != null) {
                        clear();
                        sendData.parsData();
                    }
                } else if (checkMather(serviceMessage, "\\d{3,78}")) {
                    sendData.setServiceMessage(serviceMessageString);
                    clear();
                    sendData.parsData();
                } else {
                    inf("Службове повідомлення");
                }
            } else if (checkMather(freq1, "\\d{5}")) {
                sendData.setFrequencyTx(freqString);
                if (validation(serviceMessage)) {
                    clear();
                    sendData.parsData();
                } else if (checkMather(serviceMessage, "\\d{3,78}")) {
                    sendData.setServiceMessage(serviceMessageString);
                    clear();
                    sendData.parsData();
                } else {
                    inf("Службове повідомлення");
                }
            } else {
                inf("Перевідна група");
            }
        } else {
            inf("Номер кореспондента");
        }

    }

    private boolean checkMather(TextField textField, String matches) {

        return textField.getText().replaceAll(" ", "").matches(matches);
    }

    private boolean validation(TextField textField) {
        return textField.getText().isEmpty();
    }

    public void setting(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Setting.fxml"));
        Parent root = loader.load();
        SettingController controller = loader.getController();
        Stage stage = new Stage();
        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnHidden(event -> controller.shutdown());
        stage.showAndWait();
    }



    @Override
    public void saveAll(Params params) {

    }

    @Override
    public void restoreAll(Params params) {
        typeRx = Params.SETTINGS.getString("type_rx", "Комплект");
        typeTx = Params.SETTINGS.getString("type_tx", "Комплект");
        mode = Params.SETTINGS.getString("work_mode", "DBPSK100");
        mode = WorkMode.valueOf(Params.SETTINGS.getString("work_mode", "erwrw")).toString();

    }

    class UpdateUIThread extends Thread {
        @Override
        public void run() {
            while (running) {
//                if (Core.isReady()) {
//
//                    updateMessages();
//                    updatePacketStats();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }

        private void updateMessages() {
//            final MessageModel model = Core.messages().getMessageModel();
//            if (model.isModelChanged()) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        setMessageHistory(model);
//                        model.gotAllData();
//                        messageHistoryScroll.getVerticalScrollBar().setValue(messageHistoryScroll.getVerticalScrollBar().getMaximum() * 2);
//                        messageHistoryScroll.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
//
//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                messageHistory.updateUI();
//                                messageHistoryScroll.updateUI();
//                                messageHistoryScroll.repaint();
//                            }
//                        });
//                    }
//                });
//            }
        }

        private void updatePacketStats() {
//            int sentPacketCount = Core.stats().getInt("sent_packet_count");
//            int totalPacketCount = Core.stats().getInt("received_packet_count");
//            int brokenPacketCount = Core.stats().getInt("received_bad_packet_count");
//            int repairedPacketCount = Core.stats().getInt("received_bad_and_corrected_packet_count");
//
//            //"Пакети, передано/прийнято, помилкових/невиправлених:"
//            String message = sentPacketCount + "/" + totalPacketCount + ", " + brokenPacketCount + "/" + (brokenPacketCount - repairedPacketCount);
//            packetStatsValue.setText(message);
        }

   // }


    private void inf(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(text);
        alert.setHeaderText(null);
        alert.setContentText("Не коректне значення");
        alert.showAndWait();
    }

    private void inf2() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(" ");
        alert.setHeaderText(null);
        alert.setContentText("Введіть значення");
        alert.showAndWait();
    }

    private void clear() {
        //freq1.clear();
        //serviceMessage.clear();
        //command.setValue(null);
    }



}
