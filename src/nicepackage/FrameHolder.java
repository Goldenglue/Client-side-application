package nicepackage;

import javax.swing.*;
import java.awt.*;


/**
 * Created by IvanOP on 03.05.2017.
 */
public class FrameHolder extends JFrame {
    private JFrame frame;
    GUIHolder guiHolder;
    Board board;


    FrameHolder() {
        this.frame = new JFrame("emotes");
        this.board = new Board();
        this.guiHolder = new GUIHolder(board);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(board, BorderLayout.WEST);
        frame.add(guiHolder, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
        frame.setFocusable(true);
    }
}
