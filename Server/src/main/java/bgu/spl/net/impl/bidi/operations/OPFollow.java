package bgu.spl.net.impl.bidi.operations;

import bgu.spl.net.impl.bidi.BidiMessageEncoderDecoder;

public class OPFollow extends Operation {
    private int fNumOfUsers;

    public OPFollow() {
        super();
        this.fOPcode = 4;
    }

    @Override
    public void decodeNextByte(byte nextByte) {

        if (fDel == 0) {
            fDel++;
            pushByte(nextByte);
            fArguments.add(popString());
        } else if (fDel == 1) {
            pushByte(nextByte);
            if (len == 2) {
                short numOfUsers = BidiMessageEncoderDecoder.bytesToShort(fBytes);
                String t_numOfUsers = Short.toString(numOfUsers);
                this.fBytes = new byte[1 << 10];
                len = 0;
                fArguments.add(t_numOfUsers);
                fNumOfUsers = Integer.parseInt(t_numOfUsers);
                fDel++;
            }
        } else {
            if (nextByte == '\0') {
                fDel++;
                fArguments.add(popString());
            } else pushByte(nextByte);
        }
        if (fDel - 2 == fNumOfUsers)
            fIsReady = true;

    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
