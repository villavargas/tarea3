/*
 *
 * [y] hybris Platform
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package mx.com.telcel.facades.user.impl;

import de.hybris.platform.b2ctelcofacades.user.impl.DefaultTmaUserFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.UserData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import mx.com.telcel.core.models.paymentcommerce.CardResponse;
import mx.com.telcel.core.models.paymentcommerce.GetCardsResponse;
import mx.com.telcel.core.models.paymentcommerce.ValidateTokenResponse;
import mx.com.telcel.core.services.paymentcommerce.PaymentCommerceService;
import mx.com.telcel.core.util.TelcelUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


public class TelcelDefaultUserFacade extends DefaultTmaUserFacade
{

    private static final Logger LOG = LoggerFactory.getLogger(TelcelDefaultUserFacade.class);
    private static final String SECURITY_CODE_TEST = "telcel.performance.test.enabled";

    @Resource
    private Converter<CreditCardType, CardTypeData> cardTypeConverter;

    private Populator<AddressData, AddressModel> telcelAddressReversePopulator;

    @Resource(name = "paymentCommerceService")
    private PaymentCommerceService paymentCommerceService;

    @Resource
    private TelcelUtil telcelUtil; // NOSONAR

    @Override
    public List<CCPaymentInfoData> getCCPaymentInfos(final boolean saved)
    {
        final CustomerModel currentCustomer = getCurrentUserForCheckout();
        String uid =	currentCustomer != null ? currentCustomer.getUid() : "";
        Pattern pat = Pattern.compile(".*@saplogtest.com.*");
        Matcher mat = pat.matcher(uid);

        if(Boolean.valueOf(Config.getParameter(SECURITY_CODE_TEST)) && mat.matches())
        {
            return new ArrayList<>();
        }

        final List<CreditCardPaymentInfoModel> creditCards = getCustomerAccountService().getCreditCardPaymentInfos(currentCustomer,
                saved);
        final List<CCPaymentInfoData> ccPaymentInfos = new ArrayList<>();
        final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
        for (final CreditCardPaymentInfoModel ccPaymentInfoModel : creditCards)
        {
            final CCPaymentInfoData paymentInfoData = getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);
            if (ccPaymentInfoModel.equals(defaultPaymentInfoModel))
            {
                paymentInfoData.setDefaultPaymentInfo(true);
                ccPaymentInfos.add(0, paymentInfoData);
            }
            else
            {
                ccPaymentInfos.add(paymentInfoData);
            }
        }

        GetCardsResponse getCardsResponse = null;

        try {
            final String emailUid = CustomerType.GUEST.equals(currentCustomer.getType()) ? StringUtils.substringAfter(
                    currentCustomer.getUid(), "|") : telcelUtil.getEmailRegisterForToken(currentCustomer.getUid(),currentCustomer.getContactEmail() );

            ValidateTokenResponse validateTokenResponse = getToken(emailUid);
            LOG.info("Token para get ["+validateTokenResponse.getToken()+"]");
            getCardsResponse = paymentCommerceService.getCards(validateTokenResponse.getToken(), emailUid);

        }catch (Exception e){
            LOG.error("Ocurrio un error al obtener las tarjetas "+e.getLocalizedMessage());
        }

        final List<CCPaymentInfoData> ccPaymentInfosGestor = new ArrayList<>();

        if(getCardsResponse != null){
            List<CardResponse>  cardResponse = getCardsResponse.getCards();
            if(cardResponse != null){
                LOG.info("cardResponse ["+cardResponse.toString()+"]");
                for(CardResponse cardResp : cardResponse){
                    if("ACTIVE".equalsIgnoreCase(cardResp.getStatus())){
                        for(CCPaymentInfoData paymentInfoData: ccPaymentInfos){
                            if(cardResp.getCardToken().equalsIgnoreCase(paymentInfoData.getTokenCard())){
                                String[] expDate = cardResp.getExpDate() != null ? cardResp.getExpDate().split("/") : null ;
                                String expiryMonth = expDate != null ? expDate[0] : "" ;
                                String expiryYear = expDate != null ? expDate[1] : "" ;
                                CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();
                                //ccPaymentInfoData.setAccountHolderName(cardResp.getHolderName());
                                ccPaymentInfoData.setAccountHolderName(paymentInfoData.getHolderName()+" "+paymentInfoData.getHolderLastName());
                                ccPaymentInfoData.setBillingAddress(paymentInfoData.getBillingAddress());
                                ccPaymentInfoData.setCardNumber(paymentInfoData.getCardNumber());
                                ccPaymentInfoData.setCardType(paymentInfoData.getCardType());
                                ccPaymentInfoData.setDefaultPaymentInfo(paymentInfoData.isDefaultPaymentInfo());
                                ccPaymentInfoData.setExpiryMonth(expiryMonth);
                                ccPaymentInfoData.setExpiryYear(expiryYear);
                                ccPaymentInfoData.setId(paymentInfoData.getId());
                                ccPaymentInfoData.setPaymentType(paymentInfoData.getPaymentType());
                                ccPaymentInfoData.setSaved(paymentInfoData.isSaved());
                                ccPaymentInfoData.setSubscriptionId(paymentInfoData.getSubscriptionId());
                                ccPaymentInfoData.setTokenCard(cardResp.getCardToken());
                                ccPaymentInfoData.setPromotions(paymentInfoData.getPromotions());
                                ccPaymentInfoData.setZipCode(paymentInfoData.getZipCode());
                                ccPaymentInfoData.setPaymentReferenceName(paymentInfoData.getPaymentReferenceName());
                                ccPaymentInfosGestor.add(ccPaymentInfoData);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return ccPaymentInfosGestor;
    }

    private ValidateTokenResponse getToken(String uid) {
        ValidateTokenResponse validateTokenResponse = null;
        try {
            validateTokenResponse = paymentCommerceService.getTokenByUid(uid);
        } catch (Exception e) {
            LOG.error("Error al obtener token =" + e.getLocalizedMessage());
        }
        return validateTokenResponse;
    }

    @Override
    public CCPaymentInfoData getCCPaymentInfoForCode(final String code)
    {
        if (StringUtils.isNotBlank(code))
        {
            final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
            final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService()
                    .getCreditCardPaymentInfoForCode(currentCustomer, code);
            if (ccPaymentInfoModel != null)
            {
                final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
                final CCPaymentInfoData paymentInfoData = getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);
                if (ccPaymentInfoModel.equals(defaultPaymentInfoModel))
                {
                    paymentInfoData.setDefaultPaymentInfo(true);
                }
                return paymentInfoData;
            }
        }

        return null;
    }


    @Override
    public void removeCCPaymentInfo(final String id)
    {
        validateParameterNotNullStandardMessage("id", id);
        final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
        for (final CreditCardPaymentInfoModel creditCardPaymentInfo : getCustomerAccountService()
                .getCreditCardPaymentInfos(currentCustomer, false))
        {
            if (creditCardPaymentInfo.getPk().toString().equals(id))
            {
                try {
                    final String emailUid = CustomerType.GUEST.equals(currentCustomer.getType()) ? StringUtils.substringAfter(
                            currentCustomer.getUid(), "|") : telcelUtil.getEmailRegisterForToken(currentCustomer.getUid(),currentCustomer.getContactEmail() );

                    ValidateTokenResponse validateTokenResponse = getToken(emailUid);
                    LOG.info("Token delete para get ["+validateTokenResponse.getToken()+"]");
                    if(StringUtils.isNotBlank(creditCardPaymentInfo.getTokenCardTelcel())){
                        CardResponse cardResponse = paymentCommerceService.deleteCard(creditCardPaymentInfo.getTokenCardTelcel(),validateTokenResponse.getToken());
                        getCustomerAccountService().deleteCCPaymentInfo(currentCustomer, creditCardPaymentInfo);
                    }


                }catch (Exception e){
                    LOG.error("Ocurrio un error al eliminar la tarjetas "+e.getLocalizedMessage());
                }
                break;
            }
        }
        updateDefaultPaymentInfo(currentCustomer);
    }




    @Override
    public void addAddress(final AddressData addressData)
    {
        validateParameterNotNullStandardMessage("addressData", addressData);

        final CustomerModel currentCustomer = getCurrentUserForCheckout();

        final boolean makeThisAddressTheDefault = addressData.isDefaultAddress()
                || (currentCustomer.getDefaultShipmentAddress() == null && addressData.isVisibleInAddressBook());

        // Create the new address model
        final AddressModel newAddress = getModelService().create(AddressModel.class);
        LOG.info("Numero interior: "+ addressData.getInteriorNumber());
        LOG.info("Numero exterior: "+ addressData.getExternalNumber());
        LOG.info("Referencia: "+ addressData.getReferences());
        LOG.info("Region: "+ addressData.getRegion().getName());
        getAddressReversePopulator().populate(addressData, newAddress);
        getTelcelAddressReversePopulator().populate(addressData, newAddress);

        // Store the address against the user
        getCustomerAccountService().saveAddressEntry(currentCustomer, newAddress);

        // Update the address ID in the newly created address
        addressData.setId(newAddress.getId());

        if (makeThisAddressTheDefault)
        {
            getCustomerAccountService().setDefaultAddressEntry(currentCustomer, newAddress);
        }
    }


    @Override
    public void editAddress(final AddressData addressData)
    {
        validateParameterNotNullStandardMessage("addressData", addressData);
        final CustomerModel currentCustomer = getCurrentUserForCheckout();
        final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, addressData.getId());
        LOG.info("Numero interior: "+ addressData.getInteriorNumber());
        LOG.info("Numero exterior: "+ addressData.getExternalNumber());
        LOG.info("Referencia: "+ addressData.getReferences());
        if (addressData.getRegion() != null){
            LOG.info("Region: "+ addressData.getRegion().getName());
        }
        getAddressReversePopulator().populate(addressData, addressModel);
        getTelcelAddressReversePopulator().populate(addressData, addressModel);
        getCustomerAccountService().saveAddressEntry(currentCustomer, addressModel);
        if (addressData.isDefaultAddress())
        {
            getCustomerAccountService().setDefaultAddressEntry(currentCustomer, addressModel);
        }
        else if (addressModel.equals(currentCustomer.getDefaultShipmentAddress()))
        {
            getCustomerAccountService().clearDefaultAddressEntry(currentCustomer);
        }
    }



    public Converter<CreditCardType, CardTypeData> getCardTypeConverter() {
        return cardTypeConverter;
    }

    public void setCardTypeConverter(Converter<CreditCardType, CardTypeData> cardTypeConverter) {
        this.cardTypeConverter = cardTypeConverter;
    }

    public Populator<AddressData, AddressModel> getTelcelAddressReversePopulator() {
        return telcelAddressReversePopulator;
    }

    public void setTelcelAddressReversePopulator(Populator<AddressData, AddressModel> telcelAddressReversePopulator) {
        this.telcelAddressReversePopulator = telcelAddressReversePopulator;
    }
}
