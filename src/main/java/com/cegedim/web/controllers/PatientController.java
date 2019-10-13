package com.cegedim.web.controllers;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.patient.PatientDTO;
import com.gvt.web.controllers.BaseActionForm;

@Controller
@RequestScope
public class PatientController extends BaseActionForm<PatientDTO> {

	private PersonService personService;

	public PatientController(PersonService personService) {
		this.personService = personService;
	}

	public void updatePatientAge(PatientDTO patient) {
		logger.debug("Calculating the age of a patient");

		if (patient.getBirthDate() != null) {
			LocalDate birthDate = patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = LocalDate.now();

			patient.setAge(Float.valueOf(Period.between(birthDate, currentDate).getYears()));
		}
	}

	@Override
	public PatientDTO saveObjectMethod(PatientDTO entity) throws Exception {
		return personService.savePatient(entity);
	}

	@Override
	protected PatientDTO updateObjectMethod(PatientDTO entity) {
		return personService.updatePatient(entity);
	}

}
