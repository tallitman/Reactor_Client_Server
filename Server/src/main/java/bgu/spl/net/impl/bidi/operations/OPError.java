package bgu.spl.net.impl.bidi.operations;

import bgu.spl.net.impl.bidi.BidiMessageEncoderDecoder;

public class OPError extends Operation {
    private short fsenderOP = 0;

    public OPError(short op) {
        super();
        this.fsenderOP = op;
        this.fOPcode = 11;
    }

    @Override
    public void decodeNextByte(byte nextByte) {

    }

    @Override
    public byte[] encode() {
        byte[] ack = BidiMessageEncoderDecoder.shortToBytes(fOPcode);
        addToBytesList(ack);
        byte[] sender = BidiMessageEncoderDecoder.shortToBytes(fsenderOP);
        addToBytesList(sender);
        return toArray();
    }
}
