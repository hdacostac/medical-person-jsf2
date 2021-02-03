package com.cegedim.web.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import com.cegedim.web.resources.ImagesResourceHandler;
import com.cegedim.web.resources.UploadResourcesHandler;
import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.patient.FamilyRelationshipDTO;
import com.gvt.commons.dto.v1.patient.PatientDTO;
import com.gvt.commons.dto.v1.simple.SimpleDTO;
import com.gvt.core.reflect.ReflectionUtils;
import com.gvt.web.controllers.AbstractActionForm;

import reactor.core.publisher.Mono;

@Controller
@RequestScope
public class PatientController extends AbstractActionForm<PatientDTO> {

	private PersonService personService;
	private WebClient webClient;
//	private ImagesResourceHandler resourcesHandler;

//	private UploadedFile file;

	private static final class SimpleDTOParametrizedReturnType extends ParameterizedTypeReference<List<SimpleDTO>> {
	}

	public PatientController(PersonService personService, WebClient webClient) {
		this.personService = personService;
//		this.resourcesHandler = resourcesHandler;
		this.webClient = webClient;
	}

	public void init(PatientDTO patient) {
		patient.setFamilyRelationships(new ArrayList<>(2));

		patient.getFamilyRelationships().add(new FamilyRelationshipDTO());
		patient.getFamilyRelationships().add(new FamilyRelationshipDTO());

//		personService.getBloodGroupsItems(combosHolder);
//		Mono<List<SimpleDTO>> result = this.webClient.get().uri("/api/v1/simple/bloodGroups")
		Mono<List<SimpleDTO>> result = this.webClient.get().uri("/api/v1/simple/bloodGroups")
//				.retrieve()
//				.bodyToFlux(SimpleDTO.class);
				.retrieve().bodyToMono(new SimpleDTOParametrizedReturnType());

//		combosHolder.setCombo1(result);

//		result.subscribe(items -> {
//			logger.debug("Persona:{}", items);
//
//			combosHolder.setCombo1(items);
//
////			scope.getFlowScope().put("bloodGroupsItems", items);
//
////			logger.trace("Values for combos:{}", scope.getFlowScope().get("bloodGroupsItems"));
//			logger.trace("Values for combos:{}", combosHolder.getCombo1());
//
////			RequestContextHolder.getRequestContext().getFlowScope().put("bloodGroupsItems", items);
//		});

		dumpEvents(new org.primefaces.component.selectonemenu.SelectOneMenu());
	}

	private void dumpEvents(UIComponentBase comp) {
		logger.debug(
				(comp + ":\n\tdefaultEvent: " + comp.getDefaultEventName() + ";\n\tEvents: " + comp.getEventNames()));
	}

	public void showValues(RequestContext scope) {
		logger.trace("Values for combos:{}", scope.getFlowScope().get("bloodGroupsItems"));
	}

	public void checkValues(AjaxBehaviorEvent event, RequestContext context) {
		logger.trace("click on combo!!");

//		logger.trace("elementos:{}", ((SelectOneMenu) event.getComponent()).getChildren().size());
//		logger.trace("id:{}", ((SelectOneMenu) event.getComponent()).getId());
		if (RequestContextHolder.getRequestContext() != null) {
//			if (!((List) RequestContextHolder.getRequestContext().getFlowScope().get("bloodGroupsItems")).isEmpty()) {
			logger.trace("Trying to refresh component:{}", context.getFlowScope().get("bloodGroupsItems"));

//				PrimeFaces.current().ajax()
//						.update("patientForm-idTabView-patientBloodGroup-patientBloodGroupComponent");
//			}
		}

//		PrimeFaces.current().ajax().update(expressions);

//		if(!items.isEmpty()) {
//			
//		}
	}

	public void updatePatientAge(PatientDTO patient) {
		logger.debug("Possible value in the date:{}", patient.getBirthDate());

		if (patient.getBirthDate() != null) {
//			LocalDate birthDate = patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = LocalDate.now();

			patient.setAge(Float.valueOf(Period.between(patient.getBirthDate(), currentDate).getYears()));
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
				entity.setHomePhone(ReflectionUtils.getDeleteCodeForString());
			}
			if (StringUtils.isBlank(entity.getEmail())) {
				entity.setEmail(ReflectionUtils.getDeleteCodeForString());
			}
			if (entity.getBloodGroupId() == null) {
				entity.setBloodGroupId(ReflectionUtils.getDeleteCodeForCombosValue());
			}
			if (entity.getBirthDate() == null) {
				entity.setBirthDate(ReflectionUtils.getDeleteCodeForLocalDate());
				entity.setAge(ReflectionUtils.getDeleteCodeForFloat());

//				if (logger.isTraceEnabled()) {
//					SimpleDateFormat sdf = new SimpleDateFormat();
//					logger.trace("BirthDate value to remove send from view:{}", sdf.format(entity.getBirthDate()));
//				}
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

//		entity.setUrl1(imagesResourceHandler.saveImage(uploadResourcesHandler.getFile()));
		entity.setUrl1FileName(imagesResourceHandler.saveImage(uploadResourcesHandler.getFile()));
//		entity.setUrl1FileName(uploadResourcesHandler.getFile().getFileName());
//		entity.setUrl1FileSize(Double.valueOf(uploadResourcesHandler.getFile().getSize()));
	}

//	public UploadedFile getFile() {
//		return file;
//	}
//
//	public void setFile(UploadedFile file) {
//		this.file = file;
//	}

}
