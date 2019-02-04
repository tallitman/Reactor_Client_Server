package bgu.spl.net.impl.bidi.operations;

public class OPLogout extends Operation {
    public OPLogout() {
        super();
        this.fOPcode = 3;
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
