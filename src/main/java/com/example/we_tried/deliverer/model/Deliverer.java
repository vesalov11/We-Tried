package com.example.we_tried.deliverer.model;

import com.example.we_tried.user.model.BaseUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Deliverer extends BaseUser {

    @Column(nullable = false)
    private String vehicleType;

    private boolean active;
}
