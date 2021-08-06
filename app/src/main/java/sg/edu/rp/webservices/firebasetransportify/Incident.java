package sg.edu.rp.webservices.firebasetransportify;

import java.io.Serializable;
import java.util.Date;

public class Incident implements Serializable {

    private String type;
    private String message;
    private Date date;

    public Incident() {
    }

    public Incident(String type, String message, Date date) {
        this.type = type;
        this.message = message;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}