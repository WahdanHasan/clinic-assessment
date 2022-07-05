package com.wahdanhasan.cxunicorn.assessment.api.service.impl;

import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BasicAppointmentDataDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetAllDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetBusyDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetDoctorAvailableSlotRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetDoctorByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.service.DoctorService;
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
import com.wahdanhasan.cxunicorn.assessment.util.comparator.BusyDoctorsAptCountComparator;
import com.wahdanhasan.cxunicorn.assessment.util.comparator.BusyDoctorsAptTimeComparator;
import com.wahdanhasan.cxunicorn.assessment.util.mapper.DoctorMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    AppointmentRepo appointmentRepo;

    @Autowired
    DoctorRepo doctorRepo;

    @Autowired
    PatientRepo patientRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    DoctorMapper doctorMapper;

    @Value("${appointment.min.duration.mins}")
    Long appointmentMinDuration;

    @Value("${appointment.max.count}")
    private Integer appointmentMaxCount;

    @Value("${appointment.max.total.mins}")
    private Integer appointMaxDuration;

    @Override
    public List<GetAllDoctorsRespDto> getAllDoctors() throws RestException {

        /* Get all doctors from DB */
        List<DoctorEntity> doctorEntityList = doctorRepo.findAll();

        /* Exception if no doctors are found */
        if (doctorEntityList.isEmpty()){
            throw new RestException(HttpStatus.NO_CONTENT.value(), Constants.NO_CONTENT_DESC);
        }

        /* Map entity to response DTO */
        List<GetAllDoctorsRespDto> getAllDoctorsRespDtoList = new ArrayList<>();

        doctorEntityList.forEach(
                (doctorEntity)->{
                    getAllDoctorsRespDtoList.add(doctorMapper.doctorEntityToGetAllDoctorsRespDto(doctorEntity));
                });

        return getAllDoctorsRespDtoList;
    }

    @Override
    public GetDoctorByIdRespDto getDoctorById(Integer doctorId) throws RestException {

        /* Check if doctor exists */
        Optional<DoctorEntity> doctorEntityOptional = doctorRepo.findById(doctorId);

        if(doctorEntityOptional.isEmpty()){
            throw new RestException(HttpStatus.NO_CONTENT.value(), Constants.NO_CONTENT_DESC);
        }

        /* Map doctor entity to DTO */
        DoctorEntity doctorEntity = doctorEntityOptional.get();

        GetDoctorByIdRespDto getDoctorByIdRespDto = doctorMapper.doctorEntityToGetDoctorByIdRespDto(doctorEntity);

        /* Map work schedule from entity to DTO */
        List<String> workSchedule = new ArrayList<>();
        getDoctorByIdRespDto.setWorkSchedule(workSchedule);

        for(int i = 0 ; i < doctorEntity.getWorkStartTimes().length ; i++){
            workSchedule.add(doctorEntity.getWorkStartTimes()[i] + "-" + doctorEntity.getWorkEndTimes()[i]);
        }

        return getDoctorByIdRespDto;
    }

    @Override
    public List<GetBusyDoctorsRespDto> getBusyDoctorsByAppointmentCount(String date) throws RestException {

        /* Ensure the provided date is in a valid format */
        LocalDate appointmentDate = null;
        try {
            appointmentDate = Utility.stringDateToLocalDate(date);
        }
        catch(DateTimeParseException dtpe){
            throw new RestException(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(Constants.MALFORMED_FIELD, "appointmentDate")
            );
        }

        /* Get all doctors */
        List<GetBusyDoctorsRespDto> getBusyDoctorsByAptRespDtoList = doctorRepo.getAllDoctorsByAppointmentCount(appointmentDate);

        /* Sort list of doctors by total appointment count */
        getBusyDoctorsByAptRespDtoList.sort(new BusyDoctorsAptCountComparator());

        return getBusyDoctorsByAptRespDtoList;
    }

    @Override
    public List<GetBusyDoctorsRespDto> getBusyDoctorsByAppointmentHours(Integer minimumHours, String date) throws RestException {

        /* Ensure the provided date is in a valid format */
        LocalDate appointmentDate = null;
        try {
            appointmentDate = Utility.stringDateToLocalDate(date);
        }
        catch(DateTimeParseException dtpe){
            throw new RestException(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(Constants.MALFORMED_FIELD, "appointmentDate")
            );
        }

        /* Query DB */
        List<GetBusyDoctorsRespDto> busyDoctorsList = doctorRepo.getAllDoctorsByAppointmentHours(appointmentDate);

        /* Filter by hours */
        Long minimumMinutes = minimumHours * Constants.MINUTES_IN_HOUR;

        /* Filter doctors based on minimum minutes */
        List<GetBusyDoctorsRespDto> getBusyDoctorsByAptRespDtoList = new ArrayList<>();

        String tempHoursMins;
        for(GetBusyDoctorsRespDto busyDoctor : busyDoctorsList){
            if (busyDoctor.getTotalAppointmentTimeLong() >= minimumMinutes){

                tempHoursMins = busyDoctor.getTotalAppointmentTimeLong()/Constants.MINUTES_IN_HOUR + " hours ";
                tempHoursMins += busyDoctor.getTotalAppointmentTimeLong()%Constants.MINUTES_IN_HOUR + " minutes ";

                busyDoctor.setTotalAppointmentTime(tempHoursMins);

                getBusyDoctorsByAptRespDtoList.add(busyDoctor);
            }
        }

        /* Sort list of doctors by total appointment hours */
        getBusyDoctorsByAptRespDtoList.sort(new BusyDoctorsAptTimeComparator());

        return getBusyDoctorsByAptRespDtoList;
    }

    @Override
    public GetDoctorAvailableSlotRespDto getAvailableSlotsByDoctorId(Integer doctorId, String date, String userEmail) throws RestException {

        /* Check if doctor exists */
        Optional<DoctorEntity> doctorEntityOptional = doctorRepo.findById(doctorId);

        if(doctorEntityOptional.isEmpty()){
            throw new RestException(HttpStatus.NO_CONTENT.value(), Constants.NO_CONTENT_DESC);
        }

        /* Ensure the provided date is in a valid format */
        if (!Utility.isDateFormatValid(date)){
            throw new RestException(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(Constants.MALFORMED_FIELD, "appointmentDate")
            );
        }

        /* Get entity from optional */
        DoctorEntity doctorEntity = doctorEntityOptional.get();

        /* Get user roles */
        List<String> userRoles = roleRepo.getUserRolesFromEmail(userEmail);

        /* Validate the provided appointment date and then convert it to local date */
        LocalDate appointmentDate = null;

        try {
            appointmentDate = Utility.stringDateToLocalDate(date);
        } catch (DateTimeParseException dtpe) {
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MALFORMED_FIELD, "date"));
        }

        /* Get all appointments on the provided date and doctor id */
        List<AppointmentEntity> appointmentEntityList = appointmentRepo
                .getAppointmentsForDayByDoctorId(appointmentDate,
                                                    doctorId,
                                                    Constants.APPOINTMENT_STATUS_VALID);

        /* Create a reference list to all appointment start and end times */
        List<LocalTime> appointmentStartTimesList = getAppointmentStartTimesFromEntityList(appointmentEntityList, null);
        List<LocalTime> appointmentEndTimesList = getAppointmentEndTimesFromEntityList(appointmentEntityList, null);

        /* If user has patient role, limit access by not adding certain information */
        List<Integer> patientIdList = null;
        Map<Integer, String> patientIdToFullNameMap = null;
        if (!userRoles.contains(Constants.ROLE_PATIENT)) {
            /* Get patient ids */
            patientIdList = getPatientIdsFromEntityList(appointmentEntityList, null);

            /* Get all patients from patient id list */
            List<PatientEntity> patientEntityList = patientRepo.getAllByIdList(patientIdList);

            /* Create map for patient id to patient full name */
            patientIdToFullNameMap = new HashMap<>();

            for(PatientEntity patient : patientEntityList){
                patientIdToFullNameMap.put(patient.getId(),
                        Utility.userFirstAndLastNameToFullName(patient.getFirstName(), patient.getLastName()));
            }
        }


        /* Get all occupied slots */
        List<BasicAppointmentDataDto> occupiedSlots = getDoctorOccupiedSlots(appointmentStartTimesList, appointmentEndTimesList, patientIdList);


        /* Add patient info to occupied slots */
        if (patientIdToFullNameMap != null) {
            for (BasicAppointmentDataDto occupiedSlot : occupiedSlots) {
                occupiedSlot.setPatientFullName(patientIdToFullNameMap.get(occupiedSlot.getPatientLookupId()));
            }
        }

        /* Get all available slots */
        List<BasicAppointmentDataDto> availableSlots = getDoctorAvailableSlots(doctorEntity.getWorkStartTimes(),
                                                                               doctorEntity.getWorkEndTimes(),
                                                                               appointmentStartTimesList,
                                                                               appointmentEndTimesList);

        /* Set values for response DTO */
        GetDoctorAvailableSlotRespDto getDoctorAvailableSlotRespDto = new GetDoctorAvailableSlotRespDto();
        getDoctorAvailableSlotRespDto.setAvailableSlots(availableSlots);
        getDoctorAvailableSlotRespDto.setOccupiedSlots(occupiedSlots);

        return getDoctorAvailableSlotRespDto;
    }

    @Override
    public List<GetDoctorAvailableSlotRespDto> getAllDoctorAvailableSlots(String date, String userEmail) throws RestException {

        /* Ensure the provided date is in a valid format */
        if (!Utility.isDateFormatValid(date)){
            throw new RestException(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(Constants.MALFORMED_FIELD, "date")
            );
        }

        /* Get all appointments for the date */
        List<AppointmentEntity> appointmentsList = appointmentRepo
                .getAppointmentsByDateWithStatus(Utility.stringDateToLocalDate(date), Constants.APPOINTMENT_STATUS_VALID);

        /* Get all doctor entities */
        List<DoctorEntity> doctorEntityList = doctorRepo.findAll();

        /* Create a list of all doctor ids */
        List<Integer> doctorIds = new ArrayList<>();

        doctorEntityList.forEach((doctor)-> {
            doctorIds.add(doctor.getId());
        });

        /* Create map of doctor id to doctor full name */
        Map<Integer, String> doctorIdToFullNameMap = new HashMap<>();
        String tempFullName;
        for(DoctorEntity doctorEntity : doctorEntityList){
            tempFullName = doctorEntity.getFirstName() + " " +
                    ((doctorEntity.getLastName() != null)?doctorEntity.getLastName():"");

            doctorIdToFullNameMap.put(doctorEntity.getId(), tempFullName);
        }

        /* Get user roles */
        List<String> userRoles = roleRepo.getUserRolesFromEmail(userEmail);

        /* If the user has the role of patient, then return a list of available doctors */
        List<GetDoctorAvailableSlotRespDto> getDoctorAvailableSlotRespDtoList = new ArrayList<>();
        GetDoctorAvailableSlotRespDto getDoctorAvailableSlotRespDto;

        if (userRoles.contains(Constants.ROLE_PATIENT)) {

            /* Create a map of the total appointment count and hours per doctor id */
            Map<Integer, Integer> doctorIdToAppointmentCountMap = new HashMap<>();
            Map<Integer, Double> doctorIdToAppointmentHoursMap = new HashMap<>();

            Integer tempDoctorId;
            Integer tempAppointmentCount;
            Double tempAppointmentHours;
            for(AppointmentEntity appointment : appointmentsList) {
                tempDoctorId = appointment.getDoctor().getId();

                tempAppointmentCount = doctorIdToAppointmentCountMap.get(tempDoctorId);

                /* If key exists, add 1 to its value, else, create the key and set the value to 0 */
                doctorIdToAppointmentCountMap.put(tempDoctorId,
                                              (tempAppointmentCount == null)?0:tempAppointmentCount+1);

                tempAppointmentHours = doctorIdToAppointmentHoursMap.get(tempDoctorId);

                /* If key exists, add the appointment hours to its value, else, cre*ate the key and set the value to 0 */
                doctorIdToAppointmentHoursMap.put(tempDoctorId,
                        (tempAppointmentHours == null)?0.00
                                :tempAppointmentHours + (appointment.getDurationMins()/(double)Constants.MINUTES_IN_HOUR)
                );
            }

            /* If the doctor's total appointment count is below the threshold and if the doctor's total appointment hours
            *  is less than the threshold with enough time for a minimum duration appointment, then add the doctor's name
            *  to the list of available doctors.
            *  */
            for(Integer doctorId : doctorIds){
                getDoctorAvailableSlotRespDto = new GetDoctorAvailableSlotRespDto();
                getDoctorAvailableSlotRespDtoList.add(getDoctorAvailableSlotRespDto);

                /* If the doctor has no scheduled appointments for the day, add his name */
                if(doctorIdToAppointmentCountMap.get(doctorId) == null || doctorIdToAppointmentHoursMap.get(doctorId) == null){
                    getDoctorAvailableSlotRespDto.setDoctorFullName(doctorIdToFullNameMap.get(doctorId));
                    continue;
                }

                /* If the doctor has schedule appointments, and they're below the limits, add his name */
                if (doctorIdToAppointmentCountMap.get(doctorId) < appointmentMaxCount
                && doctorIdToAppointmentHoursMap.get(doctorId) < (appointMaxDuration - appointmentMinDuration)){

                    getDoctorAvailableSlotRespDto.setDoctorFullName(doctorIdToFullNameMap.get(doctorId));

                }
            }

            return getDoctorAvailableSlotRespDtoList;
        }


        /* If the user is not a patient, proceed with getting more detailed information */

        /* Get a list of patients ids from the appointment list */
        Set<Integer> patientIdSet = new HashSet<>();
        Map<Integer, String> patientIdToFullNameMap = new HashMap<>();

        appointmentsList.forEach((appointment) -> {
            patientIdSet.add(appointment.getPatient().getId());
        });

        List<Integer> patientIdsForLookup = new ArrayList<>(patientIdSet);

        /* Get the patient entities that correspond to the patient ids */
        List<PatientEntity> patientEntityList = patientRepo.getAllByIdList(patientIdsForLookup);

        /* Create map for patient id to patient full name */
        for (PatientEntity patient : patientEntityList) {
            patientIdToFullNameMap.put(patient.getId(),
                    Utility.userFirstAndLastNameToFullName(patient.getFirstName(), patient.getLastName()));
        }


        /* Get occupied and available slots for all doctors and map them to the response DTO */
        List<LocalTime> appointmentStartTimesList;
        List<LocalTime> appointmentEndTimesList;
        List<Integer> patientIdList = null;
        List<BasicAppointmentDataDto> availableSlots;
        List<BasicAppointmentDataDto> occupiedSlots;

        for(DoctorEntity doctor : doctorEntityList){
            /* Set already available fields */
            getDoctorAvailableSlotRespDto = new GetDoctorAvailableSlotRespDto();
            getDoctorAvailableSlotRespDto.setDoctorFullName(doctorIdToFullNameMap.get(doctor.getId()));
            getDoctorAvailableSlotRespDto.setDoctorId(doctor.getId());
            getDoctorAvailableSlotRespDtoList.add(getDoctorAvailableSlotRespDto);


            /* Create a reference list to all appointment start and end times */
            appointmentStartTimesList = getAppointmentStartTimesFromEntityList(appointmentsList, doctor.getId());
            appointmentEndTimesList = getAppointmentEndTimesFromEntityList(appointmentsList, doctor.getId());
            patientIdList = getPatientIdsFromEntityList(appointmentsList, doctor.getId());


            /* Get doctor occupied and available slots */
            occupiedSlots = getDoctorOccupiedSlots(appointmentStartTimesList,
                    appointmentEndTimesList,
                    patientIdList);

            availableSlots = getDoctorAvailableSlots(doctor.getWorkStartTimes(),
                    doctor.getWorkEndTimes(),
                    appointmentStartTimesList,
                    appointmentEndTimesList);

            /* Set patient full names for occupied slots */
            for (BasicAppointmentDataDto occupiedSlot : occupiedSlots) {
                occupiedSlot.setPatientFullName(patientIdToFullNameMap.get(occupiedSlot.getPatientLookupId()));
            }

            getDoctorAvailableSlotRespDto.setAvailableSlots(availableSlots);
            getDoctorAvailableSlotRespDto.setOccupiedSlots(occupiedSlots);

        }

        return getDoctorAvailableSlotRespDtoList;
    }

    public List<BasicAppointmentDataDto> getDoctorAvailableSlots(String[] doctorStartTimesStrArr, String[] doctorEndTimesStrArr,
                                                                       List<LocalTime> appointmentStartTimes, List<LocalTime> appointmentEndTimes){

        /* Instantiate a list of booleans to ensure appointments are not checked multiple times in an algorithm written later in this function */
        List<Boolean> appointmentChecked = new ArrayList<>(appointmentStartTimes.size());

        for(int i = 0 ; i < appointmentStartTimes.size() ; i++){
            appointmentChecked.add(false);
        }


        /* Convert doctor start and end times from string to local time */
        List<LocalTime> doctorStartTimes = null;
        List<LocalTime> doctorEndTimes = null;
        try {
            doctorStartTimes = Utility.stringTimeListToLocalTimeList(doctorStartTimesStrArr);
            doctorEndTimes = Utility.stringTimeListToLocalTimeList(doctorEndTimesStrArr);
        }
        catch(ParseException pe){
            // No need since the times come from the db and have been validated before input
        }

        /* Get a doctor's available slots
        *
        *  This algorithm works by moving the 'timeStart' and 'timeEnd' time pointers in between the empty time periods
        *  between the doctor's scheduled hours and appointments. If the empty time period they are set to have a duration
        *  that exceeds the minimum appointment time, this empty time period is marked as an available slot.
        *  */
        List<BasicAppointmentDataDto> doctorAvailableSlots = new ArrayList<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);

        LocalTime timeStart;
        LocalTime timeEnd;
        LocalTime tempTimeStart;
        long timeDuration;
        BasicAppointmentDataDto tempDoctorAvailableSlot;
        for(int i = 0 ; i < doctorStartTimes.size() ; i++){

            timeStart = doctorStartTimes.get(i);

            for(int j = 0 ; j < appointmentStartTimes.size() ; j++){

                /* If this appointment has been checked. Check the next */
                if (appointmentChecked.get(j) == true){
                    continue;
                }

                /* If this appointment starts after this work time period ends, check the next work time */
                if (appointmentStartTimes.get(j).isAfter(doctorEndTimes.get(i))){
                    continue;
                }

                /* Calculate duration of time in the slot */
                timeEnd = appointmentStartTimes.get(j);

                timeDuration = MINUTES.between(timeStart, timeEnd);

                /* Set the start of the slot we're looking at to the next gap */
                tempTimeStart = timeStart;
                timeStart = appointmentEndTimes.get(j);

                /* If the slot is less than the minimum appointment time, it cannot be utilized.
                *  Else, set this as an available slot
                *
                *  */
                if (timeDuration < appointmentMinDuration && timeDuration > 0){
                    continue;
                }
                else if (timeDuration < 1){
                    appointmentChecked.set(j, true);
                    continue;
                }

                /* Set the appointment start time to null as we've taken this appointment into account */
                appointmentChecked.set(j, true);

                /* Add the slot to the list of available slots */
                tempDoctorAvailableSlot = new BasicAppointmentDataDto();

                tempDoctorAvailableSlot.setTimeStart(dtf.format(tempTimeStart));
                tempDoctorAvailableSlot.setTimeEnd(dtf.format(timeEnd));

                doctorAvailableSlots.add(tempDoctorAvailableSlot);
            }

            /* Calculate the leftover slot after the doctor's last appointment and before his schedule end */
            timeEnd = doctorEndTimes.get(i);

            timeDuration = MINUTES.between(timeStart, timeEnd);

            if (timeDuration > appointmentMinDuration){
                tempDoctorAvailableSlot = new BasicAppointmentDataDto();

                tempDoctorAvailableSlot.setTimeStart(dtf.format(timeStart));
                tempDoctorAvailableSlot.setTimeEnd(dtf.format(timeEnd));

                doctorAvailableSlots.add(tempDoctorAvailableSlot);
            }
        }

        return doctorAvailableSlots;
    }

    public List<BasicAppointmentDataDto> getDoctorOccupiedSlots(List<LocalTime> appointmentStartTimesList,
                                                                List<LocalTime> appointmentEndTimesList,
                                                                List<Integer> patientIdList){

        /* Define the pairs from the appointment start and end times as an occupied slot for the doctor.
        *  Also set the patient's id who booked this slot if the id list is not null (this is when the ids are hidden).
        * */

        List<BasicAppointmentDataDto> occupiedSlots = new ArrayList<>();

        List<String> appointmentStartTimeStrList = Utility.localTimeListToStringTimeList(appointmentStartTimesList);
        List<String> appointmentEndTimeStrList = Utility.localTimeListToStringTimeList(appointmentEndTimesList);

        BasicAppointmentDataDto tempAppointmentData;
        for(int i = 0 ; i < appointmentStartTimeStrList.size() ; i++){
            tempAppointmentData = new BasicAppointmentDataDto();

            if (patientIdList != null){
                tempAppointmentData.setPatientLookupId(patientIdList.get(i));
            }

            tempAppointmentData.setTimeStart(appointmentStartTimeStrList.get(i));
            tempAppointmentData.setTimeEnd(appointmentEndTimeStrList.get(i));

            occupiedSlots.add(tempAppointmentData);
        }

        return occupiedSlots;
    }

    public List<LocalTime> getAppointmentStartTimesFromEntityList(List<AppointmentEntity> appointmentEntityList, Integer doctorId){
        /* Get the appointment start times from an appointment entity list */

        List<LocalTime> appointmentStartTimesList = new ArrayList<>();

        for(AppointmentEntity appointmentEntity : appointmentEntityList){
            if (doctorId == null) {
                appointmentStartTimesList.add(appointmentEntity.getTimeStart());
            } else if (doctorId.equals(appointmentEntity.getDoctor().getId())){
                appointmentStartTimesList.add(appointmentEntity.getTimeStart());
            }
        }

        return appointmentStartTimesList;
    }

    public List<LocalTime> getAppointmentEndTimesFromEntityList(List<AppointmentEntity> appointmentEntityList, Integer doctorId){
        /* Get the appointment end times from an appointment entity list */

        List<LocalTime> appointmentEndTimesList = new ArrayList<>();

        for(AppointmentEntity appointmentEntity : appointmentEntityList){
            if (doctorId == null) {
                appointmentEndTimesList.add(appointmentEntity.getTimeEnd());
            } else if (doctorId.equals(appointmentEntity.getDoctor().getId())){
                appointmentEndTimesList.add(appointmentEntity.getTimeEnd());
            }
        }

        return appointmentEndTimesList;
    }

    public List<Integer> getPatientIdsFromEntityList(List<AppointmentEntity> appointmentEntityList, Integer doctorId){
        /* Get the patient ids times from an appointment entity list */

        List<Integer> patientIdList = new ArrayList<>();

        for(AppointmentEntity appointmentEntity : appointmentEntityList){
            if (doctorId == null) {
                patientIdList.add(appointmentEntity.getPatient().getId());
            } else if (doctorId.equals(appointmentEntity.getDoctor().getId())){
                patientIdList.add(appointmentEntity.getPatient().getId());
            }
        }

        return patientIdList;
    }

}
