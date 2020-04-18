package com.cegedim.web.resources;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;

@Controller
@RequestScope
public class UploadHandler {

	private static final Logger logger = LoggerFactory.getLogger(UploadHandler.class);

	private UploadedFile file;

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public void upload() {
		if (file != null) {
			FacesMessage message = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		logger.trace("Uploading file {}", event.getFile().getFileName());

//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		file = event.getFile();
	}
}
