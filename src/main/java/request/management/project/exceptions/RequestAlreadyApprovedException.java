package request.management.project.exceptions;

public class RequestAlreadyApprovedException extends Exception{
    public RequestAlreadyApprovedException(){
        super("Request already approved");
    }
}
