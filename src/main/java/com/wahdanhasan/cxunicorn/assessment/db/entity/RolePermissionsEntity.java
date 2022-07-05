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
@Table(name="role_permissions", schema="public")
public class RolePermissionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name="role_id",
            referencedColumnName = "id"
    )
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(
            name="permission_id",
            referencedColumnName = "id"
    )
    private PermissionEntity permission;

}
