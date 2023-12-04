package com.udc.fic.mapper;

import com.udc.fic.harvest.DTOs.*;
import com.udc.fic.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SourceTargetMapper {

    @Mapping(target = "roles", ignore = true)
    Empleado toEmpleado(NewUserDTO newUserDto);

    Empleado toEmpleado(UpdateUserDTO updateUserDTO);


    WorkerDTO toWorker(Trabajador trabajador);

    @Mapping(target = "calendario", ignore = true)
    @Mapping(target = "id", ignore = true)
    Trabajador toTrabajador(WorkerDTO workerDTO);

    AttendanceDTO toAttendance(Asistencia asistencia);

    ElementoListaDisponibilidad toElementoListDisponibilidad(CallDTO callDTO);

    @Mapping(target = "daywork", source = "calendarDTO.day")
    Disponibilidad toDisponibilidad(CalendarDTO calendarDTO);

    @Mapping(target = "day", source = "disponibilidad.daywork")
    CalendarDTO toCalendarDTO(Disponibilidad disponibilidad);


    ZoneDTO toZoneDTO(Zona zona);

    @Mapping(target = "id",ignore = true)
    Zona toZona(ZoneDTO zoneDTO);
}
