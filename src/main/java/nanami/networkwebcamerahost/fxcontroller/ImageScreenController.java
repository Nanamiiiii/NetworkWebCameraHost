package nanami.networkwebcamerahost.fxcontroller;

import javafx.stage.Stage;
import nanami.networkwebcamerahost.ImageServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageScreenController {

    /* Fields */

    @FXML
    private ImageView imageView;

    @FXML
    private Button closeBtn;

    private ImageServer mImageServer;
    private String TAG = "[ImageScreen Controller]";
    private Stage mStage;

    /* Methods */

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

    public void setImageServer (ImageServer mImageServer) {
        this.mImageServer = mImageServer;
    }

    public void setStage(Stage mStage) { this.mStage = mStage; }

    public ImageServer getImageServer() {
        return mImageServer;
    }
}
