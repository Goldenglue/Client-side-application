package nicepackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gets called only once at the beginning to load every image
 * Created by IvanOP on 27.03.2017.
 */
public class FileData {
    Path dir;
    Stream<Path> heh = null;
    List<String> pathesToImages;
    List<String> namesOfImages = new ArrayList<String>();
    File[] files;

    //TODO filter automatically, but should learn about Stream more
    public FileData() throws IOException {
        this.dir = Paths.get("twitch chat");
        this.heh = Files.list(dir);
        this.pathesToImages = heh.map(Path::toString).collect(Collectors.toList());


        this.files = new File("twitch chat").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                namesOfImages.add(file.getName().split("\\.")[0]);
            }
        }
        Iterator<String> iterator = pathesToImages.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().contains("Thumbs")) {
                iterator.remove();
            }
        }
        iterator = namesOfImages.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().contains("Thumbs")) {
                iterator.remove();
            }
        }
    }
}

