package com.cegedim.web.controllers;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;

import com.gvt.main.hibernate.model.Persona;
import com.gvt.web.controllers.BaseActionForm;

@Controller
@RequestScope
public class PatientController extends BaseActionForm {

	public void updatePatientAge(Persona patient) {
		logger.debug("Calculating the age of a patient");

		if (patient.getFechaNacimientoPersona() != null) {
			LocalDate birthDate = patient.getFechaNacimientoPersona().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
			LocalDate currentDate = LocalDate.now();

			patient.setEdad(Float.valueOf(Period.between(birthDate, currentDate).getYears()));
		}
	}

}
