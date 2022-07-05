package com.wahdanhasan.cxunicorn.assessment.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.handler.RequestHandler;
import com.wahdanhasan.cxunicorn.assessment.api.dto.user.EmployeeDetails;
import com.wahdanhasan.cxunicorn.assessment.api.dto.user.RegisterUserReqDto;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/* Utility functions that are used throughout the program */

public class Utility {

    /* The class for the formatters is thread safe */
    public static final DateTimeFormatter dtfDate =  DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    public static final DateTimeFormatter dtfTime =  DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);

    public static Pageable paginationDtoToPageable(PaginationDto paginationDto){

        /* Get sort direction from dto or use default */
        Sort.Direction direction;

        if (Constants.SORT_ORDER_DESC.equalsIgnoreCase(paginationDto.getSortOrder())){
            direction = Sort.Direction.DESC;
        }
        else {
            direction = Sort.Direction.ASC;
        }

        /* Get sort direction from dto or use default */
        if (paginationDto.getSortOrder() == null){
            paginationDto.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }

        return PageRequest.of(paginationDto.getPageNum(), paginationDto.getPageSize(), direction, paginationDto.getSortBy());
    }

    public static PaginationDto getPaginationDto(RequestHandler<?> request) {

        /* If the pagination dto is null, return default page settings */
        if (request == null){
            return new PaginationDto();
        }

        return request.getPaginationDto();
    }

    public static LocalDate stringDateToLocalDate(String date) throws DateTimeParseException {
        return LocalDate.parse(date, dtfDate);
    }

    public static LocalTime stringTimeToLocalTime(String time) throws DateTimeParseException {
        return LocalTime.parse(time, dtfTime);
    }

    public static Boolean isDateFormatValid(String date){
        /* Return true if valid, false otherwise */
        try {
            stringDateToLocalDate(date);

            return true;
        }
        catch (DateTimeParseException dtpe){
            return false;
        }
    }

    public static Boolean isTimeFormatValid(String time){
        /* Return true if valid, false otherwise */
        try {
            stringTimeToLocalTime(time);

            return true;
        }
        catch (DateTimeParseException dtpe){
            return false;
        }
    }

    public static Boolean isThisDateAfterThatDate(LocalDate dateA, LocalDate dateB){
        return dateA.isAfter(dateB);
    }

    public static boolean isThisTimeAfterThatTime(LocalTime timeStart, LocalTime timeEnd) {
        return timeStart.isAfter(timeEnd);
    }

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static List<LocalTime> stringTimeListToLocalTimeList(String[] strTimeArr) throws ParseException {
        List<LocalTime> localTimeList = new ArrayList<>();

        for(String strTime : strTimeArr){
            localTimeList.add(stringTimeToLocalTime(strTime));
        }

        return localTimeList;
    }

    public static String localTimeToStringTime(LocalTime time) {
        return time.format(dtfTime).toString();
    }

    public static List<String> localTimeListToStringTimeList(List<LocalTime> localTimeList) {
        List<String> strTimeList = new ArrayList<>();

        for(LocalTime localTime : localTimeList){
            strTimeList.add(localTimeToStringTime(localTime));
        }

        return strTimeList;
    }

    public static DecodedJWT getDecodedJWT(HttpServletRequest request){
        /* Used for extracting information from the JWT from an API
        *  This is repeated after authorization due to key information that cannot be extracted otherwise
        * */

        /* Get the authorization header from the JWT */
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        /* Remove the bearer prefix from the authorization header */
        String token = authorizationHeader.substring(Constants.BEARER_PREFIX.length());

        /* Create an algorithm to encode/decode the JWT using the secret key */
        Algorithm algo = Algorithm.HMAC256(Constants.SECRET_KEY);

        /* Create a verifier from the algorithm */
        JWTVerifier verifier = JWT.require(algo).build();

        /* Verify the token and return */
        return verifier.verify(token);
    }

    public static String userFirstAndLastNameToFullName(String firstName, String lastName){
        return firstName + " " + ((lastName != null)?lastName:"");
    }

    public static void validateUserRolesUniqueness(List<String> userRoles) throws RestException{

        /* If a user 2 of any unique roles, raise exception */
        if (userRoles.contains(Constants.ROLE_DOCTOR) && userRoles.contains(Constants.ROLE_PATIENT)
                || userRoles.contains(Constants.ROLE_DOCTOR) && userRoles.contains(Constants.ROLE_CLINIC_ADMIN)
                || userRoles.contains(Constants.ROLE_PATIENT) && userRoles.contains(Constants.ROLE_CLINIC_ADMIN)){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), Constants.MULTIPLE_UNIQUE_ROLES);
        }
    }

    public static void validateEmployeeFields(RegisterUserReqDto registerUserReqDto) throws RestException {
        if (registerUserReqDto.getEmployee() != null){
            EmployeeDetails employeeDetails = registerUserReqDto.getEmployee();

            /* If work times field are null or empty, raise exception */
            if (employeeDetails.getWorkStartTimes() == null || employeeDetails.getWorkStartTimes().length == 0) {
                throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_FIELD, "workStartTimes"));
            }

            if (employeeDetails.getWorkEndTimes() == null || employeeDetails.getWorkEndTimes().length == 0) {
                throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_FIELD, "workEndTimes"));
            }

            /* If work time fields differ in length, raise exception */
            if (employeeDetails.getWorkStartTimes().length != employeeDetails.getWorkEndTimes().length) {
                throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MALFORMED_FIELD, "workStartTimes and workEndTimes"));
            }

            /* If any work time formats are invalid or any work start occurs after work end, raise exception */
            LocalTime timeStart;
            LocalTime timeEnd;
            for (int i = 0 ; i < employeeDetails.getWorkStartTimes().length ; i++) {

                try {
                    timeStart = Utility.stringTimeToLocalTime(employeeDetails.getWorkStartTimes()[i]);
                    timeEnd = Utility.stringTimeToLocalTime(employeeDetails.getWorkEndTimes()[i]);

                    if (Utility.isThisTimeAfterThatTime(timeStart, timeEnd)) {
                        throw new RestException(HttpStatus.BAD_REQUEST.value(),
                                String.format(
                                        Constants.DATE_OCCURS_AFTER_DATE,
                                        "workStartTime",
                                        "workEndTime")
                        );
                    }

                }
                catch (DateTimeParseException dtpe) {
                    throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MALFORMED_FIELD, "workStartTimes/workEndTimes"));
                }
            }
        }
        else {
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_FIELD, "employee"));
        }
    }

    public static void validateUserFields(RegisterUserReqDto registerUserReqDto) throws RestException{

        /* If any required field is missing or contains data of the wrong type/format, raise exception */

        if (StringUtils.isEmpty(registerUserReqDto.getEmail()) || !EmailValidator.getInstance().isValid(registerUserReqDto.getEmail())){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_OR_MALFORMED_FIELD, "email"));
        }

        if (StringUtils.isEmpty(registerUserReqDto.getPassword())){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_FIELD, "password"));
        }

        if (StringUtils.isEmpty(registerUserReqDto.getFirstName()) || !StringUtils.isAlpha(registerUserReqDto.getFirstName())){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_OR_MALFORMED_FIELD, "firstName"));
        }

        if (!StringUtils.isEmpty(registerUserReqDto.getLastName()) && !StringUtils.isAlpha(registerUserReqDto.getLastName())){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_OR_MALFORMED_FIELD, "lastName"));
        }

        if (StringUtils.isEmpty(registerUserReqDto.getDateOfBirth()) || !Utility.isDateFormatValid(registerUserReqDto.getDateOfBirth())){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_OR_MALFORMED_FIELD, "dateOfBirth"));
        }

        if (!StringUtils.isEmpty(registerUserReqDto.getGender())) {
            if (registerUserReqDto.getGender().length() > 1){
                throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MALFORMED_FIELD, "gender"));
            }

            /* Set gender to uppercase */
            registerUserReqDto.setGender(registerUserReqDto.getGender().toUpperCase());

            /* Get gender */
            Character gender = registerUserReqDto.getGender().charAt(0);

            if (gender != 'M' && gender != 'F'){
                throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MALFORMED_FIELD, "gender"));
            }

            registerUserReqDto.setGenderChar(gender);
        }

        if (registerUserReqDto.getPhoneCountryCode() == null || registerUserReqDto.getPhoneNumber() == null){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_FIELD, "phoneCountryCode/phoneNumber"));
        }
    }

}
