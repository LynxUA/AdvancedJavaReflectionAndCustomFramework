package application.classes;

import framework.core.annotations.Required;

public class Bus implements Transport {
    @Required
    private String message;

   //@Required
    private String lol;
    
    public Bus() {
        message = "I am the Bus!";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void getTransport() {
        System.out.println(message);
    }
}
