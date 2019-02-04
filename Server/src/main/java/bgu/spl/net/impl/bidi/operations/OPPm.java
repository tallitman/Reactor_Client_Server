package bgu.spl.net.impl.bidi.operations;

public class OPPm extends Operation {
    private final int fMaxDel;

    public OPPm() {
        super();
        this.fOPcode = 6;
        fMaxDel = 2;

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
