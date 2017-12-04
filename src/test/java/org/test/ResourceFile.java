package org.test;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Helper file to get an external resource into Unitary test.
 */
public class ResourceFile implements Closeable {
	private String res;
	private File file = null;
	private InputStream stream;

	/**
	 * ResourceFile constructor.
	 * @param res the path for the file
	 */
	public ResourceFile(String res) {
		this.res = res;
		stream = createInputStream();
	}

	/**
	 * Try to get file
	 *
	 * @return the found File
	 * @throws IOException thrown in case of IO Error
	 */
	public File getFile() throws IOException {
		if (file == null) {
			createFile();
		}
		return file;
	}

	/**
	 * Get {@link InputStream}
	 *
	 * @return the {@link InputStream}
	 */
	public InputStream getInputStream() {
		return stream;
	}

	/**
	 * Create an {@link InputStream}
	 *
	 * @return the created {@link InputStream}
	 */
	private InputStream createInputStream() {
		return getClass().getResourceAsStream(res);
	}

	/**
	 * Get file content.
	 *
	 * @return the File content in UTF-8
	 * @throws IOException thrown in case of IO error
	 */
	public String getContent() throws IOException {
		return getContent("utf-8");
	}

	/**
	 * Get file content in a specific charset.
	 *
	 * @param charSet the charset for the content
	 * @return the file content in the specified charset
	 * @throws IOException thrown in case of IO error
	 */
	private String getContent(final String charSet) throws IOException {
		char[] tmp = new char[4096];
		StringBuilder b = new StringBuilder();
		try (InputStreamReader reader = new InputStreamReader(createInputStream(), Charset.forName(charSet))) {
			while (true) {
				int len = reader.read(tmp);
				if (len < 0) {
					break;
				}
				b.append(tmp, 0, len);
			}
			reader.close();
		}
		return b.toString();
	}

	/**
	 * Create File.
	 *
	 * @throws IOException thrown in case of IO error
	 */
	private void createFile() throws IOException {
		file = new File(".", res);
		try (InputStream stream = getClass().getResourceAsStream(res)) {
			file.createNewFile();
			FileOutputStream ostream = null;
			try {
				ostream = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				while (true) {
					int len = stream.read(buffer);
					if (len < 0) {
						break;
					}
					ostream.write(buffer, 0, len);
				}
			} finally {
				if (ostream != null) {
					ostream.close();
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}
}