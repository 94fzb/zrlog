package com.fzb.blog.util;

import com.fzb.common.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class LoadJarUtil {

	public static void loadJar(File[] jarFiles) throws IOException, URISyntaxException {

		ZipInputStream zipIn = null;
		ZipFile zip = null;
		try {
			List<String> str = new ArrayList<String>();
			List<File> classFiles = new ArrayList<File>();
			for (File string : jarFiles) {
				zipIn = new ZipInputStream(new FileInputStream(string));
				ZipEntry in;
				zip=new ZipFile(string);
				
				while ((in = zipIn.getNextEntry()) != null) {
					if (!in.getName().endsWith("/")
							&& (in.getName().endsWith(".class") || in.getName().endsWith(".properties"))) {
						byte[] b = IOUtil.getByteByInputStream(zip.getInputStream(in));
						File file = new File(new File(LoadJarUtil.class.getResource("/").toURI().toURL().toString()
								.substring(6))
								+ "/" + in.getName());
						if (!new File(file.getParent()).exists()) {
							new File(file.getParent()).mkdirs();
						}
						classFiles.add(file);
						FileOutputStream fout = new FileOutputStream(file);
						fout.write(b);
						fout.close();
						
						if(in.getName().endsWith(".class")){
							str.add(in.getName()
									.substring(0, in.getName().lastIndexOf("."))
									.replace("/", "."));
						}
					}
				}
			}
			
			for (String string : str) {
				try {
					ClassLoader.getSystemClassLoader().loadClass(string);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			}
			/*for (File file : classFiles) {
				file.delete();
				file.getParentFile().delete();
			}*/
		} finally {
			if (zipIn != null) {
				zipIn.close();
			}
			if (zip != null) {
				zip.close();
			}
		}
	}
}
