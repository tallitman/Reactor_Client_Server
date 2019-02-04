package bgu.spl.net.impl.bidi.operations;

import java.util.ArrayList;
import java.util.List;

public class OPPost extends Operation {
    private final int fMaxDel;

    public OPPost() {
        super();
        this.fOPcode = 5;
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

    /**
     * finding tagged users inside the post content
     *
     * @return List<String> when each string represents a tagged username in the post message
     **/
    public List<String> getTaggedUsers() {
        String post = fArguments.get(0);
        List<String> users = new ArrayList<>();
        StringBuffer user = new StringBuffer("");
        for (int i = 0; i < post.length(); i++) {
            if (post.charAt(i) == '@') {
                int j = i + 1;
                while (j < post.length() && post.charAt(j) != ' ') {
                    user.append(post.charAt(j));
                    j++;
                }
                i = j;
                if (!users.contains(user.toString())) {
                    users.add(user.toString());
                }
                user = new StringBuffer("");

            }
        }
        return users;
    }


    @Override
    public byte[] encode() {
        return new byte[0];

    }
}
