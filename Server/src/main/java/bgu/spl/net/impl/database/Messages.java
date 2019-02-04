package bgu.spl.net.impl.database;

import java.time.LocalDateTime;

public abstract class Messages {

    private String fSender;
    private String fContent;
    private final LocalDateTime fDate;

    public Messages(String fSender, String fContent) {
        fDate = LocalDateTime.now();
        this.fSender = fSender;
        this.fContent = fContent;
    }

    public String getfSender() {
        return fSender;
    }

    public String getfContent() {
        return fContent;
    }

    public LocalDateTime getfDate() {
        return fDate;
    }
}
