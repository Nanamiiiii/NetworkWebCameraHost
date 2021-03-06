package nanami.networkwebcamerahost;

import javafx.application.Platform;
import nanami.networkwebcamerahost.fxcontroller.ImageScreenController;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import javafx.embed.swing.SwingFXUtils;
import java.util.ArrayDeque;
import java.util.Queue;

public class ImageServer {
    private String hostIP;
    private String hostPort;
    private boolean running = false;
    private boolean connectionEstablished = false;
    private ImageScreenController mImageScreenController;
    private ServerSocket mServerSocket = null;
    private Socket mSocket = null;
    private Thread receiveImageThread;
    private Thread setImageThread;
    private Thread renderImageThread;
    private RenderImage mRenderImage;
    private String TAG = "[ImageServer]";

    /* Public Method */

    public ImageServer(String hostIP, String hostPort) {
        this.hostIP = hostIP;
        this.hostPort = hostPort;
        try {
            mServerSocket = new ServerSocket(Integer.parseInt(hostPort));
        } catch (IOException e) {
            System.out.println(TAG + "\tCannot open the port.");
            e.printStackTrace();
        }
        mRenderImage = new RenderImage();
        startServer();
    }

    public void startServer() {
        System.out.println(TAG + "Starting Server");
        running = true;
        start();
    }

    public void stopServer() {
        running = false;
        stop();
    }

    public void setImageScreen (ImageScreenController mImageScreenController) {
        if (mImageScreenController != null) {
            this.mImageScreenController = mImageScreenController;
        }
    }

    public ImageScreenController getImageScreen() {
        return this.mImageScreenController;
    }

    public boolean serverAlive(){
        return receiveImageThread.isAlive();
    }

    /* Private Method */

    private void start() {
        renderImageThread = new Thread(() -> {
            while(running) {
                Image renderImage = mRenderImage.getImage();
                if (renderImage != null) {
                    mImageScreenController.setImage(renderImage);
                }
                sleep(1000/60);
            }
        });

        receiveImageThread = new Thread(() -> {
            if (!connectionEstablished) {
                if (!waitingConnection()) return;
            }
            BufferedInputStream bis = getBufferedIS(mSocket);
            while(running) {
                // First byte must be 0xff
                byte firstByte = readByte(bis);
                if(firstByte != (byte) 0xff) {
                    inputError();
                    break;
                }
                try {
                    // Next 4 bytes are size of image
                    byte[] dataSizeByte = readBytes(bis, 4);
                    int dataSize = ByteBuffer.wrap(dataSizeByte).getInt();
                    byte[] receivedImageByte = readBytes(bis, dataSize);
                    if(setImageThread != null && setImageThread.isAlive()){
                        setImageThread.interrupt();
                    }
                    setImageThread = new Thread(() -> {
                        Image receivedImage = byteToFXImage(receivedImageByte);
                        mRenderImage.setImage(receivedImage);
                    });
                    setImageThread.start();
                    sleep(1);
                } catch (StreamReadingException e){
                    System.err.println(TAG + "\tConnection will close.");
                    running = false;
                    mImageScreenController.closeFromOutside();
                }
            }

            closeBufferedIS(bis);
            disconnect();
            System.out.println(TAG + "\tDisconnected.");
        });

        receiveImageThread.start();
        renderImageThread.start();
    }

    private void stop() {
        try {
            if (!connectionEstablished) {
                disconnect();
            }
            if (receiveImageThread != null) {
                receiveImageThread.join();
                receiveImageThread = null;
            }
            if (renderImageThread != null){
                renderImageThread.join();
                renderImageThread = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Image byteToFXImage (byte[] bytes) {
        javafx.scene.image.Image fxImage = null;
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
            fxImage = SwingFXUtils.toFXImage(bi, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fxImage;
    }

    // For Debug
    private void saveImageByte (byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(bis);
            ImageIO.write(image, "JPG", new File("img.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inputError() {
        System.out.println(TAG + "\t[Error]\t" + "Invalid Data Input. Connection will be closed.");
        running = false;
    }

    private byte[] readBytes(BufferedInputStream bis, int size) throws StreamReadingException {
        byte[] input = new byte[size];
        int offset = 0;
        int readByte = 0;
        int now_read = 0;
        try {
            // keep reading until getting all bytes
            while(readByte < size) {
                if((now_read = bis.read(input, offset, size - readByte)) == -1) {
                    throw new StreamReadingException("Byte Reading Error.");
                }
                readByte += now_read;
                offset = readByte;
            }
            System.out.println(TAG + "\tread " + size + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }

    private byte readByte(BufferedInputStream bis) {
        byte input = 0;
        try {
            input = (byte) bis.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    private BufferedInputStream getBufferedIS(Socket socket) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bis;
    }

    private void closeBufferedIS (BufferedInputStream bis) {
        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean waitingConnection() {
        try {
            mSocket = mServerSocket.accept();
        } catch (IOException e) {
            System.out.println(TAG + "\tConnection refused.");
            e.printStackTrace();
            return false;
        }

        InetAddress clientAddr = mSocket.getInetAddress();
        String progstr = "Connect to " + clientAddr.getHostAddress();
        connectionEstablished = true;
        mImageScreenController.setIndicatorVisibility(false);
        mImageScreenController.setProgressText(progstr);
        System.out.println(TAG + "\tConnection Accepted.");
        return true;
    }
}
