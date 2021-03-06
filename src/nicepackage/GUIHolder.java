package nicepackage;

import DServer.DatagramConnection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IvanOP on 03.05.2017.
 */
public class GUIHolder extends JPanel {
    private JButton jButton;
    private JTextField jTextField;
    private Board board;

    public Dimension getPreferredSize() {
        return new Dimension(200, 500);
    }

    GUIHolder(Board board) {
        setBackground(Color.BLACK);
        this.board = board;
        createAndShowGUI();
    }


    private void createAndShowGUI() {
        jButton = new JButton("Save as text");
        jButton.addActionListener(actionEvent -> {
            try {
                board.saveObjectsAsText();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        add(jButton);
        jButton = new JButton("Save as bin");
        jButton.addActionListener(actionEvent -> {
            try {
                board.saveObjectsAsBinary();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        add(jButton);
        jButton = new JButton("Read as text");
        jButton.addActionListener(actionEvent -> {
            try {
                board.loadObjectsAsText();
            } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        });
        add(jButton);
        jButton = new JButton("read as bin");
        jButton.addActionListener(actionEvent -> {
            try {
                board.loadObjectsAsBinary();
            } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        });
        add(jButton);
        jButton =  new JButton("serialize");
        jButton.addActionListener(actionEvent -> {
            try {
                board.javaSerialization();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        add(jButton);
        jButton = new JButton("deserialize");
        jButton.addActionListener(actionEvent -> {
            try {
                board.javaDeserialization();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        add(jButton);
        jButton = new JButton("Pause or continue movement");
        jButton.addActionListener(actionEvent -> {
            board.pauseOrContinueMovement();
        });
        add(jButton);
        jTextField = new JTextField(10);
        jTextField.addActionListener(actionEvent -> {
            DatagramConnection.processCommand(jTextField.getText());
            jTextField.setText("");
        });
        add(jTextField);
    }
}
