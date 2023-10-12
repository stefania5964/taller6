package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;
public class Securespark {
    public static void main(String[] args){
        port(getPort());
        secure("keystores/ecikeystore.p12", "Colombia123", null, null);
        get("/hello", (req,res) -> "Hello World");
    }
    private static int getPort(){
        if(System.getenv("PORT") != null){
           return Integer.parseInt(System.getenv("PORT")) ;

        }
        return 5000;
    }


}
