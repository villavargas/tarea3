/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservices.core.validator;

import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * The type Telcel address ws dto validator.
 */
public class TelcelAddressWsDTOValidator implements Validator {

    private static final int SIZE_POSTAL_CODE = 5;
    private static final int SIZE_CONTACT_PHONE_NUMBER = 10;
    /**
     * The constant ONLY_NUMBERS_REGEX.
     */
    private static final String ONLY_NUMBERS_REGEX = "[0-9]+";

    @Override
    public boolean supports(Class<?> aClass) {
        return AddressWsDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "firstName", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "lastName", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "line1", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "externalNumber", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "postalCode", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "district", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "town", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "region", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "phone", "field.required");

        final AddressWsDTO customerCheckoutAddressWsDTO = (AddressWsDTO) o;
        final String postalCode = customerCheckoutAddressWsDTO.getPostalCode();
        final String contactPhoneNumber = customerCheckoutAddressWsDTO.getPhone();
        if (postalCode.length() != SIZE_POSTAL_CODE || !validateOnlyNumbers(postalCode)) {
            errors.reject("postalCode");
        }
        if (contactPhoneNumber.length() != SIZE_CONTACT_PHONE_NUMBER || !validateOnlyNumbers(contactPhoneNumber)) {
            errors.reject("phone");
        }
    }

    private boolean validateOnlyNumbers(String fieldValue) {
        return fieldValue.matches(ONLY_NUMBERS_REGEX);
    }
}
