package com.cegedim.web.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;

import org.primefaces.model.UploadedFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.webflow.execution.RequestContextHolder;

import com.cegedim.web.image.ResourcesHandler;
import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.patient.FamilyRelationshipDTO;
import com.gvt.commons.dto.v1.patient.PatientDTO;
import com.gvt.web.controllers.BaseActionForm;

@Controller
@RequestScope
public class PatientController extends BaseActionForm<PatientDTO> {

	private PersonService personService;
	private ResourcesHandler resourcesHandler;

	private UploadedFile file;

	public PatientController(PersonService personService, ResourcesHandler resourcesHandler) {
		this.personService = personService;
		this.resourcesHandler = resourcesHandler;
	}

	public void init(PatientDTO patient) {
		patient.setFamilyRelationships(new ArrayList<>(2));

		patient.getFamilyRelationships().add(new FamilyRelationshipDTO());
		patient.getFamilyRelationships().add(new FamilyRelationshipDTO());
	}

	public void updatePatientAge(PatientDTO patient) {
		logger.debug("Calculating the age of a patient");

		if (patient.getBirthDate() != null) {
			LocalDate birthDate = patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = LocalDate.now();

			patient.setAge(Float.valueOf(Period.between(birthDate, currentDate).getYears()));
		}
	}

	public void updateAvatarType(PatientDTO patient) {
		if (patient.getSexId() != null) {
			if (patient.getSexId() == 1) {
				RequestContextHolder.getRequestContext().getViewScope().put("avatarType",
						"/images/male_photo_placeholder.jpg");
			} else {
				RequestContextHolder.getRequestContext().getViewScope().put("avatarType",
						"/images/female_photo_placeholder.png");
			}
		}
	}

	@Override
	public PatientDTO saveObjectMethod(PatientDTO entity) {
		try {
			saveImageToDisk(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return personService.savePatient(entity);
	}

	@Override
	protected PatientDTO updateObjectMethod(PatientDTO entity) {
		try {
			saveImageToDisk(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PatientDTO patientDTOUpdated = personService.updatePatient(entity);

		return patientDTOUpdated;
	}

	private void saveImageToDisk(PatientDTO entity) throws IOException {
		if (file == null) {
			return;
		}

		entity.setUrl1(resourcesHandler.saveImage(file));
		entity.setUrl1FileName(file.getFileName());
		entity.setUrl1FileSize(Double.valueOf(file.getSize()));
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
