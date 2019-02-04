package bgu.spl.net.impl.bidi.operations;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Operation {
    /**
     * The {@link Operation} class is an abstract class represents the
     * what we enc/dec each of our messages
     * each Operation Objects hold a message arguments {@fArguments}
     * wich represents the message content depends on each message
     * some of the message is used in Client to server which mainly decode bytes recieved from client
     * the others is a Server to client (push) therfore used to encode Message/answer to the client e.g: notification,ack,error
     */
    protected List<Byte> fBytesToEncode;//bytes we encode
    protected short fOPcode;//message OP code
    protected List<String> fArguments;//message arugments
    protected boolean fIsReady;//if encoding/decoding a message is done
    protected byte[] fBytes;
    protected int fDel = 0;
    protected int len = 0;
    public static final short REGISTER = 1, LOGIN = 2, LOGOUT = 3, FOLLOW = 4, POST = 5, PM = 6, USERLIST = 7, STAT = 8;

    public Operation() {
        this.fArguments = new ArrayList<>();
        this.fIsReady = false;
        this.fBytes = new byte[1 << 10];
        this.fBytesToEncode = new ArrayList<>();
    }

    public short getfOPcode() {
        return fOPcode;
    }

    public List<String> getfArguments() {
        return fArguments;
    }

    public boolean isReady() {
        return fIsReady;
    }

    public void pushByte(byte nextByte) {
        if (len >= fBytes.length) {
            fBytes = Arrays.copyOf(fBytes, len * 2);
        }

        fBytes[len++] = nextByte;
    }

    protected String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(fBytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        fBytes = new byte[1 << 10];
        return result;

    }

    protected void addToBytesList(byte[] source) {
        for (byte b : source) {
            fBytesToEncode.add(new Byte(b));
        }
    }

    @Override
    public String toString() {
        return "Operation{" +
                "fOPcode=" + fOPcode +
                '}';
    }

    protected byte[] toArray() {
        byte[] ans = new byte[fBytesToEncode.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = fBytesToEncode.get(i).byteValue();
        }
        fBytesToEncode.clear();
        return ans;
    }

    public abstract void decodeNextByte(byte nextByte);

    public abstract byte[] encode();


}






