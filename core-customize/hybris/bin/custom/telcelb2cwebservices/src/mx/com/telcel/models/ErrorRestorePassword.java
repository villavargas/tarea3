package mx.com.telcel.models;

public class ErrorRestorePassword {

    private int code;
    private String description;
    private String loginName;

    /**
     * @return the code
     */
    public int getCode()
    {
        return code;
    }

    /**
     * @param code
     *           the code to set
     */
    public void setCode(final int code)
    {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description
     *           the description to set
     */
    public void setDescription(final String description)
    {
        this.description = description;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
