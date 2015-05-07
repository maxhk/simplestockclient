package client;

import market_proto.Market;

/**
 * Created by Max on 07/05/2015.
 */


public class Main {

    public static void main(String[] args) {
        Market.New newOrder = Market.New.newBuilder().setClientOrderId(1).setInstrument("hsbc").setPrice(10f).setQuantity(2).setWay(Market.New.OrderWay.BUY).build();
        System.out.println(newOrder.toString());


    }
}
