package com.zentia.app.identity.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;
import com.zentia.app.identity.model.User;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "zentia_secret_key_financial_super_secure_jwt_token_2026";
    private static final long EXPIRATION_TIME_SECONDS = 86400; // 24 horas

    /**
     * Genera un token JWT estándar (RFC 7519) firmado con HMAC-SHA256
     */
    public String generateToken(User user) {
        try {
            // 1. Header
            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));

            // 2. Payload con claims esenciales
            long now = Instant.now().getEpochSecond();
            long exp = now + EXPIRATION_TIME_SECONDS;
            String payloadJson = String.format(
                    "{\"sub\":\"%s\",\"userId\":%d,\"name\":\"%s\",\"iat\":%d,\"exp\":%d}",
                    user.getEmail(),
                    user.getId(),
                    user.getName(),
                    now,
                    exp
            );
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

            // 3. Firma digital
            String dataToSign = encodedHeader + "." + encodedPayload;
            String encodedSignature = sign(dataToSign);

            return dataToSign + "." + encodedSignature;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el token JWT", e);
        }
    }

    /**
     * Valida la firma HMAC-SHA256 y comprueba que el token no haya expirado.
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return false;
            }
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String dataToSign = parts[0] + "." + parts[1];
            String expectedSignature = sign(dataToSign);

            // Comparación en tiempo constante para mitigar timing attacks
            if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                return false;
            }

            String payloadJson = getPayloadJson(token);
            if (payloadJson == null) {
                return false;
            }

            Long exp = extractLongClaim(payloadJson, "exp");
            if (exp == null) {
                return false;
            }

            return exp > Instant.now().getEpochSecond();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parsea el JSON del payload (Base64Url) y extrae el "userId" (Long).
     */
    public Long extractUserId(String token) {
        String payloadJson = getPayloadJson(token);
        if (payloadJson == null) {
            return null;
        }
        return extractLongClaim(payloadJson, "userId");
    }

    /**
     * Extrae el subject ("sub") que corresponde al correo del usuario.
     */
    public String extractEmail(String token) {
        String payloadJson = getPayloadJson(token);
        if (payloadJson == null) {
            return null;
        }
        return extractStringClaim(payloadJson, "sub");
    }

    private String getPayloadJson(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractStringClaim(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private Long extractLongClaim(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        if (start == end) return null;
        return Long.parseLong(json.substring(start, end));
    }

    private String sign(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }
}
