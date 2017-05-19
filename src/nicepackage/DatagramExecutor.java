package nicepackage;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * I wanted give command processing it's own class so i made this.
 * This way it feels easier to add new commands/methods and read code.
 * Created by IvanOP on 04.05.2017.
 */
public class DatagramExecutor {
    private Map<String, Call> stringMethodMap;
    private String[] possibleCommands;
    private Board board;
    private String message;

    interface Call {
        void execute();
    }

    /**
     * Store every method that executes given command
     */
    private Call[] calls = new Call[]{
            this::sendSerializedObject,
            this::receiveSerializedObject,
            this::clearVectorOnServer,
            this::clearVectorOnClient,
            this::sizeOnServer,
            this::sizeOnClient,
            this::requestObject,
            this::sendObject,
            this::getObject
    };

    DatagramExecutor(Board board) {
        this.board = board;
        try {
            setStringMethodMap();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets command for every function that works with server/client
     * -ct - connect to server
     * -dc - disconnect from server
     * -sobjc - send Board.graphObjectVector to server in JSON format
     * -sobjs - cannot be executed directly from client, identifies that server sent object
     * -clrc - clears storage of objects on server
     * -clrs - cannot be executed directly from client,
     * identifies that server requested to clear storage of objects on client
     * -vecsc - requests size of objects storage on server
     * -vecss - cannot be executed directly from client,
     * identifies that server requested size of objects storage on client
     * -gobjc - request of object with given number ex: -gobjc3
     * -gobjs - cannot be executed directly from client,
     * identifies that server requested object with given number ex:-gobjs3
     * -robj - cannot be executed directly from client, identifies that server sent requested object by command -gobjc
     *
     * @throws NoSuchMethodException
     */
    private void setStringMethodMap() throws NoSuchMethodException {
        possibleCommands = new String[]{"-sobjc", "-sobjs", "-clrc", "-clrs", "-vecsc", "-vecss", "-gobjc"
                , "-gobjs", "-robj"};
        stringMethodMap = new HashMap<>();
        for (int i = 0; i < possibleCommands.length; i++) {
            stringMethodMap.put(possibleCommands[i], calls[i]);
        }
    }


    void executeMessageFromClient(String message) {
        this.message = message;
        message = message.replaceAll("\\d", "");
        if (stringMethodMap.containsKey(message)) {
            for (Map.Entry<String, Call> temp : stringMethodMap.entrySet()) {
                if (temp.getKey().equals(message)) {
                    temp.getValue().execute();
                }
            }
        }
    }

    private void sendSerializedObject() {
        Gson gson = new Gson();
        String string = gson.toJson(board.graphObjectVector);
        DatagramConnection.sendPacketOfData(message);
        DatagramConnection.sendPacketOfData(string);
    }

    //for some reason that i haven't figured out yet every field of object that get created from json
    // is null in constructor. So i can't load image directly from there so setImage function must exist for now
    private void receiveSerializedObject() {
        Gson gson = new Gson();
        String string = DatagramConnection.receivePacketOfData();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(string).getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            System.out.println(jsonArray);
            Images image = gson.fromJson(jsonArray.get(i), Images.class);
            image.setImage();
            board.graphObjectVector.add(image);
            i++;
            Strings strings = gson.fromJson(jsonArray.get(i), Strings.class);
            board.graphObjectVector.add(strings);
        }
    }

    private void clearVectorOnServer() {
        DatagramConnection.sendPacketOfData(message);
    }

    private void clearVectorOnClient() {
        board.graphObjectVector.removeAllElements();
        System.out.println("Vector cleared");
    }

    private void sizeOnServer() {
        DatagramConnection.sendPacketOfData(message);
        String size = DatagramConnection.receivePacketOfData();
        System.out.println("size on server: " + size);
    }

    private void sizeOnClient() {
        DatagramConnection.sendPacketOfData(String.valueOf(board.graphObjectVector.size()));
    }

    private void requestObject() {
        DatagramConnection.sendPacketOfData(message);
    }

    private void sendObject() {
        message = message.replaceAll("[^0-9]", "");
        Gson gson = new Gson();
        String object = gson.toJson(board.graphObjectVector.get(Integer.valueOf(message)));
        DatagramConnection.sendPacketOfData("-robj");
        DatagramConnection.sendPacketOfData(object);
    }

    //for some reason that i haven't figured out yet every field of object that get created from json
    // is null in constructor. So i can't load image directly from there so setImage function must exist for now
    private void getObject() {
        Gson gson = new Gson();
        String type = DatagramConnection.receivePacketOfData();
        System.out.println(type);
        String object = DatagramConnection.receivePacketOfData();
        System.out.println(object);
        if (type.equals("Images")) {
            Images images = gson.fromJson(object, Images.class);
            images.setImage();
            board.graphObjectVector.add(images);
        } else if (type.equals("Strings")) {
            Strings strings = gson.fromJson(object, Strings.class);
            board.graphObjectVector.add(strings);
        }
    }

}
