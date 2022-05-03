package org.acm.auth.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseUtil {
    private static MongoClient databaseInstance;
    private static String DAYS_OF_CODING_DATABASE_NAME;
    private static final Logger LOGGER = LogManager.getLogger(DatabaseUtil.class);

    private DatabaseUtil() {

    }

    public static MongoClient getMongoClientInstance() {
        if(databaseInstance != null)
            return databaseInstance;

        Dotenv dotenv = Dotenv.configure().load();
        String connectionString = dotenv.get("MONGO_DATABASE_URL", "");

        if(connectionString.equals("")) {
            LOGGER.fatal("Cannot connect to MongoDB instance");
            System.exit(-1);
        } else {
            databaseInstance = MongoClients.create(connectionString);
            LOGGER.info("Connected to MongoDB instance on " + connectionString);
        }

        return databaseInstance;
    }

    public static String getDaysOfCodingDatabaseName() {
        if(DAYS_OF_CODING_DATABASE_NAME != null)
            return DAYS_OF_CODING_DATABASE_NAME;

        Dotenv dotenv = Dotenv.configure().load();
        String databaseName = dotenv.get("MONGO_DATABASE_NAME", "");

        if(databaseName.equals("")) {
            LOGGER.fatal("Cannot find database name for MongoDB instance");
            System.exit(-1);
        } else {
            DAYS_OF_CODING_DATABASE_NAME = databaseName;
            LOGGER.info("Found database name for MongoDB instance: " + DAYS_OF_CODING_DATABASE_NAME);
        }

        return DAYS_OF_CODING_DATABASE_NAME;
    }
}
