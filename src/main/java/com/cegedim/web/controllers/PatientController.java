package com.cegedim.web.controllers;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;

import com.gvt.main.hibernate.model.Persona;
import com.gvt.main.hibernate.model.Sexo;
import com.gvt.web.controllers.BaseActionForm;

@Controller
@RequestScope
public class PatientController extends BaseActionForm {

	public List<Sexo> buildSexItems() {
		List<Sexo> items = new ArrayList<>();

		Sexo sex = new Sexo("s001", "Masculino");
		sex.setId(1L);
		items.add(sex);

		sex = new Sexo("s002", "Femenino");
		sex.setId(1L);
		items.add(sex);

		return items;
	}

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
