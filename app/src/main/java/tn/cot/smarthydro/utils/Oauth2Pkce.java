package tn.cot.smarthydro.utils;

import jakarta.enterprise.context.ApplicationScoped;
import tn.cot.smarthydro.entities.User;
import tn.cot.smarthydro.enums.Role;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@ApplicationScoped
public class Oauth2Pkce {


    private final Map<String, String> challenges = new HashMap<>();
    private final Map<String, String> authorizationsCodes = new HashMap<>();
    private final Map<String, User> AttemptsUsers = new HashMap<>();

    public void addChallenge(String state, String codeChallenge) {
        challenges.put(codeChallenge, state);
    }

    public String generateAuthorizationCode(String state, User user) {
        String authCode = UUID.randomUUID().toString();
        authorizationsCodes.put(state, authCode);
        AttemptsUsers.put(authCode, user);
        return authCode;
    }
    public Map<String, Object> CheckChallenge(String code, String codeVerifier) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(codeVerifier.getBytes(StandardCharsets.UTF_8));
        String key = Base64.getEncoder().encodeToString(md.digest());
        key = key.substring(0, key.length() - 1);
        key = key.replace("/", "_").replace("+", "-");

        if (challenges.containsKey(key)) {
            String state = challenges.get(key);
            if (authorizationsCodes.containsKey(state)) {
                if (authorizationsCodes.get(state).equals(code)) {
                    authorizationsCodes.remove(state);
                    challenges.remove(key);
                    User attemptedUser = AttemptsUsers.remove(code);
                    if (attemptedUser != null) {
                        String subject = attemptedUser.getEmail();
                        String approvedScopes = "resource:read,resource:write";
                        Set<Role> roles = attemptedUser.getRoles();
                        Map<String, Object> result = new HashMap<>();
                        result.put("tenantId", state);
                        result.put("subject", subject);
                        result.put("approvedScopes", approvedScopes);
                        result.put("roles", roles);
                        return result;
                    }
                }
            }
        }
        return null;
    }
}
