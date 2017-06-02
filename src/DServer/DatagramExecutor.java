package DServer;

import com.google.gson.*;
import nicepackage.Board;
import nicepackage.Images;
import nicepackage.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * I wanted give command processing it's own class so i made this.
 * This way it feels easier to add new commands/methods and read code.
 * Created by IvanOP on 04.05.2017.
 */
public class DatagramExecutor {
    private Map<String, NoParameterMethod> stringNoParameterMethodMap;
    private Map<String, ParameterMethod> stringParameterMethodMap;
    private Board board;
    private String message;

    @FunctionalInterface
    interface NoParameterMethod {
        void execute();
    }

    @FunctionalInterface
    interface ParameterMethod {
        void execute(String something);
    }

    /**
     * Store every method that executes given command
     */
    private NoParameterMethod[] noParameterMethods = new NoParameterMethod[]{
            this::sendSerializedObject,
            this::clearVectorOnServer,
            this::clearVectorOnClient,
            this::sizeOnClient,
            this::requestObject,
            this::sendObject
    };

    private ParameterMethod[] parameterMethods = new ParameterMethod[]{
            this::receiveSerializedObject,
            this::sizeOnServer,
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
     * -sobjc - send Board.graphObjectVector to server in JSON format
     * -sobjs - cannot be executed directly from client, identifies that server sent object
     * -clrvc - clears storage of objects on server
     * -clrvs - cannot be executed directly from client,
     * identifies that server requested to clear storage of objects on client
     * -vecsc - requests size of objects storage on server
     * -vecss - cannot be executed directly from client,
     * identifies that server requested size of objects storage on client
     * -gobjc - request of object with given number ex: -gobjc3
     * -gobjs - cannot be executed directly from client,
     * identifies that server requested object with given number ex:-gobjs3
     * -rcobj - cannot be executed directly from client, identifies that server sent requested object by command -gobjc
     *
     * @throws NoSuchMethodException
     */
    private void setStringMethodMap() throws NoSuchMethodException {
        String[] possibleNoParameterCommands = new String[]{"-sobjc", "-clrvc", "-vecsc", "-gobjc"
                , "-gobjs"};
        String[] possibleParameterCommands = new String[]{"-sobjs", "-vecss", "-rcobj"};
        stringNoParameterMethodMap = new HashMap<>();
        for (int i = 0; i < possibleNoParameterCommands.length; i++) {
            stringNoParameterMethodMap.put(possibleNoParameterCommands[i], noParameterMethods[i]);
        }

        stringParameterMethodMap = new HashMap<>();
        for (int i = 0; i < possibleParameterCommands.length; i++) {
            stringParameterMethodMap.put(possibleParameterCommands[i], parameterMethods[i]);
        }
    }


    void executeMessageFromClient(String[] message) {
        this.message = message[0];
        message[0] = message[0].replaceAll("\\d", "");
        for (Map.Entry<String, ParameterMethod> temp : stringParameterMethodMap.entrySet()) {
            if (temp.getKey().equals(message[0])) {
                temp.getValue().execute(message[1]);
            }
        }
    }

    void executeMessageFromClient(String message) {
        this.message = message;
        message = message.replaceAll("\\d", "");
        for (Map.Entry<String, NoParameterMethod> temp : stringNoParameterMethodMap.entrySet()) {
            if (temp.getKey().equals(message)) {
                temp.getValue().execute();
            }
        }
    }

    private void sendSerializedObject() {
        Gson gson = new Gson();
        String string = gson.toJson(board.graphObjectVector);
        DatagramConnection.sendPacketOfData(message);
        DatagramConnection.sendPacketOfData(string);
        DatagramConnection.sendPacketOfData("end");
    }

    //for some reason that i haven't figured out yet every field of object that get created from json
    // is null in constructor. So i can't load image directly from there so setImage function must exist for now
    private void receiveSerializedObject(String data) {
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(data).getAsJsonArray();
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

    private void sizeOnServer(String data) {
        DatagramConnection.sendPacketOfData(message);
        System.out.println("size on server: " + data);
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
    private void getObject(String data) {
        Gson gson = new Gson();
        JsonParser parser =  new JsonParser();
        JsonElement jsonElement = parser.parse(data);
        String type = jsonElement.getAsJsonObject().get("Type").getAsString();
        String objectItSelf = jsonElement.getAsJsonObject().get("Object").getAsString();
        System.out.println(type);
        System.out.println(data);
        if (type.equals("Images")) {
            Images images = gson.fromJson(objectItSelf, Images.class);
            images.setImage();
            board.graphObjectVector.add(images);
        } else if (type.equals("Strings")) {
            Strings strings = gson.fromJson(objectItSelf, Strings.class);
            board.graphObjectVector.add(strings);
        }
    }

}
