package org.trident.util;

import java.io.*;
import java.util.Properties;

/**
 * @author https://github.com/vincenzopalazzo
 */
public class ConfiguratorSingleton {

    //Called pattern Singleton fin on the web
    private static ConfiguratorSingleton SINGLETON;
    //tag for logger
    private static final Class TAG = ConfiguratorSingleton.class;

    public static ConfiguratorSingleton getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new ConfiguratorSingleton();
        }
        return SINGLETON;
    }

    private Properties config;

    private ConfiguratorSingleton() {
        loadProperties();
    }

    private void loadProperties() {
        //Do nothing for moment
        String pathHome = this.getPathHome();
        pathHome += Constant.NAME_FILE.toString();
        TridentLogger.getInstance().debug(TAG, "Path is: " + pathHome);
        config = new Properties();

        try {
            File fileConf = new File(pathHome);
            if (fileConf.exists()) {
                FileInputStream fileInputStream = new FileInputStream(fileConf);
                config.load(fileInputStream);
            } else {
                TridentLogger.getInstance().debug(TAG, "Home directory not found");
                TridentLogger.getInstance().debug(TAG, "The app is to first load");
                TridentLogger.getInstance().debug(TAG, "Load the default conf");
                pathHome = Constant.NAME_FILE.toString();
                config.load(this.getClass().getClassLoader().getResourceAsStream(pathHome));
                TridentLogger.getInstance().debug(TAG, "Default conf load");
                this.createHomeDirectory();
                this.savePropertiesToHomeDir();
            }
        } catch (IOException e) {
            TridentLogger.getInstance().error(TAG, "IRREVERSIBLE ERROR");
            TridentLogger.getInstance().error(TAG, "Impossible load configurations");
            TridentLogger.getInstance().error(TAG, "Contact the support https://github.com/vincenzopalazzo/trident");
            e.printStackTrace();
        }

    }

    private void createHomeDirectory() {
        String path = this.getPathHome();
        this.createDirectory(path);
    }

    public String getPropriety(Constant key) {
        return config.getProperty(key.toString());
    }

    public void setPropriety(Constant key, String value) {
        if (value == null || value.isEmpty()) {
            TridentLogger.getInstance().error(TAG, "TODO build a good message");
            throw new IllegalArgumentException("TODO build a good message");
        }
        TridentLogger.getInstance().debug(TAG, "Added propriety with key: "
                + key.toString() + " and value: " + value);
        config.setProperty(key.toString(), value);
    }

    public String getPathHome() {
        String pathHome = System.getProperty("user.home");
        if (TridentUtil.isOS(TridentUtil.OS.LINUX)) {
            pathHome += Constant.DEFAULT_PATH_LINU.toString();
        } else {
            pathHome += Constant.DEFAULT_PATH_OTHERS.toString();
        }
        return pathHome.trim();
    }

    public void createDirectory(String path) {
        if (path == null || path.isEmpty()) {
            String message = "ERROR: Path is null or empty";
            TridentLogger.getInstance().error(TAG, message);
            throw new IllegalArgumentException(message);
        }
        File newDir = new File(path);
        if (!newDir.exists()) {
            TridentLogger.getInstance().debug(TAG, "Path not exist: " + path);
            if (!newDir.mkdir()) {
                TridentLogger.getInstance().error(TAG, "Can not cerate the dir at the path: " + path);
                //TODO should be create an examption? maybe but after
            }
            return;
        }
        TridentLogger.getInstance().debug(TAG, "Path exist yet: " + path);
    }

    public void savePropertiesAt(String path){
        if (path == null || path.isEmpty()) {
            String message = "ERROR: Path is null or empty";
            TridentLogger.getInstance().error(TAG, message);
            throw new IllegalArgumentException(message);
        }
        path += Constant.NAME_FILE.toString();
        try {
            OutputStream outputStream = new FileOutputStream(path);
            config.store(outputStream, null);
            TridentLogger.getInstance().debug(TAG, "Save config to : " + path);
        } catch (FileNotFoundException fileNotFoundException) {
            TridentLogger.getInstance().error(TAG, "Exception generated: " + fileNotFoundException.getLocalizedMessage());
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            TridentLogger.getInstance().error(TAG, "Exception generated: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void savePropertiesToHomeDir(){
        String pathHome = this.getPathHome() + "/" + Constant.NAME_FILE.toString();
        try {
            OutputStream outputStream = new FileOutputStream(pathHome);
            config.store(outputStream, null);
        } catch (FileNotFoundException fileNotFoundException) {
            TridentLogger.getInstance().error(TAG, "Exception generated: " + fileNotFoundException.getLocalizedMessage());
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            TridentLogger.getInstance().error(TAG, "Exception generated: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
