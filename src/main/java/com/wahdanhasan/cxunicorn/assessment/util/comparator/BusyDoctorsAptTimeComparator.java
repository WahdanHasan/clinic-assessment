package com.wahdanhasan.cxunicorn.assessment.util.comparator;

import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetBusyDoctorsRespDto;

import java.util.Comparator;

public class BusyDoctorsAptTimeComparator implements Comparator<GetBusyDoctorsRespDto> {
    @Override
    public int compare(GetBusyDoctorsRespDto o1, GetBusyDoctorsRespDto o2) {
        return o2.getTotalAppointmentTimeLong().compareTo(o1.getTotalAppointmentTimeLong());
    }
}
