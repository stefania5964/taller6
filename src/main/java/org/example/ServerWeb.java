package org.example;

import static spark.Spark.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

public class ServerWeb {
    private static Map<String, String> users = new HashMap<>();

    public static void main(String[] args) throws Exception {
        secure("keystores/ecikeystore.p12", "Colombia123", null, null);
        addUsers();

        staticFileLocation("/");
        port(getPort());
        get("/", (req, res) -> getHttps());

        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if (users.containsKey(username)) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes());
                byte[] digest = md.digest();
                String myHash = DatatypeConverter
                        .printHexBinary(digest).toUpperCase();
                if (users.get(username).equals(myHash)) {
                    req.session().attribute("username", username);
                    res.redirect("/");
                }
            } else {
                return "Incorrecto, intente de nuevo";
            }
            return null;
        });

        before((req, res) -> {
            String username = req.session().attribute("username");
            if (username == null) {
                res.redirect("/index.html");
            }
        });
    }
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5002;
    }

    /**
     * Hace conexión HTTPS con un SSL context específico (webAppTrustStore.p12)
     * @return respuesta HTTPS del servidor
     * @throws Exception
     */
    public static String getHttps() throws Exception {
        String httpsURL = "https://ec2-34-229-159-172.compute-1.amazonaws.com:5002";
        URL myURL = new URL(httpsURL);
        HttpsURLConnection conn = (HttpsURLConnection) myURL.openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();

    }

    private static void addUsers() throws NoSuchAlgorithmException {
        String user = "user";
        String password = "password";

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        users.put(user, myHash);

    }






    
}
