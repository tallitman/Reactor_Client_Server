package bgu.spl.net.impl.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.bidi.operations.*;

public class BidiMessageEncoderDecoder implements MessageEncoderDecoder<Operation> {
    private byte[] bytes;
    private int len = 0;
    private Operation fOperation;
    private boolean fNextOp;

    public BidiMessageEncoderDecoder() {
        this.fNextOp=true;
        this.bytes = new byte[2];
        this.len=0;
    }

    /**
     *
     * @param nextByte the next byte to consider for the currently decoded
     * message
     * @return an Operation Objects represent an encoded/decoded message when the messae is ready to use by the protocol
     */
    @Override
    public Operation decodeNextByte(byte nextByte) {
        if (len < 2) {
            if(fNextOp)
                fOperation=null;
            bytes[len] = nextByte;
            len++;
        }
        if (len == 2 && fNextOp) {
            short opcode = bytesToShort(bytes);
            fNextOp=false;
            switch (opcode) {
                case Operation.REGISTER:
                    fOperation = new OPRegister();
                    break;
                case Operation.LOGIN:
                    fOperation = new OPLogin();
                    break;
                case Operation.LOGOUT:
                    fOperation = new OPLogout();
                    break;
                case Operation.FOLLOW:
                    fOperation = new OPFollow();
                    break;
                case Operation.POST:
                    fOperation = new OPPost();
                    break;
                case Operation.PM:
                    fOperation = new OPPm();
                    break;
                case Operation.USERLIST:
                    fOperation = new OPUserList();
                    break;
                case Operation.STAT:
                    fOperation = new OPStat();
                    break;
            }
            len++;
        }
        else if(len>=3&&fOperation!=null) {
            fOperation.decodeNextByte(nextByte);

        }
        if(fOperation!=null && fOperation.isReady()){
            fNextOp = true;
            len = 0;
            return fOperation;
        }
        return null;
    }

    @Override
    public byte[] encode(Operation op) {
        return op.encode();
    }


    public static byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }


    public static short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }



}
