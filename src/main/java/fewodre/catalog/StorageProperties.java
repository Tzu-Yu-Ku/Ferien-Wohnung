package fewodre.catalog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	public static String location = Paths
			.get("")
			.toAbsolutePath()
			.toString() + "/src/main/resources/static/resources/img/";


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}