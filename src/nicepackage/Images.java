package nicepackage;

/**
 * Created by IvanOP on 25.03.2017.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * @author IvanOP
 */
public class Images extends GraphObject implements Serializable {
    private transient BufferedImage img;
    String source;


    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, x - (objWidth / 2), y - (objHeight / 2), objWidth, objHeight, null);
    }

    private void getAwayFromTheCenter() {
        int randX = -15 + (int) (Math.random() * 30);
        int randY = -15 + (int) (Math.random() * 30);
        this.x = x + randX;
        this.y = y + randY;
        this.spaceContainer.x = x + randX;
        this.spaceContainer.y = y + randY;
    }

    private void backToTheCenter() {
        this.x = initialX;
        this.y = initialY;
        this.spaceContainer.x = initialX;
        this.spaceContainer.y = initialY;
    }

    Runnable running = (Runnable & Serializable) () -> {

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
            getAwayFromTheCenter();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            backToTheCenter();
        }
    };

    @Override
    public void startAnimation() {
        //System.out.println("a");
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

    public Images(int x, int y, String imgPath) {
        super(x, y, x, y);

        this.source = imgPath;
        try {
            this.img = ImageIO.read(new File(source));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.threadSuspended = false;
        this.objWidth = img.getWidth();
        this.objHeight = img.getHeight();
        this.spaceContainer = new Rectangle(x - (objWidth / 2), y - (objHeight / 2), objWidth, objHeight);
        this.isRunning = true;
        startAnimation();
    }

    public Images() {
        this.startAnimation();
    }

    public Images(Path path) throws IOException {
        this.readAsTextFile(path);
        try {
            this.img = ImageIO.read(new File(source));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.threadSuspended = false;
        this.objWidth = img.getWidth();
        this.objHeight = img.getHeight();
        this.spaceContainer = new Rectangle(x - (objWidth / 2), y - (objHeight / 2), objWidth, objHeight);
        this.isRunning = true;
        this.startAnimation();
    }

    @Override
    public void setImage() {
        try {
            this.img = ImageIO.read(new File(source));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveAsTextFile(Path path) throws IOException {
        File filewrite = new File(path.toString());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filewrite, true))) {
            writer.write(this.getClass().getName() + "\n");
            writer.write(this.source + "\n");
            writer.write(this.x + "\n");
            writer.write(this.y + "\n");
            writer.write(this.initialX + "\n");
            writer.write(this.initialY + "\n");
        }
    }

    public void saveAsBinaryFile(Path path) throws IOException {
        File filewrite = new File(path.toString());
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filewrite, true)))) {
            writer.write(this.getClass().getName() + "\n");
            writer.write(this.source + "\n");
            writer.write(this.x + "\n");
            writer.write(this.y + "\n");
            writer.write(this.initialX + "\n");
            writer.write(this.initialY + "\n");
        }
    }

    public void readAsTextFile(Path path) throws IOException {
        File fileread = new File(path.toString());
        try (BufferedReader reader = new BufferedReader(new FileReader(fileread))) {
            reader.readLine();
            this.source = reader.readLine();
            this.x = Integer.valueOf(reader.readLine());
            this.y = Integer.valueOf(reader.readLine());
            this.initialX = Integer.valueOf(reader.readLine());
            this.initialY = Integer.valueOf(reader.readLine());
        }
    }

    public void readAsBinaryFile(Path path) throws IOException {
        File fileread = new File(path.toString());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileread)))) {
            reader.readLine();
            this.source = reader.readLine();
            this.x = Integer.valueOf(reader.readLine());
            this.y = Integer.valueOf(reader.readLine());
            this.initialX = Integer.valueOf(reader.readLine());
            this.initialY = Integer.valueOf(reader.readLine());
        }
    }
}