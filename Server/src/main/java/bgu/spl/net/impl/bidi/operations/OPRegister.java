package bgu.spl.net.impl.bidi.operations;

public class OPRegister extends Operation {

    private final int fMaxDel;

    public OPRegister() {
        super();
        this.fOPcode = 1;
        fMaxDel = 2;
    }

    @Override
    public String toString() {
        return "OPRegister{" +
                "fArguments=" + fArguments +
                '}';
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
        return null;
    }


}
