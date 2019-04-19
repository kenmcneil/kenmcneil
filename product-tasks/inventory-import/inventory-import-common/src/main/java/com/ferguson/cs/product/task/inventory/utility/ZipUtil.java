package com.ferguson.cs.product.task.inventory.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ZipUtil {
	private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);

	private ZipUtil() {
	}

	public static File gZip(File inputFile, File outputFile) {
		byte[] buffer = new byte[1024];
		try (GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(outputFile));
			 FileInputStream fileInputStream = new FileInputStream(inputFile)) {


			int len;
			while ((len = fileInputStream.read(buffer)) > 0) {
				gzos.write(buffer, 0, len);
			}

		} catch (IOException ex) {
			logger.warn(ex.toString(), ex);
		}

		return outputFile;
	}

	public static File gUnZip(File inputFile, File outputFile) {

		byte[] buffer = new byte[1024];

		try (GZIPInputStream gzis =	new GZIPInputStream(new FileInputStream(inputFile));
			 FileOutputStream fileOutputStream = new FileOutputStream(outputFile)){
			int len;
			while ((len = gzis.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, len);
			}

		} catch (IOException ex) {
			logger.warn(ex.toString(), ex);
		}

		return outputFile;
	}

	public static File zip(File inputFile, File outputFile) {
		byte[] buffer = new byte[1024];
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputFile));
			 FileInputStream fileInputStream = new FileInputStream(inputFile)) {


			int len;
			while ((len = fileInputStream.read(buffer)) > 0) {
				zipOutputStream.write(buffer, 0, len);
			}

		} catch (IOException ex) {
			logger.warn(ex.toString(), ex);
		}

		return outputFile;
	}

	public static File unZip(File inputFile, File outputFile) throws IOException {
		byte[] buffer = new byte[1024];

		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(inputFile));
			 FileOutputStream fileOutputStream = new FileOutputStream(outputFile)){
			int len;
			while ((len = zipInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, len);
			}

		} catch (IOException ex) {
			logger.warn(ex.toString(), ex);
		}

		return outputFile;
	}
}
