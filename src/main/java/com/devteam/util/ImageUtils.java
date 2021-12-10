package com.devteam.util;

import com.devteam.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;


@Component
public class ImageUtils {
	private static RestTemplate restTemplate = new RestTemplate();


	private static final String githubUploadApi = "https://api.github.com/repos/%s/%s/contents%s/%s";
	private static final String cdnUrl4Github = "https://cdn.jsdelivr.net/gh/%s/%s%s/%s";

	private static String serverUploadPath;
	private static String serverUrl;
	private static String githubToken;
	private static String githubUsername;
	private static String githubRepos;
	private static String githubReposPath;

	@Value("${upload.path}")
	public void setServerUploadPath(String serverUploadPath) {
		this.serverUploadPath = serverUploadPath;
	}

	@Value("${custom.url.api}")
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	@Value("${upload.github.token}")
	public void setGithubToken(String githubToken) {
		this.githubToken = githubToken;
	}

	@Value("${upload.github.username}")
	public void setGithubUsername(String githubUsername) {
		this.githubUsername = githubUsername;
	}

	@Value("${upload.github.repos}")
	public void setGithubRepos(String githubRepos) {
		this.githubRepos = githubRepos;
	}

	@Value("${upload.github.repos.path}")
	public void setGithubReposPath(String githubReposPath) {
		this.githubReposPath = githubReposPath;
	}

	@AllArgsConstructor
	@Getter
	static class ImageResource {
		byte[] data;
		String type;
	}

	public static ImageResource getImageByRequest(String url) {
		ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
		if ("image".equals(responseEntity.getHeaders().getContentType().getType())) {
			return new ImageResource(responseEntity.getBody(), responseEntity.getHeaders().getContentType().getSubtype());
		}
		throw new BadRequestException("response contentType unlike image");
	}

	public static String saveImage(ImageResource image) throws IOException {
		File folder = new File(serverUploadPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = UUID.randomUUID() + "." + image.getType();
		FileOutputStream fileOutputStream = new FileOutputStream(serverUploadPath + fileName);
		fileOutputStream.write(image.getData());
		fileOutputStream.close();
		return serverUrl + "/image/" + fileName;
	}

	public static String push2Github(ImageResource image) {
		String fileName = UUID.randomUUID() + "." + image.getType();
		String url = String.format(githubUploadApi, githubUsername, githubRepos, githubReposPath, fileName);
		String imgBase64 = Base64.getEncoder().encodeToString(image.getData());

		HttpHeaders headers = new HttpHeaders();
		headers.put("Authorization", Collections.singletonList("token " + githubToken));

		HashMap<String, String> body = new HashMap<>();
		body.put("message", "Add files via NBlog");
		body.put("content", imgBase64);

		HttpEntity httpEntity = new HttpEntity(body, headers);
		restTemplate.put(url, httpEntity);

		return String.format(cdnUrl4Github, githubUsername, githubRepos, githubReposPath, fileName);
	}
}
