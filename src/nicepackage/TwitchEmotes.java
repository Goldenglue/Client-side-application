package nicepackage;

import DServer.DatagramConnection;
import SServer.ClientSideConnection;

/**
 * Created by IvanOP on 25.03.2017.
 */

import javax.swing.*;

public class TwitchEmotes extends JPanel {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TwitchEmotes twitchEmotes = new TwitchEmotes();
        FrameHolder frameHolder = new FrameHolder();
        ClientSideConnection connection = new ClientSideConnection(frameHolder.board);
//        DatagramConnection datagramConnection = new DatagramConnection(frameHolder.board);
    }
}