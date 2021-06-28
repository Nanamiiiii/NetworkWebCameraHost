package nanami.networkwebcamerahost;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nanami.networkwebcamerahost.fxcontroller.MainController;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent root = loader.load();
        MainController mMainController = loader.getController();
        mMainController.setMainStage(primaryStage);
        primaryStage.setTitle("Network WebCamera Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
