package com.wahdanhasan.cxunicorn.assessment.util;

/* Constants that are used throughout the program */

public class Constants {

    /* Generic response messages */
    public static final String NO_CONTENT_DESC = "Record not found";
    public static final String EMPTY_PAYLOAD = "The provided payload is empty";
    public static final String OK_DESC = "Success";
    public static final String FORBIDDEN_DESC = "Access forbidden";
    public static final String BAD_REQUEST_DESC = "The request could not be understood due to malformed syntax in the payload";
    public static final String MISSING_FIELD = "The field '%s' is missing";
    public static final String MALFORMED_FIELD = "The field '%s' is malformed";
    public static final String MISSING_OR_MALFORMED_FIELD = "The field '%s' is missing/malformed";
    public static final String FIELD_WITH_VALUE_NOT_FOUND = "The field '%s' with value '%s' was not found";
    public static final String FIELD_OUTSIDE_RANGE = "The field '%s' is outside of the range '%s'";
    public static final String MISSING_PAGINATION = "The required pagination information is missing";
    public static final String MULTIPLE_UNIQUE_ROLES = "Multiple unique roles provided";
    public static final String DATE_OCCURS_AFTER_DATE = "Date of '%s' occurs after the date of '%s'";
    public static final String TIME_OCCURS_TIME_DATE = "Time of '%s' occurs after the time of '%s'";


    /* Pagination values */
    public static final String SORT_ORDER_DESC = "desc";
    public static final String SORT_ORDER_ASC = "asc";
    public static final String DEFAULT_SORT_BY = "id";
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final String DEFAULT_SORT_ORDER = SORT_ORDER_ASC;

    /* Custom response messages  */
    public static final String DOCTOR_FULL_SCHEDULE = "The requested doctor is unavailable for the date '%s' due to a full schedule";
    public static final String DOCTOR_UNAVAILABLE_DURING_HOURS = "The doctor is unavailable during the requested hours";
    public static final String OUTSIDE_DOCTOR_SCHEDULE = "The appointment request is outside of the doctor's scheduled hours";
    public static final String PATIENT_OVERLAPPING_APPOINTMENTS ="The patient has an overlapping appointment for the requested time period";
    public static final String APPOINTMENT_ALREADY_CANCELLED = "The requested appointment has already been cancelled";

    public static final String JWT_EXPIRED = "The JWT has expired.";

    /* Custom values / shortcuts */
    public static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
    public static final Long MINUTES_IN_HOUR = 60L;
    public static final String APPOINTMENT_STATUS_CANCELLED = "cancelled";
    public static final String APPOINTMENT_STATUS_VALID = "valid";
    public static final boolean PATIENT_ATTENDED = true;
    public static final String CLAIM_ROLES = "roles";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String SECRET_KEY = "F652F1A61182446744503C3D5F955841667EE21E0ED32A867BD17AC83AECB8A8";
    public static final String BEARER_PREFIX = "Bearer ";


    /* Role name constants */
    public static final String ROLE_DOCTOR = "doctor";
    public static final String ROLE_PATIENT = "patient";
    public static final String ROLE_CLINIC_ADMIN = "clinic admin";


}
