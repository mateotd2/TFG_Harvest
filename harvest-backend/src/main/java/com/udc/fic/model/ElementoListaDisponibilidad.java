package com.udc.fic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementoListaDisponibilidad {
    Long id;
    LocalTime checkin;
    LocalTime checkout;
    boolean attendance;
}
