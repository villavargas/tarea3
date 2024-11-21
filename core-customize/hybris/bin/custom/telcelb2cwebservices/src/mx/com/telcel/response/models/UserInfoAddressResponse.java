package mx.com.telcel.response.models;

import java.util.List;
import mx.com.telcel.models.Error;
import mx.com.telcel.models.AddressUser;

public class UserInfoAddressResponse {
    
    private Error error;
    private List<AddressUser> addressUser;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public List<AddressUser> getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(List<AddressUser> addressUser) {
        this.addressUser = addressUser;
    }
}