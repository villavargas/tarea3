/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package mx.com.telcel.validator;

import mx.com.telcel.dto.CustomerCheckoutInfWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Customer checkout inf ws dto validator.
 */
public class CustomerCheckoutInfWsDTOValidator implements Validator {

    private static final String EMAIL_PATTERN = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@" + // NOSONAR
            "[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";
    private static final int SIZE_CURP = 18;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // NOSONAR

    @Override
    public boolean supports(Class<?> aClass) {
        return CustomerCheckoutInfWsDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        ValidationUtils.rejectIfEmpty(errors, "name", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "lastName", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "dateOfBirth", "field.required");
        ValidationUtils.rejectIfEmpty(errors, "email", "field.required");

        final CustomerCheckoutInfWsDTO customerCheckoutInfWsDTO = (CustomerCheckoutInfWsDTO) o;
        final String emailValue = customerCheckoutInfWsDTO.getEmail();
        final String curpValue = customerCheckoutInfWsDTO.getCurp();
        final String dateOfBirthValue = customerCheckoutInfWsDTO.getDateOfBirth();
        if (validateEmail(emailValue)) {
            errors.reject("email");
        }
        if (StringUtils.isNotEmpty(curpValue) && curpValue.length() != SIZE_CURP) {
            errors.reject("curp");
        }
        if (validateDateOfBirth(dateOfBirthValue)) {
            errors.reject("dateOfBirth");
        }
    }


    private boolean validateEmail(String emailValue) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        boolean result = false;
        Matcher matcher = pattern.matcher(emailValue);
        if (!matcher.matches()) {
            result = true;
        }
        return result;
    }

    private boolean validateDateOfBirth(String dateOfBirthValue) {
        boolean result = false;
        try {
            formatter.parse(dateOfBirthValue);
        } catch (ParseException e) {
            result = true;
        }
        return result;
    }
}
