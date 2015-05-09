package client;


import market_proto.Market;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;



public class Main {

    private static final Integer DEFAULT_MARKET_PORT = 888;

    public static void main(String[] args) throws IOException, ParseException {

        Logger  logger = Logger.getLogger("client.Main");
        logger.setLevel(Level.INFO);


        // Parse command line arguments

        Properties config = new Properties();
        config.load(Main.class.getClassLoader().getResourceAsStream("config.properties"));

        String marketHost = config.getProperty("market_host");
        String marketPortStr = config.getProperty("market_port");
        logger.info(String.format("Market host = %s", marketHost));
        logger.info(String.format("Market port = %s", marketPortStr));


        int marketPort;
        try {
            marketPort = Integer.parseInt(marketPortStr);
        } catch (NumberFormatException e) {
            marketPort = DEFAULT_MARKET_PORT;
            logger.warn(String.format("Could not convert market port '%s' to int. Using default port instead (%d)", marketPortStr, DEFAULT_MARKET_PORT));
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

        if (connectionResponse.getStatus() == Market.ConnectionResponse.SessionStatus.REJECTED) {
            logger.error("Connection request rejected by server. Reason: " + connectionResponse.getMessage());
            logger.error("Quitting...");
            System.exit(1);
        }

        logger.info("Connection established");


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        boolean exit = false;
        while (!exit) {
            logger.info("reading from commandline");
            String command = bufferedReader.readLine();
            if (command.trim().equals("quit")) {
                exit = true;
            } else {
                try {
                    Order order = Order.fromString(command);
                    order.writeNewToStream(socket.getOutputStream());
                    socket.getOutputStream().flush();
                } catch (IllegalArgumentException e) {
                    logger.warn("Could not parse order from text: " + command);
                }

            }
        }
    }
}
