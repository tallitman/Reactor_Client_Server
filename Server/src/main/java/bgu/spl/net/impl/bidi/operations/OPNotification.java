package bgu.spl.net.impl.bidi.operations;

import bgu.spl.net.impl.bidi.BidiMessageEncoderDecoder;

import java.util.List;

public class OPNotification extends Operation {
    private short fSenderOP;

    public OPNotification(List<String> arguments) {
        super();
        fArguments = arguments;
        this.fOPcode = 9;
        this.fSenderOP = Short.parseShort(fArguments.get(0));
    }

    @Override
    public void decodeNextByte(byte nextByte) {

    }

    @Override
    public byte[] encode() {
        byte[] notificationOP = BidiMessageEncoderDecoder.shortToBytes(fOPcode);
        addToBytesList(notificationOP);
        if (fSenderOP == 5) {//post
            addToBytesList(new byte[]{1});
        } else {
            addToBytesList(new byte[]{0});
        }
        for (int i = 1; i < fArguments.size(); i++) {
            byte[] userBytes = fArguments.get(i).getBytes();
            addToBytesList(userBytes);
            fBytesToEncode.add(new Byte((byte) '\0'));
        }
        return toArray();
    }

}
