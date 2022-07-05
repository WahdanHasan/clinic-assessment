package com.wahdanhasan.cxunicorn.assessment.db.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="employee", schema="public")
@TypeDefs({
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        )
})
public class EmployeeEntity extends UserEntity{

    @Column(name="phone_ext")
    private Integer phoneExt;

    @Column(name="office_room_num")
    private String officeRoomNumber;

    @Type( type = "string-array" )
    @Column(
            name="work_start_times",
            columnDefinition = "text[]"
    )
    private String[] workStartTimes;

    @Type( type = "string-array" )
    @Column(
            name="work_end_times",
            columnDefinition = "text[]"
    )
    private String[] workEndTimes;

}
