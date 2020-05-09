package com.cegedim.web.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.webflow.execution.RequestContextHolder;

import com.cegedim.web.resources.ImagesResourceHandler;
import com.cegedim.web.resources.UploadResourcesHandler;
import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.patient.FamilyRelationshipDTO;
import com.gvt.commons.dto.v1.patient.PatientDTO;
import com.gvt.core.reflect.ReflectionUtils;
import com.gvt.web.controllers.AbstractActionForm;

@Controller
@RequestScope
public class PatientController extends AbstractActionForm<PatientDTO> {

	private PersonService personService;
//	private ImagesResourceHandler resourcesHandler;

//	private UploadedFile file;

	public PatientController(PersonService personService) {
		this.personService = personService;
//		this.resourcesHandler = resourcesHandler;
	}

	public void init(PatientDTO patient) {
		patient.setFamilyRelationships(new ArrayList<>(2));

		patient.getFamilyRelationships().add(new FamilyRelationshipDTO());
		patient.getFamilyRelationships().add(new FamilyRelationshipDTO());
	}

	public void updatePatientAge(PatientDTO patient) {
		logger.debug("Possible value in the date:{}", patient.getBirthDate());

		if (patient.getBirthDate() != null) {
			LocalDate birthDate = patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = LocalDate.now();

			patient.setAge(Float.valueOf(Period.between(birthDate, currentDate).getYears()));
		} else {
			patient.setAge(null);
		}
	}

	public void updateAvatarType(PatientDTO patient, UploadResourcesHandler uploadResourcesHandler,
			ImagesResourceHandler imagesResourceHandler) throws IOException {
		if (patient.getSexId() != null) {
			if (patient.getSexId() == 1) {
				RequestContextHolder.getRequestContext().getViewScope().put("avatarType",
						"/images/male_photo_placeholder.jpg");
			} else {
				RequestContextHolder.getRequestContext().getViewScope().put("avatarType",
						"/images/female_photo_placeholder.png");
			}
		}

		setImageToEntity(patient, uploadResourcesHandler, imagesResourceHandler);
	}

	@Override
	protected void validate(PatientDTO entity, int validatingOnAction) throws Exception {
		super.validate(entity, validatingOnAction);

		if (validatingOnAction == VALIDATING_ON_UPDATE) {
			if (StringUtils.isBlank(entity.getHomePhone())) {
				entity.setHomePhone(ReflectionUtils.DELETE_CODE_FOR_STRING);
			}
			if (StringUtils.isBlank(entity.getEmail())) {
				entity.setEmail(ReflectionUtils.DELETE_CODE_FOR_STRING);
			}
			if (entity.getBloodGroupId() == null) {
				entity.setBloodGroupId(ReflectionUtils.DELETE_CODE_FOR_COMBOS_VALUE);
			}
			if (entity.getBirthDate() == null) {
				entity.setBirthDate(ReflectionUtils.DELETE_CODE_FOR_DATE);
				entity.setAge(ReflectionUtils.DELETE_CODE_FOR_FLOAT);

				if (logger.isTraceEnabled()) {
					SimpleDateFormat sdf = new SimpleDateFormat();
					logger.trace("BirthDate value to remove send from view:{}", sdf.format(entity.getBirthDate()));
				}
			}
		}
	}

	@Override
	public PatientDTO saveObjectMethod(PatientDTO entity) {
//		try {
//			saveImageToDisk(entity,
//					(UploadResourcesHandler) RequestContextHolder.getRequestContext().getFlowScope().get("uploadHandler"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return personService.savePatient(entity);
	}

	@Override
	protected PatientDTO updateObjectMethod(PatientDTO entity) {
//		try {
//			saveImageToDisk(entity,
//					(UploadResourcesHandler) RequestContextHolder.getRequestContext().getFlowScope().get("uploadHandler"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return personService.updatePatient(entity);
	}

	private void setImageToEntity(PatientDTO entity, UploadResourcesHandler uploadResourcesHandler,
			ImagesResourceHandler imagesResourceHandler) throws IOException {
		if (uploadResourcesHandler.getFile() == null) {
			return;
		}

		entity.setUrl1(imagesResourceHandler.saveImage(uploadResourcesHandler.getFile()));
		entity.setUrl1FileName(uploadResourcesHandler.getFile().getFileName());
		entity.setUrl1FileSize(Double.valueOf(uploadResourcesHandler.getFile().getSize()));
	}

//	public UploadedFile getFile() {
//		return file;
//	}
//
//	public void setFile(UploadedFile file) {
//		this.file = file;
//	}

}
