package com.wahdanhasan.cxunicorn.assessment.db.entity;

import com.wahdanhasan.cxunicorn.assessment.util.Constants;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="appointment", schema="public")
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name="doctor_id",
            referencedColumnName = "id"
    )
    private DoctorEntity doctor;

    @ManyToOne
    @JoinColumn(
            name="patient_id",
            referencedColumnName = "id"
    )
    private PatientEntity patient;

    @Column(name="date_created")
    private LocalDate dateCreated;

    @Column(name="time_start")
    private LocalTime timeStart;

    @Column(name="time_end")
    private LocalTime timeEnd;

    @Column(name="duration_mins")
    private Integer durationMins;

    @Column(name="patient_attended")
    private Boolean patientAttended = !Constants.PATIENT_ATTENDED;

    @Column(name="status")
    private String appointmentStatus = Constants.APPOINTMENT_STATUS_VALID;
}
