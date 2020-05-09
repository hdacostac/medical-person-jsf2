package com.cegedim.web.resources.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cegedim.web.resources.ImagesResourceHandler;

@Component("imagesResourceHandler")
public class LocalImagesResourceHandler implements ImagesResourceHandler {

	@Value("${app.resources.disk.directory}")
	private String resourcesDirectory;

	@Override
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

			return new DefaultStreamedContent(new FileInputStream(new File(resourcesDirectory, filename)));
		}
	}

	@Override
	public String saveImage(UploadedFile file) throws IOException {
		if (file == null) {
			return null;
		}

		Path folder = Paths.get(resourcesDirectory);
		String filename = FilenameUtils.getBaseName(file.getFileName());
		String extension = FilenameUtils.getExtension(file.getFileName());
		Path filePath = Files.createTempFile(folder, filename + "-", "." + extension);

		try (InputStream input = file.getInputstream()) {
			Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
		}

		return filePath.getFileName().toString();
	}

}
