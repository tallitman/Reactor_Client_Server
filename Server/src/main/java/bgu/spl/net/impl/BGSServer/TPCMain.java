package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.bidi.BidiProtocolImp;
import bgu.spl.net.impl.bidi.BidiMessageEncoderDecoder;
import bgu.spl.net.impl.database.DataBase;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args){
        DataBase db = new DataBase();
        Server.threadPerClient(Integer.parseInt(args[0]),
        ()-> new BidiProtocolImp(db),
        ()-> new BidiMessageEncoderDecoder()
        ).serve();

    }
}
