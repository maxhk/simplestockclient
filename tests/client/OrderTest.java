package client;

import market_proto.Market;
import org.junit.Test;

/**
 * Created by max on 9/5/15.
 */
public class OrderTest {

    @Test
    public void testFromString() throws Exception {
        String text = "B 23 GoogleInc at 99.2";
        Order order = Order.fromString(text);
        assert order.getWay() == Market.Order.OrderWay.BUY;
        assert order.getQuantity() == 23;
        assert order.getInstrument().equals("GoogleInc");
        assert order.getPrice() == 99.2f;

        text = "S 23 GoogleInc at 99";
        order = Order.fromString(text);
        assert order.getWay() == Market.Order.OrderWay.SELL;
        assert order.getQuantity() == 23;
        assert order.getInstrument().equals("GoogleInc");
        assert order.getPrice() == 99f;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringFail() throws Exception {
        String text = "W 23 GoogleInc at 99.2";
        Order.fromString(text);

    }
}
