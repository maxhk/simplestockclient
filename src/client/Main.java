package client;


import market_proto.Market;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;


/**
 * Created by Max on 07/05/2015.
 */


public class Main {

    private static final Integer DEFAULT_MARKET_PORT = 888;

    public static void main(String[] args) throws IOException, ParseException {

        Logger  logger = Logger.getLogger("client.Main");
        logger.setLevel(Level.INFO);


        // Parse command line arguments
        BasicParser parser = new BasicParser();
        Options opts = new Options();
        opts.addOption("config", true, "Path to the config file");
        CommandLine cmd = parser.parse(opts, args);
        String configFilepath = cmd.getOptionValue("config");

        // Parse config file
        if ( configFilepath == null) {
            logger.error("--config missing from command line arguments");
            System.exit(1);
        }

        logger.info(String.format("Loading configuration from %s", configFilepath));
        Properties config = new Properties();
        config.load(new FileInputStream(configFilepath));

        String marketHost = config.getProperty("market_host");
        String marketPortStr = config.getProperty("market_port");
        logger.info(String.format("Market host = %s", marketHost));
        logger.info(String.format("Market port = %s", marketPortStr));


        int marketPort = 0;
        try {
            marketPort = Integer.parseInt(marketPortStr);
        } catch (NumberFormatException e) {
            marketPort = DEFAULT_MARKET_PORT;
            logger.warn(String.format("Could not convert market port '%s' to int. Using default port instead (%d)", new Object[]{marketPortStr, DEFAULT_MARKET_PORT}));
        }


        // Read and process connection parameters
        String clientIdStr = config.getProperty("client_id");
        Long clientId = null;
        if (clientIdStr == null) {
            logger.error("client_id not set. Exiting");
            System.exit(1);
        }
        try {
            clientId = Long.parseLong(clientIdStr);
        } catch (NumberFormatException e) {
            logger.error(String.format("Wrong client_id format: '%s'. Expected numeric value. Exiting", clientIdStr));
            System.exit(1);
        }

        String clientPassword = config.getProperty("client_password", "");

        // Connecting to exchange
        Socket socket = new Socket(marketHost, marketPort);

        // Create connection request message and send it
        Market.ConnectionRequest cnxReq = Market.ConnectionRequest.newBuilder().setClientId(clientId).setPassword(clientPassword).build();
        cnxReq.writeDelimitedTo(socket.getOutputStream());
        socket.getOutputStream().flush();
        logger.info("Connection request sent to exchange: " + cnxReq.toString().replaceAll("\n", " ; "));

        // Read response from the exchange
        Market.ConnectionResponse connectionResponse = Market.ConnectionResponse.parseFrom(socket.getInputStream());
        logger.info("Connection response received from exchange: " + connectionResponse.toString().replaceAll("\n", " ; "));









    }
}
