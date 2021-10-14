package com.mokhtarabadi.secureapi.server.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mokhtarabadi.secureapi.server.Main;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.codec.digest.HmacUtils;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.net.URLDecoder;
import java.util.HashMap;

import static spark.Spark.halt;

@Slf4j
@UtilityClass
public class EncryptionUtility {

    private final HashMap<Long, String> NONCE =
            new HashMap<>(); // must remove old nonce every 5 seconds

    public void checkApiKey(Request request, Response response) {
        val apiKey = request.headers("X-API-KEY");
        if (apiKey == null) {
            halt(HttpStatus.BAD_REQUEST_400, "api key header not found");
            return;
        }

        if (Main.API_ACCESS_DETAILS.values().stream()
                .noneMatch(apiAccessDetails -> apiAccessDetails.getKey().equals(apiKey))) {
            halt(HttpStatus.UNAUTHORIZED_401, "api key not valid");
        }
    }

    @SneakyThrows
    public void checkSignature(Request request, Response response) {
        if (!request.requestMethod().equals("POST") && !request.requestMethod().equals("PUT")) {
            return;
        }

        log.info("try checking signature for: {}", request.pathInfo());

        val apiKey = request.headers("X-API-KEY");
        val secretKey =
                Main.API_ACCESS_DETAILS.values().stream()
                        .filter(apiAccessDetails -> apiAccessDetails.getKey().equals(apiKey))
                        .findFirst()
                        .get()
                        .getSecret();

        val parts = request.body().split("\\.");
        val signedBody = URLDecoder.decode(parts[0]);
        val signature = parts[1];

        log.info("body: {}", request.body());

        if (signature.equals(HmacUtils.hmacSha256Hex(secretKey, signedBody))) {
            val mapper = new JsonMapper();
            val object = mapper.readValue(signedBody, JsonNode.class);
            val timestamp = object.get("timestamp").asLong();
            val receiveWindows = object.get("receive_windows").asInt(5000);
            val nonce = object.get("nonce").asText();

            val currentTimeMillis = System.currentTimeMillis();

            // check time
            if (timestamp < (currentTimeMillis + 1000)
                    && (currentTimeMillis - timestamp) <= receiveWindows) {
                // check nonce
                if (!NONCE.containsValue(nonce)) {
                    NONCE.put(currentTimeMillis, nonce);
                    request.attribute("data", signedBody);

                    log.info("valid data: {}", signedBody);
                } else {
                    halt("can't process request, nonce is duplicate");
                }
            } else {
                halt("can't process this request, time expiry");
            }
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "signature not valid");
        }
    }
}
