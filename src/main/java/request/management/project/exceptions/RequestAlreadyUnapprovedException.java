package request.management.project.exceptions;

public class RequestAlreadyUnapprovedException extends Exception{
    public RequestAlreadyUnapprovedException(){
        super("Request already unapproved");
    }
}
