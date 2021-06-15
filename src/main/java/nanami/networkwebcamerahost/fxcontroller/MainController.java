package nanami.networkwebcamerahost.fxcontroller;

import nanami.networkwebcamerahost.ImageServer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    private static String TAG = "[MainController]";

    private boolean NICSelected = false;

    @FXML
    private Text hostname;

    @FXML
    private Text ip_addr;

    @FXML
    private TextField port_no;

    @FXML
    private ChoiceBox<String> nic_select;

    private List<InetAddress> ipv4List;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            nic_select.setItems(FXCollections.observableArrayList(getNICList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        nic_select.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                NICSelected = true;
                hostname.setText(ipv4List.get(t1.intValue()).getHostName());
                ip_addr.setText(ipv4List.get(t1.intValue()).getHostAddress());
                System.out.println(TAG + "\tSelectedNIC\t" + nic_select.getItems().get(t1.intValue()));
            }
        });
        nic_select.setTooltip(new Tooltip("Select NIC."));
        port_no.setTooltip(new Tooltip("Enter the PORT No. for ImageReceiving."));
    }

    @FXML
    void onServerStartAction(ActionEvent ev) {
        if (!NICSelected) {
            System.out.println(TAG + "\t[Error]\tNIC is not selected.");
            Alert notSelectedAlert = new Alert(Alert.AlertType.ERROR);
            notSelectedAlert.setContentText("NIC is not selected.");
            notSelectedAlert.showAndWait();
            return;
        }

        String hostIP = ip_addr.getText();
        String hostPort = port_no.getText();

        try {
            generateImageViewWindow(hostIP, hostPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateImageViewWindow(String hostIP, String hostPort) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ImageScreen.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        ImageScreen mImageScreen = loader.getController();
        ImageServer mImageServer = new ImageServer(hostIP, hostPort);
        mImageServer.setImageScreen(mImageScreen);
        mImageScreen.setImageServer(mImageServer);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("ImageView " + "[" + hostIP + ":" + hostPort + "]");
        stage.showAndWait();
    }

    private List<String> getNICList () throws IOException {
        Enumeration<NetworkInterface> enuIfs = NetworkInterface.getNetworkInterfaces();
        List<String> ipList = new ArrayList<>();
        ipv4List = new ArrayList<>();
        if (null != enuIfs) {
            while (enuIfs.hasMoreElements()) {
                System.out.println(TAG + "\tINTERFACE FOUND");
                NetworkInterface ni = (NetworkInterface)enuIfs.nextElement();
                System.out.println(TAG + "\tgetDisplayName:\t" + ni.getDisplayName());
                System.out.println(TAG + "\tgetName:\t" + ni.getName());
                if (ni.isLoopback()) continue;
                List<InterfaceAddress> interfaceList = ni.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress: interfaceList) {
                    InetAddress ia = interfaceAddress.getAddress();
                    if (ia instanceof Inet4Address) {
                        System.out.println(TAG + "\tIPv4 Address:\t" + ia.getHostAddress());
                        ipv4List.add(ia);
                        String content = ni.getDisplayName() + ":" + ia.getHostAddress();
                        ipList.add(content);
                    }
                }
            }
        }
        return ipList;
    }
}
