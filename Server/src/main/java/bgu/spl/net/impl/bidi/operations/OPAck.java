package bgu.spl.net.impl.bidi.operations;

import bgu.spl.net.impl.bidi.BidiMessageEncoderDecoder;

import java.util.List;


public class OPAck extends Operation {

    private short fSenderOP;

    public OPAck(List<String> arguments) {
        super();
        fArguments = arguments;
        this.fOPcode = 10;
        this.fSenderOP = Short.parseShort(fArguments.get(0));

    }

    @Override
    public void decodeNextByte(byte nextByte) {

    }

    @Override
    public byte[] encode() {
        byte[] ack = BidiMessageEncoderDecoder.shortToBytes(fOPcode);
        addToBytesList(ack);
        byte[] sender = BidiMessageEncoderDecoder.shortToBytes(fSenderOP);
        addToBytesList(sender);
        char del = '\0';
        switch (fSenderOP) {
            case 4:
            case 7: {
                short numOfUsers = Short.parseShort(fArguments.get(1));
                byte[] numofUsersByte = BidiMessageEncoderDecoder.shortToBytes(numOfUsers);
                addToBytesList(numofUsersByte);
                for (int i = 2; i < fArguments.size(); i++) {
                    byte[] userBytes = fArguments.get(i).getBytes();
                    addToBytesList(userBytes);
                    fBytesToEncode.add(new Byte((byte) del));
                }
            }
            break;
            case 8: {
                short numOfPosts = Short.parseShort(fArguments.get(1));
                byte[] numofPostsByte = BidiMessageEncoderDecoder.shortToBytes(numOfPosts);
                addToBytesList(numofPostsByte);
                short numOfFollowers = Short.parseShort(fArguments.get(2));
                byte[] numOfFollowersByte = BidiMessageEncoderDecoder.shortToBytes(numOfFollowers);
                addToBytesList(numOfFollowersByte);
                short numofFollowing = Short.parseShort(fArguments.get(3));
                byte[] numofFollowingBytes = BidiMessageEncoderDecoder.shortToBytes(numofFollowing);
                addToBytesList(numofFollowingBytes);
            }
            break;
        }
        return toArray();
    }

}
