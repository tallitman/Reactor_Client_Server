package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.bidi.BidiMessageEncoderDecoder;
import bgu.spl.net.impl.bidi.BidiProtocolImp;
import bgu.spl.net.impl.database.DataBase;
import bgu.spl.net.srv.Server;
//(nthreads, port, protocolFactory, encoderDecoderFactory);
public class ReactorMain {
    public static void main(String[] args){
        DataBase db = new DataBase();
        Server.reactor(Integer.parseInt(args[1]),Integer.parseInt(args[0]),
                ()-> new BidiProtocolImp(db),
                ()-> new BidiMessageEncoderDecoder()
        ).serve();

    }
}
