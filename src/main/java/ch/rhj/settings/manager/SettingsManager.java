package ch.rhj.settings.manager;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;

public class SettingsManager {

	public static String LICENSE_URL = "https://raw.githubusercontent.com/rhjoerg/rhj-settings/master/LICENSE";
	public static String SETTINGS_URL = "https://raw.githubusercontent.com/rhjoerg/rhj-settings/master/settings.xml";
	public static String BUILD_URL = "https://raw.githubusercontent.com/rhjoerg/rhj-settings/master/build.yml";

	public static String LICENSE_PATH = "LICENSE";
	public static String SETTINGS_PATH = "settings.xml";
	public static String BUILD_PATH = ".github/workflows/build.yml";

	public static class Content {

		public final String license;
		public final String settings;
		public final String build;

		public Content(String license, String settings, String build) {

			this.license = license;
			this.settings = settings;
			this.build = build;
		}
	}

	public static void main(String[] args) throws Exception {

		List<Path> projects = managedProjects();

		if (projects.isEmpty())
			return;

		Content content = downloadContent();

		for (Path project : projects) {

			writeContent(content, project);
		}
	}

	private static void writeContent(Content content, Path project) throws Exception {

		writeContent(content.license, project.resolve(LICENSE_PATH));
		writeContent(content.settings, project.resolve(SETTINGS_PATH));
		writeContent(content.build, project.resolve(BUILD_PATH));
	}

	private static void writeContent(String content, Path target) throws Exception {

		Files.deleteIfExists(target);
		Files.createDirectories(target.getParent());
		Files.writeString(target, content, UTF_8);
	}

	private static Content downloadContent() throws Exception {

		HttpClient client = HttpClient.newHttpClient();
		String license = download(client, LICENSE_URL);
		String settings = download(client, SETTINGS_URL);
		String build = download(client, BUILD_URL);

		return new Content(license, settings, build);
	}

	private static String download(HttpClient client, String url) throws Exception {

		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		int status = response.statusCode();

		if (status < 400)
			return response.body();

		throw new Exception("response status " + status);
	}

	private static List<Path> managedProjects() throws Exception {

		Predicate<Path> isManaged = p -> Files.exists(p.resolve(".sharedsettings"));

		return Files.list(Paths.get("..")).filter(isManaged).map(p -> p.toAbsolutePath().normalize()).collect(toList());
	}
}
