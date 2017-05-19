package nicepackage;

/**
 * Created by IvanOP on 25.03.2017.
 */

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static java.lang.Thread.sleep;


public class TwitchEmotes extends JPanel {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        TwitchEmotes twitchEmotes = new TwitchEmotes();
        FrameHolder frameHolder = new FrameHolder();
        //ClientSideConnection connection = new ClientSideConnection(frameHolder.board);
        DatagramConnection datagramConnection =  new DatagramConnection(frameHolder.board);
    }
}