package database;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import model.UserProfile;
import utils.ConfigReader;

import java.io.IOException;

public class ArangoHandler {
    private ArangoDB driver;
    private ArangoDatabase database;
    private ArangoCollection collection;

    /**
     * Connects to ArangoDB
     */
    public void connect(){
        /* Read config file */
        try{
            ConfigReader config = new ConfigReader("config");
            String databaseName = config.getConfig("db.name");
            String collectionName = config.getConfig("collection.users.name");

            /* Select driver */
            driver = DatabaseConnection.getDBConnection().getArangoDriver();

                try{
                    /* If database does not exist */
                     if (!driver.getDatabases().contains(databaseName)) {
                         /* Create database */

                         driver.createDatabase(databaseName);
                         System.out.println("1");

                         driver.db(databaseName).createCollection(collectionName);
                         System.out.println("Database created");
                     }

                }catch (ArangoDBException e) {
                    System.out.println("Failed to create database");
                }
                /* Create collection */


            /* Select database */
            database = driver.db(databaseName);
            /* Select collection */
            collection = database.collection(collectionName);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Disconnects from ArangoDB
     */
    public void disconnect() {
        /* Tear down connection */
        driver.shutdown();
    }

    /**
     * Retrieves user profile by key
     */
    public UserProfile getUserProfile(String key) {
        /* Retrieve user profile */
        return collection.getDocument(key, UserProfile.class);
    }

    /**
     * Creates user profile
     */
    public String createUserProfile(UserProfile userProfile) throws ArangoDBException {
        /* Check validity before insertion */
        if (!isValidUserProfile(userProfile)) {
            throw new RuntimeException("Invalid user profile");
        }

        /* Insert user profile */
        DocumentCreateEntity insertedUserProfile = collection.insertDocument(userProfile);
        return insertedUserProfile.getKey();
    }

    /**
     * Validates user profile
     */
    public boolean isValidUserProfile(UserProfile userProfile) {
        // TODO: Check if valid email
        // TODO: Check if email does not already exist
        return true;
    }

    public static void main(String[] args) {
        ArangoHandler handler = new ArangoHandler();
        handler.connect();

        String key = handler.createUserProfile(
                new UserProfile(
                        "nabila.ahmed@gmail.com",
                        "Nabila",
                        "Ahmed"
                )
        );

        System.out.println(handler.getUserProfile(key));

        handler.disconnect();
    }
}
