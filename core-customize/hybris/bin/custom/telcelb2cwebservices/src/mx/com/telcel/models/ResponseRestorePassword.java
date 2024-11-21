package mx.com.telcel.models;

public class ResponseRestorePassword {

    private ErrorRestorePassword error;

    /**
     * @return the error
     */
    public ErrorRestorePassword getError()
    {
        return error;
    }

    /**
     * @param error
     *           the error to set
     */
    public void setError(final ErrorRestorePassword error)
    {
        this.error = error;
    }
}
