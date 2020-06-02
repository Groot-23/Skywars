package me.groot_23.skywars.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceExtractor {
	
	/**
	 * This function copies all resources of this jar located at the resPath folder to
	 * the dest folder. It's used to extract resources
	 * @param resPath Path to the resource
	 * @param dest Path to the destination
	 * @param relativeResPath If set to true, the relative path from resPath will be used
	 */
	public static void extractResources(String resPath, Path dest, boolean relativeResPath) {
		File jarFile;
		try {
			jarFile = new File(ResourceExtractor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if(jarFile.isFile()) {
				try(JarFile jar = new JarFile(jarFile)) {
					Enumeration<JarEntry> entries = jar.entries();
					while(entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						if(name.startsWith(resPath)) {
							if(relativeResPath) {
								name = name.substring(resPath.length());
								if(!resPath.endsWith("/")) {
									name = name.substring(name.indexOf('/') + 1);
								}
							}
							if(entry.isDirectory()) {
								Path dir = dest.resolve(name);
								Files.createDirectory(dir);
							} else {
								Path file = dest.resolve(name);
								File parent = file.toFile().getParentFile();
								if(!parent.exists()) {
									parent.mkdirs();
								}
								try(InputStream in = jar.getInputStream(entry)) {
									Files.copy(in, file);
								}
							}
						}
					}
				} catch(IOException e) {
					e.printStackTrace();
				}

			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}
}
