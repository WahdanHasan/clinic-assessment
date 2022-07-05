package com.wahdanhasan.cxunicorn.assessment.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctor", schema="public")
public class DoctorEntity extends EmployeeEntity{

    @Column(name="specialty")
    private String specialty;

    @OneToMany(
            mappedBy = "doctor",
            fetch = FetchType.LAZY
    )
    private List<AppointmentEntity> appointments;
}
