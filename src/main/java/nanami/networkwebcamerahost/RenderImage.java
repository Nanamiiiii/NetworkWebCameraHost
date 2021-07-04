package nanami.networkwebcamerahost;

import javafx.scene.image.Image;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RenderImage {
    private Image mImage = null;
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock readLock = rwl.readLock();
    private Lock writeLock = rwl.writeLock();

    public RenderImage() {
        // NOP
    }

    public synchronized void setImage(Image image){
        writeLock.lock();
        try {
            mImage = image;
        }finally{
            writeLock.unlock();
        }
    }

    public synchronized Image getImage(){
        readLock.lock();
        try {
            return mImage;
        }finally{
            readLock.unlock();
        }
    }
}
