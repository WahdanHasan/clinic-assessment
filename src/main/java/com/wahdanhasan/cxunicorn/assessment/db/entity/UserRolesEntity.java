package com.wahdanhasan.cxunicorn.assessment.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_roles", schema="public")
public class UserRolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name="user_id",
            referencedColumnName = "id"
    )
    private UserEntity user;

    @ManyToOne
    @JoinColumn(
            name="role_id",
            referencedColumnName = "id"
    )
    private RoleEntity role;
}
