package com.example.we_tried.admin.model;

import com.example.we_tried.user.model.BaseUser;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder

public class Admin extends BaseUser {

}
