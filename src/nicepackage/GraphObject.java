package nicepackage;

/**
 * Created by IvanOP on 25.03.2017.
 */


import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * @author IvanOP
 */
abstract class GraphObject implements Serializable {
    int x;
    int y;
    int objWidth;
    int objHeight;
    int initialX;
    int initialY;
    Rectangle spaceContainer;
    transient static Thread animation;
    boolean isRunning;
    boolean threadSuspended;

    GraphObject() {
        System.out.println("add");
    }

    GraphObject(int x, int y, int initialX, int initialY) {
        this.x = x;
        this.y = y;
        this.initialX = initialX;
        this.initialY = initialY;


    }

    public synchronized void stopThread() {}

    public synchronized void notifyThread() {}

    public void startAnimation() {}

    public abstract void paintComponent(Graphics g);

    public void saveAsTextFile(Path path) throws IOException {}

    public void saveAsBinaryFile(Path path) throws IOException {}

    public void readAsTextFile(Path path) throws IOException {}

    public void readAsBinaryFile(Path path) throws IOException {}

}