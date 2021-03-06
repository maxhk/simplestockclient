# Definitions

+ Order: [I]nstrument, [Q]uantity, [P]rice, [W]ay, [C]lient Order Id
+ Actions: [N]ew, [C]ancel


# Protocol

TCP


## New

message New {
    required string instrument = 1;
    required int64 quantity = 2;
    required float price = 3;

    enum OrderWay {
        BUY = 0;
        SELL = 1;
    }

    required OrderWay way = 4;

    required int64 client_order_id = 5;
}

message Cancel {
    required int64 client_order_id = 1;
}

message UpdateOrder {
    required int64 client_order_id = 1;

    enum OrderStatus {
        ACK = 0;
        NACK = 1;
        FILLED = 2;
    }

    required OrderStatus status = 2;

    optional int32 exec_qty = 3;

    optional int32 remaining_qty = 4;

    optional float exec_price = 5;

    optional string messasge = 6;

}

message ConnectionRequest {
    required int64 client_id = 1;

    required string password = 2;
}

message ConnectionResponse {
    
    enum SessionStatus {
        ACCEPTED  = 0;
        REJECTED = 1;
    }

    required SessionStatus status = 1;

    optional string message = 2;

}
