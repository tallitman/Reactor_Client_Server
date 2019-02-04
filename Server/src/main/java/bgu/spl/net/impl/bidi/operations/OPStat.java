package bgu.spl.net.impl.bidi.operations;

public class OPStat extends Operation {
    private final int fMaxDel;

    public OPStat() {
        super();
        this.fOPcode = 8;
        fMaxDel = 1;
    }

    @Override
    public void decodeNextByte(byte nextByte) {
        if (nextByte == '\0') {
            fDel++;
            fArguments.add(popString());
        } else {
            pushByte(nextByte);
        }
        if (fDel == fMaxDel) {

            fIsReady = true;
            return;
        }
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
