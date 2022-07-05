package com.wahdanhasan.cxunicorn.assessment.api.service.impl;

import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BasicAppointmentDataDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BookAppointmentReqDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.GetAllPatientAppointmentsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.GetAppointmentByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import com.wahdanhasan.cxunicorn.assessment.api.service.AppointmentService;
import com.wahdanhasan.cxunicorn.assessment.db.entity.AppointmentEntity;
import com.wahdanhasan.cxunicorn.assessment.db.entity.DoctorEntity;
import com.wahdanhasan.cxunicorn.assessment.db.entity.PatientEntity;
import com.wahdanhasan.cxunicorn.assessment.db.repo.AppointmentRepo;
import com.wahdanhasan.cxunicorn.assessment.db.repo.DoctorRepo;
import com.wahdanhasan.cxunicorn.assessment.db.repo.PatientRepo;
import com.wahdanhasan.cxunicorn.assessment.db.repo.RoleRepo;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;
import com.wahdanhasan.cxunicorn.assessment.util.Utility;
import com.wahdanhasan.cxunicorn.assessment.util.mapper.AppointmentMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static java.time.temporal.ChronoUnit.MINUTES;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Value("${appointment.max.lookahead.days}")
    private Integer appointmentLookaheadDays;

    @Value("${appointment.min.duration.mins}")
    private Integer appointmentMinMinutes;

    @Value("${appointment.max.duration.mins}")
    private Integer appointmentMaxMinutes;

    @Value("${appointment.max.count}")
    private Integer appointmentMaxCount;

    @Value("${appointment.max.total.mins}")
    private Integer appointMaxTotalMins;

    @Override
    public void bookAppointment(BookAppointmentReqDto bookAppointmentReqDto) throws RestException {

        /* Ensure that the doctor and patient ids have been provided */
        if (bookAppointmentReqDto.getDoctorId() == null){
            throw new RestException(HttpStatus.NO_CONTENT.value(), String.format(Constants.MISSING_FIELD, "doctorId"));
        }
        else if (bookAppointmentReqDto.getPatientId() == null){
            throw new RestException(HttpStatus.NO_CONTENT.value(), String.format(Constants.MISSING_FIELD, "patientId"));
        }


        /* Ensure that the doctor and patient provided exist */
        /* Validate provided doctor */
        Optional<DoctorEntity> doctorEntityOptional = doctorRepo.findById(bookAppointmentReqDto.getDoctorId());

        if (!doctorEntityOptional.isPresent()){
            throw new RestException(HttpStatus.NO_CONTENT.value(),
                    String.format(
                            Constants.FIELD_WITH_VALUE_NOT_FOUND,
                            "doctorId",
                            bookAppointmentReqDto.getDoctorId().toString()));
        }

        DoctorEntity doctorEntity = doctorEntityOptional.get();

        /* Validate provided patient */
        Optional<PatientEntity> patientEntityOptional = patientRepo.findById(bookAppointmentReqDto.getPatientId());

        if (!patientEntityOptional.isPresent()){
            throw new RestException(HttpStatus.NO_CONTENT.value(),
                    String.format(
                            Constants.FIELD_WITH_VALUE_NOT_FOUND,
                            "patientId",
                            bookAppointmentReqDto.getPatientId().toString()));
        }


        /* Ensure that the appointment date and time provided are valid */
        /* Validate date format and ensure it does not exceed the max duration*/
        LocalDate appointmentDate;
        try {
            appointmentDate = Utility.stringDateToLocalDate(bookAppointmentReqDto.getAppointmentDate());
            LocalDate maxAppointmentDate = Utility.dateToLocalDate(new Date(System.currentTimeMillis() + (Constants.DAY_IN_MS * appointmentLookaheadDays)));
            LocalDate yesterdayDate = Utility.dateToLocalDate(new Date(System.currentTimeMillis() - (Constants.DAY_IN_MS)));

            if (Utility.isThisDateAfterThatDate(appointmentDate, maxAppointmentDate) || !appointmentDate.isAfter(yesterdayDate)){
                throw new RestException(
                        HttpStatus.BAD_REQUEST.value(),
                        String.format(
                                Constants.FIELD_OUTSIDE_RANGE,
                                "appointmentDate",
                                "up to " + appointmentLookaheadDays.toString() + " days ahead from current date"));
            }

        } catch (DateTimeParseException dtpe) {
            throw new RestException(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(Constants.MALFORMED_FIELD, "appointmentDate")
            );
        }

        /* Validate time format */
        LocalTime appointmentTimeStart;
        LocalTime appointmentTimeEnd;
        try {
            appointmentTimeStart = Utility.stringTimeToLocalTime(bookAppointmentReqDto.getAppointmentTimeStart());
            appointmentTimeEnd = Utility.stringTimeToLocalTime(bookAppointmentReqDto.getAppointmentTimeEnd());

            /* DISCLAIMER: The validation to ensure the appointment start time hasn't already passed was left out on
            *  purpose as testing/using the application would be quite annoying to do.
            *
            *   */

            if (Utility.isThisTimeAfterThatTime(appointmentTimeStart, appointmentTimeEnd)){
                throw new RestException(
                        HttpStatus.BAD_REQUEST.value(),
                        String.format(Constants.TIME_OCCURS_TIME_DATE, appointmentTimeStart.toString(), appointmentTimeEnd.toString())
                );
            }
        }
        catch (DateTimeParseException dtpe){
            throw new RestException(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(Constants.MALFORMED_FIELD, "appointmentTimeStart/appointmentTimeEnd")
            );
        }

        /* Ensure the appointment duration is within limits */
        Integer appointmentDurationMins = (int) MINUTES.between(appointmentTimeStart, appointmentTimeEnd);
        if (appointmentDurationMins > appointmentMaxMinutes
                || appointmentDurationMins < appointmentMinMinutes){

            throw new RestException(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(
                            Constants.FIELD_OUTSIDE_RANGE,
                            "appointmentDurationMins",
                            appointmentMinMinutes.toString() + " to " + appointmentMaxMinutes.toString() + " mins")
            );
        }


        /* Ensure the appointment time is within the doctor's scheduled hours */
        Boolean appointmentBetweenSchedule = false;
        LocalTime tempScheduleTimeStart;
        LocalTime tempScheduleTimeEnd;
        for(int i = 0 ; i < doctorEntity.getWorkStartTimes().length ; i++){
            tempScheduleTimeStart = LocalTime.parse(doctorEntity.getWorkStartTimes()[i]);
            tempScheduleTimeEnd = LocalTime.parse(doctorEntity.getWorkEndTimes()[i]);

            /* If the appointment start and end time are outside the current schedule, check the next doctor schedule */
            /* This is based on the understanding that some doctors sit a certain amount of hours at a hospital/clinic
            *  and then leave. They may have multiple scheduled hours at the hospital/clinic. */
            if (appointmentTimeStart.isBefore(tempScheduleTimeStart)
                    || appointmentTimeStart.isAfter(tempScheduleTimeEnd)) {
                continue;
            }

            /* If the appointment was inside one of the doctor's sitting hours, break */
            appointmentBetweenSchedule = true;
            break;
        }

        if (!appointmentBetweenSchedule){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), Constants.OUTSIDE_DOCTOR_SCHEDULE);
        }


        /* Ensure the doctor has not exceeded his total appointment count or hours */
        List<AppointmentEntity> doctorsAppointments = appointmentRepo
                .getAppointmentsForDayByDoctorId(appointmentDate,
                                                    doctorEntity.getId(),
                                                    Constants.APPOINTMENT_STATUS_VALID);

        /* Check appointment count */
        if (doctorsAppointments.size() > appointmentMaxCount){
            throw new RestException(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    String.format(
                            Constants.DOCTOR_FULL_SCHEDULE,
                            bookAppointmentReqDto.getAppointmentDate())
            );
        }

        /* Check appointment total hours */
        Integer totalAppointmentDurationMins = 0;
        for(AppointmentEntity doctorAppointment : doctorsAppointments){
            totalAppointmentDurationMins += doctorAppointment.getDurationMins();
        }

        if (totalAppointmentDurationMins > appointMaxTotalMins){
            throw new RestException(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    String.format(
                            Constants.DOCTOR_FULL_SCHEDULE,
                            bookAppointmentReqDto.getAppointmentDate())
            );
        }


        /* Ensure the requested appointment time period does not overlap with another appointment */
        LocalTime tempAppointmentTimeStart;
        LocalTime tempAppointmentTimeEnd;
        for(AppointmentEntity doctorAppointment : doctorsAppointments){
            /* Parse string times to local times. Add/Subtract 1 minute to make ensure it does not overlap */
            tempAppointmentTimeStart = doctorAppointment.getTimeStart();
            tempAppointmentTimeEnd = doctorAppointment.getTimeEnd();

            /* If either the appointment start or end time are in between the already booked appointment start and end time.
            *  Or if the appointment start or end time are 1:1 with the already booked appointment start and end time,
            *  raise exception.
            * */
            if (((appointmentTimeStart.isAfter(tempAppointmentTimeStart) && appointmentTimeStart.isBefore(tempAppointmentTimeEnd))
                || (appointmentTimeEnd.isAfter(tempAppointmentTimeStart) && appointmentTimeEnd.isBefore(tempAppointmentTimeEnd)))
            || (appointmentTimeStart.equals(tempAppointmentTimeStart) || appointmentTimeEnd.equals(tempAppointmentTimeEnd))) {
                throw new RestException(HttpStatus.BAD_REQUEST.value(), Constants.DOCTOR_UNAVAILABLE_DURING_HOURS);
            }
        }


        /* Ensure the patient does not have overlapping appointments */
        List<AppointmentEntity> patientAppointments = appointmentRepo.
                getAppointmentsForDayByPatientId(appointmentDate,
                                                    bookAppointmentReqDto.getPatientId(),
                                                    Constants.APPOINTMENT_STATUS_VALID);

        for(AppointmentEntity patientAppointment : patientAppointments){
            /* Parse string times to local times. Add/Subtract 1 minute to make ensure it does not overlap */
            tempAppointmentTimeStart = patientAppointment.getTimeStart().minusMinutes(1);
            tempAppointmentTimeEnd = patientAppointment.getTimeEnd().plusMinutes(1);

            /* Check if the requested time is between another appointment's scheduled time */
            if (appointmentTimeStart.isAfter(tempAppointmentTimeStart) && appointmentTimeStart.isBefore(tempAppointmentTimeEnd)
                    || appointmentTimeEnd.isAfter(tempAppointmentTimeStart) && appointmentTimeEnd.isBefore(tempAppointmentTimeEnd)) {
                throw new RestException(HttpStatus.BAD_REQUEST.value(), Constants.PATIENT_OVERLAPPING_APPOINTMENTS);
            }
        }


        /* Map the request DTO to an appointment entity */
        AppointmentEntity appointmentEntity = appointmentMapper.bookAppointmentReqDtoToAppointmentEntity(bookAppointmentReqDto);
        appointmentEntity.setDurationMins(appointmentDurationMins);


        /* Save the entity to DB */
        appointmentRepo.save(appointmentEntity);
    }

    @Override
    public void cancelAppointment(Integer appointmentId) throws RestException {

        /* Ensure the appointment to cancel exists */
        Optional<AppointmentEntity> appointmentEntityOptional = appointmentRepo.findById(appointmentId);

        if (!appointmentEntityOptional.isPresent()){
            throw new RestException(HttpStatus.NO_CONTENT.value(),
                    String.format(
                            Constants.FIELD_WITH_VALUE_NOT_FOUND,
                            "appointmentId",
                            appointmentId.toString()));
        }

        AppointmentEntity appointmentEntity = appointmentEntityOptional.get();

        /* Ensure that the appointment hasn't already been cancelled */
        if (appointmentEntity.getAppointmentStatus().equals(Constants.APPOINTMENT_STATUS_CANCELLED)){
            throw new RestException(HttpStatus.NO_CONTENT.value(), Constants.APPOINTMENT_ALREADY_CANCELLED);
        }

        /* Set the appointment to cancelled */
        appointmentEntity.setAppointmentStatus(Constants.APPOINTMENT_STATUS_CANCELLED);

        /* Update appointment in DB */
        appointmentRepo.save(appointmentEntity);
    }

    @Override
    public GetAppointmentByIdRespDto getAppointmentById(Integer appointmentId, String userEmail) throws RestException {

        /* Ensure the appointment to cancel exists */
        Optional<AppointmentEntity> appointmentEntityOptional = appointmentRepo.findById(appointmentId);

        if (appointmentEntityOptional.isEmpty()){
            throw new RestException(HttpStatus.NO_CONTENT.value(),
                    String.format(
                            Constants.FIELD_WITH_VALUE_NOT_FOUND,
                            "appointmentId",
                            appointmentId.toString()));
        }

        AppointmentEntity appointmentEntity = appointmentEntityOptional.get();

        /* Verify if the user that sent the request is a patient
        *  If he is, then he can only access this resource if he is associated with the appointment
        *  */
        List<String> userRolesStrList = roleRepo.getUserRolesFromEmail(userEmail);

        if (userRolesStrList.contains(Constants.ROLE_PATIENT)){
            if (!appointmentEntity.getPatient().getEmail().equals(userEmail)){
                throw new RestException(HttpStatus.FORBIDDEN.value(), Constants.FORBIDDEN_DESC);
            }
        }

        /* Map the entity to response DTO */
        GetAppointmentByIdRespDto getAppointmentByIdRespDto =
                appointmentMapper.appointmentEntityToGetAppointmentByIdRespDto(appointmentEntity);

        /* Get doctor and patient entities */
        /* A check to see if they exist is not required as the book appointment api validates their existence.
        *  The design of the system also does not delete users, but rather, removes personal information should
        *  they request to do so. It still retains their basic information such as their name.
        *  */
        PatientEntity patientEntity = patientRepo.findById(appointmentEntity.getPatient().getId()).get();
        DoctorEntity doctorEntity = doctorRepo.findById(appointmentEntity.getDoctor().getId()).get();

        /* Set their full names as first name + last name(if exists) */
        getAppointmentByIdRespDto
                .setPatientFullName(Utility.userFirstAndLastNameToFullName(patientEntity.getFirstName(),
                                                                           patientEntity.getLastName()));

        getAppointmentByIdRespDto
                .setDoctorFullName(Utility.userFirstAndLastNameToFullName(doctorEntity.getFirstName(),
                                                                          doctorEntity.getLastName()));

        /* Return response dto */
        return getAppointmentByIdRespDto;
    }

    @Override
    public GetAllPatientAppointmentsRespDto getAllPatientAppointments(Integer patientId, PaginationDto paginationDto, String userEmail) throws RestException {

        /* Validate provided patient */
        Optional<PatientEntity> patientEntityOptional = patientRepo.findById(patientId);

        if (patientEntityOptional.isEmpty()){

            throw new RestException(HttpStatus.NO_CONTENT.value(),
                    String.format(
                            Constants.FIELD_WITH_VALUE_NOT_FOUND,
                            "patientId",
                            patientId.toString()));
        }

        PatientEntity patientEntity = patientEntityOptional.get();

        /* Ensure that the patient that is requesting the patient's appointment info is the same patient that the appointments belong to */
        if (!userEmail.equals(patientEntity.getEmail())) {
            throw new RestException(HttpStatus.FORBIDDEN.value(), Constants.FORBIDDEN_DESC);
        }

        /* Create pageable object from paginationDto */
        paginationDto.setSortBy(Constants.DEFAULT_SORT_BY);
        Pageable pageable = Utility.paginationDtoToPageable(paginationDto);

        /* Get paginated appointments from DB */
        Page<AppointmentEntity> patientAppointmentsPage = appointmentRepo.getPaginatedAppointmentsByPatientId(patientId, pageable);

        /* Set pagination values based on results */
        paginationDto.setTotalPages(patientAppointmentsPage.getTotalPages());
        paginationDto.setTotalElements(patientAppointmentsPage.getTotalElements());

        /* Map data to response DTO */
        GetAllPatientAppointmentsRespDto getAllPatientAppointmentsRespDto = new GetAllPatientAppointmentsRespDto();

        getAllPatientAppointmentsRespDto.setPatientId(patientId);

        getAllPatientAppointmentsRespDto.setPatientFullName(Utility.userFirstAndLastNameToFullName(patientEntity.getFirstName(),
                                                                                                   patientEntity.getLastName()));


        /* Map doctor information to response DTO */
        /* Create a list of unique doctor ids from the appointments */
        Set<Integer> doctorIdsFromAppointmentsSet = new HashSet<>();
        for(AppointmentEntity tempAppointment : patientAppointmentsPage){
            doctorIdsFromAppointmentsSet.add(tempAppointment.getDoctor().getId());
        }

        /* Convert the set to a list for querying */
        List<Integer> doctorIdsFromAppointmentsList = new ArrayList<>(doctorIdsFromAppointmentsSet);

        /* Get a list of doctors based on the unique id list */
        List<DoctorEntity> doctorEntitiesList = doctorRepo.getDoctorsById(doctorIdsFromAppointmentsList);

        /* Create a set for easy lookup of doctor id -> doctor full name */
        Map<Integer, String> doctorIdToFullNameMap = new HashMap<>();
        String tempFullName;
        for(DoctorEntity doctorEntity : doctorEntitiesList){
            tempFullName = Utility.userFirstAndLastNameToFullName(doctorEntity.getFirstName(), doctorEntity.getLastName());

            doctorIdToFullNameMap.put(doctorEntity.getId(), tempFullName);
        }

        /* Map the patient, doctor, and appointment data to the appointments list */
        List<BasicAppointmentDataDto> appointmentsList = new ArrayList<>();
        for(AppointmentEntity appointment : patientAppointmentsPage){
            BasicAppointmentDataDto tempBasicAppointmentDataDto = appointmentMapper.appointmentEntityToBasicAppointmentDataDto(appointment);

            tempBasicAppointmentDataDto.setDoctorFullName(doctorIdToFullNameMap.get(appointment.getDoctor().getId()));

            appointmentsList.add(tempBasicAppointmentDataDto);
        }

        getAllPatientAppointmentsRespDto.setAppointments(appointmentsList);

        return getAllPatientAppointmentsRespDto;
    }
}
