package src;

import matachi.mapeditor.editor.Controller;
import org.jdom.input.SAXBuilder;
import src.utility.GameCallback;
import src.utility.NewGameCallback;
import src.utility.PropertiesLoader;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = System.getProperty("user.dir") + "/properties/test4.properties";
    public static final String DEFAULT_MAP_PATH = System.getProperty("user.dir") + "/game";

    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
//        System.out.println(Paths.get("").toAbsolutePath().toString());
//        System.out.println(System.getProperty("user.dir"));
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        String mapPath = DEFAULT_MAP_PATH;
        if (args.length > 0 && mapPath.contains("game")) {
            File selectedFile = new File(args[0]);
                if (!selectedFile.exists()) {
                    System.out.println("[Game folder" + args[0] + " no maps found");

                }


            final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
            NewGameCallback gameCallback = new NewGameCallback();
            new NewGame(gameCallback, properties,1);
        } else {
            new Controller();
        }

    }

    public static void run(){
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        GameCallback gameCallback = new GameCallback();
        new Game(gameCallback, properties);
    }
}
