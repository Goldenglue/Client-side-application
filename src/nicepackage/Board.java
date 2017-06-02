package nicepackage;

/**
 * Created by IvanOP on 25.03.2017.
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * @author IvanOP
 */
public class Board extends JPanel implements MouseListener, Serializable {

    public Vector<GraphObject> graphObjectVector;
    private int fileDataSize = 0;
    private FileData fileData;
    private boolean areThreadsRunning;
    private transient Thread boardThread;

    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }

    private Runnable running = (Runnable & Serializable) () -> {
        while (true) {
            repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    public Board() {

        this.graphObjectVector = new Vector<>();

        areThreadsRunning = true;
        try {
            fileData = new FileData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileDataSize =  FileData.getPathsToImages().size() - 1;
        addMouseListener(this);
        boardThread = new Thread(running);
        boardThread.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        graphObjectVector.forEach(kek -> kek.paintComponent(g));
    }

    //for some weird reason accuracy is quite bad, can't explain
    @Override
    public synchronized void mousePressed(MouseEvent me) {
        switch (me.getModifiers()) {
            //создание объекта по нажатию ЛКМ
            case InputEvent.BUTTON1_MASK: {
                //in case of click over something that is already on pane - remove it and paint over it
                Iterator<GraphObject> iterator = graphObjectVector.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().spaceContainer.contains(me.getX(), me.getY())) {
                        iterator.remove();
                        if (iterator.hasNext()) {
                            if (iterator.next() instanceof Images) {
                                continue;
                            } else {
                                iterator.remove();
                            }
                        }
                        repaint();
                    }
                }
                int min = 0;
                int randomNumber = min + (int) (Math.random() * (fileDataSize));
                graphObjectVector.add(new Images(me.getX(), me.getY(), FileData.getPathsToImages().get(randomNumber)));
                graphObjectVector.add(new Strings(me.getX(), me.getY(), FileData.getNamesOfImages().get(randomNumber)));
                repaint();
                break;
            }

            ///удаление объекта по нажатию ПКМ
            case InputEvent.BUTTON3_MASK: {
                Iterator<GraphObject> iterator = graphObjectVector.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().spaceContainer.contains(me.getX(), me.getY())) {
                        iterator.remove();
                        repaint();
                    }
                }
                repaint();
                break;
            }

            //остановка движения объектов по нажатию колесика
            case InputEvent.BUTTON2_MASK: {
                for (GraphObject graphObject : graphObjectVector) {
                    if (graphObject.spaceContainer.contains(me.getX(), me.getY())) {
                        if (graphObject.isRunning) {
                            graphObject.stopThread();
                            graphObject.isRunning = !(graphObject.isRunning);
                        } else {
                            graphObject.notifyThread();
                            graphObject.isRunning = !(graphObject.isRunning);
                        }
                    }
                }
                break;
            }
        }
    }


    public void saveObjectsAsText() throws IOException {
        Path path = Paths.get("text.txt");
        for (GraphObject temp : graphObjectVector) {
            try {
                temp.saveAsTextFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File writeNumberOfObjects = new File(path.toString());
        try (BufferedWriter toWrite = new BufferedWriter(new FileWriter(writeNumberOfObjects, true))) {
            toWrite.write("number of objects: " + graphObjectVector.size() + "\n");
        }
    }

    //TODO remake this method fully
    public void loadObjectsAsText() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Path pathSource = Paths.get("text.txt");
        Path pathTarget = Paths.get("tempText.txt");

        if (!(Files.exists(pathTarget))) {
            Files.copy(pathSource, pathTarget);
        }
        int num = 0;
        File toRead = new File(pathTarget.toString());
        try (Scanner scanner = new Scanner(toRead)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("number of objects: ")) {
                    line = line.replaceAll("[^0-9]", "");
                    num = (Integer.valueOf(line));
                }
            }
        }

        List<String> lines = Files.readAllLines(pathTarget);
        while (num != 0) {
            int count = 0;
            int thing = 0;
            Class<?> c = Class.forName(lines.get(0));
            if (Objects.equals(lines.get(0), "nicepackage.Strings")) {
                thing = 7;
            } else {
                thing = 6;
            }
            Constructor<?> cons = c.getConstructor(Path.class);
            Object object = cons.newInstance(pathTarget);
            graphObjectVector.add((GraphObject) object);
            while (count != thing) {
                lines.remove(0);
                count++;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(toRead))) {
                for (String kek : lines) {
                    try {
                        writer.write(kek + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            num--;
        }
        toRead.delete();
        repaint();
    }

    public void saveObjectsAsBinary() throws IOException {
        Path path = Paths.get("bin.bin");
        for (GraphObject temp : graphObjectVector) {
            try {
                temp.saveAsBinaryFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File writeNumberOfObjects = new File(path.toString());
        try (BufferedWriter toWrite = new BufferedWriter(new FileWriter(writeNumberOfObjects, true))) {
            toWrite.write("number of objects: " + graphObjectVector.size() + "\n");
        }
    }

    //TODO remake this method fully
    public void loadObjectsAsBinary() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Path pathSource = Paths.get("bin.bin");
        Path pathTarget = Paths.get("tempBin.bin");

        if (!(Files.exists(pathTarget))) {
            Files.copy(pathSource, pathTarget);
        }
        int num = 0;
        File toRead = new File(pathTarget.toString());
        try (Scanner scanner = new Scanner(toRead)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("number of objects: ")) {
                    line = line.replaceAll("[^0-9]", "");
                    num = (Integer.valueOf(line));
                }
            }
        }

        List<String> lines = Files.readAllLines(pathTarget);
        while (num != 0) {
            int count = 0;
            int thing = 0;
            Class<?> c = Class.forName(lines.get(0));
            if (Objects.equals(lines.get(0), "nicepackage.Strings")) {
                thing = 7;
            } else {
                thing = 6;
            }
            Constructor<?> cons = c.getConstructor(Path.class);
            Object object = cons.newInstance(pathTarget);
            graphObjectVector.add((GraphObject) object);
            while (count != thing) {
                lines.remove(0);
                count++;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(toRead))) {
                for (String kek : lines) {
                    try {
                        writer.write(kek + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            num--;
        }
        toRead.delete();
        repaint();
    }

    public void javaSerialization() throws IOException {
        Path path = Paths.get("javaSerialization.out");
        File file = new File(path.toString());

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(graphObjectVector);
            outputStream.close();
        }
    }

    public void javaDeserialization() throws IOException, ClassNotFoundException {
        Path path = Paths.get("javaSerialization.out");
        File file = new File(path.toString());

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            graphObjectVector = (Vector<GraphObject>) inputStream.readObject();
            for (GraphObject graphObject : graphObjectVector) {
                graphObject.setImage();
                graphObject.startAnimation();
            }
        }
    }

    void pauseOrContinueMovement() {
        if (areThreadsRunning) {
            graphObjectVector.forEach(GraphObject::stopThread);
            areThreadsRunning = !areThreadsRunning;
        } else {
            graphObjectVector.forEach(GraphObject::notifyThread);
            areThreadsRunning = !areThreadsRunning;
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

}
