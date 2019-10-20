package com.cegedim.web.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.webflow.execution.RequestContextHolder;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.patient.PatientDTO;
import com.gvt.web.controllers.BaseActionForm;

@Controller
@RequestScope
public class PatientController extends BaseActionForm<PatientDTO> {

	private PersonService personService;
	private UploadedFile file;

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

		return personService.updatePatient(entity);
	}

	private void saveImageToDisk(PatientDTO entity) throws IOException {
		if (file == null) {
			return;
		}

		Path folder = Paths.get("/home/harold/development/images");
		String filename = FilenameUtils.getBaseName(file.getFileName());
		String extension = FilenameUtils.getExtension(file.getFileName());
		Path filePath = Files.createTempFile(folder, filename + "-", "." + extension);

		try (InputStream input = file.getInputstream()) {
			Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
		}

		entity.setUrl1(filePath.getFileName().toString());
		entity.setUrl1FileName(file.getFileName());
		entity.setUrl1FileSize(Double.valueOf(file.getSize()));
	}

	public StreamedContent getImage() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the view. Return a stub StreamedContent so that it will
			// generate right URL.
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Return a real StreamedContent with the
			// image bytes.
			String filename = context.getExternalContext().getRequestParameterMap().get("filename");

			return new DefaultStreamedContent(
					new FileInputStream(new File("/home/harold/development/images", filename)));
		}
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
