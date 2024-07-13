package request.management.project.exceptions;

public class EmptyReasonException extends Exception{
    public EmptyReasonException(){
        super("Please, inform your reason to disapproving the request.");
    }
}
