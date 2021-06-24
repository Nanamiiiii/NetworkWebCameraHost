package nanami.networkwebcamerahost.fxcontroller;

import nanami.networkwebcamerahost.ImageServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageScreen {
    @FXML
    private ImageView imageView;

    @FXML
    private Button closeBtn;

    private ImageServer mImageServer;
    private String TAG = "[ImageScreen Controller]";

    @FXML
    public void onClickClose(ActionEvent event) {
        mImageServer.stopServer();
        mImageServer = null;
        closeBtn.getScene().getWindow().hide();
    }

    @FXML
    public void setImage(Image image) {
       imageView.setImage(image);
    }

    public void setImageServer (ImageServer mImageServer) {
        this.mImageServer = mImageServer;
    }

    public ImageServer getImageServer() {
        return mImageServer;
    }
}
