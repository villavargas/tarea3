package mx.com.telcel.facades.customer.impl;

import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerEmailResolutionService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.util.mail.MailUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class DefaultTelcelCustomerEmailResolutionService extends DefaultCustomerEmailResolutionService {

    private static final Logger LOG = Logger.getLogger(DefaultTelcelCustomerEmailResolutionService.class);

    @Override
    protected String validateAndProcessEmailForCustomer(final CustomerModel customerModel)
    {
        validateParameterNotNullStandardMessage("customerModel", customerModel);

        final String email = CustomerType.GUEST.equals(customerModel.getType()) ? StringUtils.substringAfter(
                customerModel.getDetailEmail(), "|") : customerModel.getDetailEmail();
        try
        {
            MailUtils.validateEmailAddress(email, "customer email");
            return email;
        }
        catch (final EmailException e) //NOSONAR
        {
            LOG.info("Given uid is not appropriate email. Customer PK: " + customerModel.getPk() + " Exception: "
                    + e.getClass().getName());
        }
        return null;
    }

    @Override
    public String getEmailForCustomer(CustomerModel customerModel) {

        final String emailAfterProcessing = validateAndProcessEmailForCustomer(customerModel);
        if (StringUtils.isNotEmpty(emailAfterProcessing))
        {
            return emailAfterProcessing;
        }

        if (CustomerType.GUEST.equals(customerModel.getType())) {
            return StringUtils.substringAfter(customerModel.getUid(), "|");
        }

        return getConfigurationService().getConfiguration().getString(DEFAULT_CUSTOMER_KEY, DEFAULT_CUSTOMER_EMAIL);
    }
}
