package nicepackage;

/**
 * Created by IvanOP on 25.03.2017.
 */

import java.awt.*;
import java.io.*;
import java.nio.file.Path;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

/**
 * @author IvanOP
 */
public class Strings extends GraphObject implements Serializable {
    String hah;
    int radius;
    double a = 0;


    public void paintComponent(Graphics g) {
        FontMetrics metrics = g.getFontMetrics();
        this.objWidth = metrics.stringWidth(hah);
        this.objHeight = metrics.getHeight();
        this.spaceContainer.height = objHeight;
        this.spaceContainer.width = objWidth;
        g.drawString(hah, x - objWidth / 2, y + 28);
    }

    private void circularMotion() {
        if (a > PI * 2) {
            this.a = 0;
        } else {
            int nextX = (int) (objWidth / 2 + cos(a) * radius);
            int nextY = (int) (objHeight / 2 + sin(a) * radius);
            this.x = initialX + nextX;
            this.y = initialY + nextY;
            this.spaceContainer.x = initialX + nextX;
            this.spaceContainer.y = initialY + nextY;
            this.a = a + 0.1;
        }
    }


    transient Runnable running = (Runnable & Serializable) () -> {
        while (true) {

            try {
                Thread.sleep(50);

                synchronized (this) {
                    while (threadSuspended) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            circularMotion();

        }
    };


    @Override
    public void startAnimation() {
        animation = new Thread(running);
        animation.start();
    }

    @Override
    public synchronized void notifyThread() {
        this.threadSuspended = false;
        this.notify();
    }

    @Override
    public synchronized void stopThread() {
        threadSuspended = true;
    }


    public Strings(int x, int y, String name) {

        super(x, y, x, y);
        this.hah = name;
        this.radius = 10 + (int) (Math.random() * 50);
        this.threadSuspended = false;
        this.isRunning = true;
        this.spaceContainer = new Rectangle(x - objWidth / 2, y + objHeight / 2, objWidth, objHeight);
        this.startAnimation();

    }

    public Strings() {
        this.startAnimation();
    }

    public Strings(Path path) throws IOException {
        //this.readAsTextFile(path);
        this.readAsBinaryFile(path);
        this.threadSuspended = false;
        this.isRunning = true;
        this.spaceContainer = new Rectangle(x - objWidth / 2, y + objHeight / 2, objWidth, objHeight);
        this.startAnimation();
    }


    @Override
    public void saveAsTextFile(Path path) throws IOException {
        File filewrite = new File(path.toString());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filewrite, true))) {
            writer.write(this.getClass().getName() + "\n");
            writer.write(this.hah + "\n");
            writer.write(this.x + "\n");
            writer.write(this.y + "\n");
            writer.write(this.radius + "\n");
            writer.write(this.initialX + "\n");
            writer.write(this.initialY + "\n");
        }
    }

    @Override
    public void readAsTextFile(Path path) throws IOException {
        File fileread = new File(path.toString());
        try (BufferedReader reader = new BufferedReader(new FileReader(fileread))) {
            reader.readLine();
            this.hah = reader.readLine();
            this.x = Integer.valueOf(reader.readLine());
            this.y = Integer.valueOf(reader.readLine());
            this.radius = Integer.valueOf(reader.readLine());
            this.initialX = Integer.valueOf(reader.readLine());
            this.initialY = Integer.valueOf(reader.readLine());
        }
    }

    @Override
    public void saveAsBinaryFile(Path path) throws IOException {
        File filewrite = new File(path.toString());
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filewrite, true)))) {
            writer.write(this.getClass().getName() + "\n");
            writer.write(this.hah + "\n");
            writer.write(this.x + "\n");
            writer.write(this.y + "\n");
            writer.write(this.radius + "\n");
            writer.write(this.initialX + "\n");
            writer.write(this.initialY + "\n");
        }
    }

    @Override
    public void readAsBinaryFile(Path path) throws IOException {
        File fileread = new File(path.toString());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileread)))) {
            reader.readLine();
            this.hah = reader.readLine();
            this.x = Integer.valueOf(reader.readLine());
            this.y = Integer.valueOf(reader.readLine());
            this.radius = Integer.valueOf(reader.readLine());
            this.initialX = Integer.valueOf(reader.readLine());
            this.initialY = Integer.valueOf(reader.readLine());
        }
    }
}
