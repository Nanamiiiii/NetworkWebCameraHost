package nanami.networkwebcamerahost.fxcontroller;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nanami.networkwebcamerahost.ImageServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ImageScreenController implements Initializable {

    /* Fields */

    @FXML
    private ImageView imageView;

    @FXML
    private Button closeBtn;

    @FXML
    private ProgressIndicator connectionProgress;

    @FXML
    private Text progressText;

    private ImageServer mImageServer;
    private String TAG = "[ImageScreen Controller]";
    private Stage mStage;
    private Lock imageLock = new ReentrantLock();

    /* Methods */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    @FXML
    public void onClickClose(ActionEvent event) {
        mImageServer.stopServer();
        mImageServer = null;
        this.mStage.close();
    }

    @FXML
    public void setImage(Image image) {
        imageView.setImage(image);
    }

    @FXML
    public void setIndicatorVisibility(boolean val){
        connectionProgress.setVisible(val);
    }

    @FXML
    public void setProgressText(String str){
        progressText.setText(str);
    }

    public void setImageServer (ImageServer mImageServer) {
        this.mImageServer = mImageServer;
    }

    public void setStage(Stage mStage) { this.mStage = mStage; }

    public void closeFromOutside(){
        Platform.runLater(() -> {
            mStage.close();
        });
    }

    public ImageServer getImageServer() {
        return mImageServer;
    }

}
