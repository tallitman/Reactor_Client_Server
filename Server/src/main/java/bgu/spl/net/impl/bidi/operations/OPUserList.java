package bgu.spl.net.impl.bidi.operations;

public class OPUserList extends Operation {
    public OPUserList() {
        super();
        this.fOPcode = 7;
        fIsReady = true;
    }

    @Override
    public void decodeNextByte(byte nextByte) {

    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
