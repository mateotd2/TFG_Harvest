package com.udc.fic.mapper;

import com.udc.fic.harvest.DTOs.NewUserDTO;
import com.udc.fic.harvest.DTOs.UpdateUserDTO;
import com.udc.fic.harvest.DTOs.WorkerDTO;
import com.udc.fic.model.Empleado;
import com.udc.fic.model.Trabajador;
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

}
