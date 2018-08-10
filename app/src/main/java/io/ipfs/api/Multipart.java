package io.ipfs.api;

import android.os.Build;
import android.support.annotation.RequiresApi;
import com.annimon.stream.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.Random;

public class Multipart {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream out;
    private PrintWriter writer;

    public Multipart(String requestURL, String charset) {
        this.charset = charset;

        boundary = createBoundary();

        try {
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "Java IPFS CLient");
            out = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(out, charset), true);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String createBoundary() {
        Random r = new Random();
        String allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 32; i++)
            b.append(allowed.charAt(r.nextInt(allowed.length())));
        return b.toString();
    }

    public void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addSubtree(String fileName, NamedStreamable dir) {
        addDirectoryPart(fileName);
        for (NamedStreamable f : dir.getChildren()) {
            if (f.isDirectory()) {
                addSubtree(fileName, f);
            } else {
                addFilePart(fileName, f);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O) public void addSubtree(Path parentPath, NamedStreamable dir) throws IOException {
        Path dirPath = parentPath.resolve(dir.getName().get());
        addDirectoryPart(dirPath);
        for (NamedStreamable f : dir.getChildren()) {
            if (f.isDirectory()) {
                addSubtree(dirPath, f);
            } else {
                addFilePart("file", dirPath, f);
            }
        }
    }

    public void addDirectoryPart(String fileName) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: file; filename=\"").append(encode(fileName)).append("\"").append(LINE_FEED);
        writer.append("Content-Type: application/x-directory").append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
    }
    public void addDirectoryPart(Path path) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: file; filename=\"").append(encode(path.toString())).append("\"").append(LINE_FEED);
        writer.append("Content-Type: application/x-directory").append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
    }

    private static String encode(String in) {
        try {
            return URLEncoder.encode(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addFilePart(String fileName, NamedStreamable uploadFile) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: file; filename=\"").append(fileName).append("\";").append(LINE_FEED);
        writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        try {
            InputStream inputStream = uploadFile.getInputStream();
            byte[] buffer = new byte[4096];
            int r;
            while ((r = inputStream.read(buffer)) != -1) out.write(buffer, 0, r);
            out.flush();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        writer.append(LINE_FEED);
        writer.flush();
    }

    @RequiresApi(api = Build.VERSION_CODES.O) public void addFilePart(String fieldName, Path parent, NamedStreamable uploadFile) {
        Optional<String> fileName = uploadFile.getName().map(n -> encode(parent.resolve(n).toString().replace('\\', '/')));
        writer.append("--").append(boundary).append(LINE_FEED);
        if (!fileName.isPresent()) {
            writer.append("Content-Disposition: file; name=\"").append(fieldName).append("\";").append(LINE_FEED);
        } else {
            writer.append("Content-Disposition: file; filename=\"").append(fileName.get()).append("\";").append(LINE_FEED);
        }
        writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        try {
            InputStream inputStream = uploadFile.getInputStream();
            byte[] buffer = new byte[4096];
            int r;
            while ((r = inputStream.read(buffer)) != -1) out.write(buffer, 0, r);
            out.flush();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        writer.append(LINE_FEED);
        writer.flush();
    }

    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    public String finish() {
        StringBuilder b = new StringBuilder();

        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        try {
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    b.append(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        b.append(line);
                    }
                    reader.close();
                } catch (Throwable t) {
                }
                throw new IOException(
                        "Server returned status: " + status + " with body: " + b.toString() + " and Trailer header: " + httpConn.getHeaderFields()
                                .get("Trailer"));
            }

            return b.toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
