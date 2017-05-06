package nicepackage;

/**
 * Created by IvanOP on 25.03.2017.
 */

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static java.lang.Thread.sleep;


public class TwitchEmotes extends JPanel implements Runnable {
    /**
     * @param args the command line arguments
     */
    private String toServer;
    private Thread mainThread;
    private FrameHolder frameHolder;
    private ClientSideConnection connection;
    private Runnable runnable = () -> {
        while (true) {
            this.toServer = frameHolder.guiHolder.getToServer();
            connection.setToServer(this.toServer);
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        TwitchEmotes twitchEmotes = new TwitchEmotes();
        twitchEmotes.run();

    }

    @Override
    public void run() {

        frameHolder = new FrameHolder();
        connection = new ClientSideConnection(frameHolder.board);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainThread = new Thread(runnable);
        mainThread.start();
    }
}