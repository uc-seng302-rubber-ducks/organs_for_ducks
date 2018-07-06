package odms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
public class TokenStore {

    private Set<Token> knownTokens;

    @Autowired
    public TokenStore() {
        knownTokens = new HashSet<>();
    }

    public Token get(String token) {
        for(Token t : knownTokens){
            if (t.getToken().equals(token)){
                return t;
            }
        }
        return null;
    }

    public boolean add(Token token) {
        return knownTokens.add(token);
    }

    public boolean contains(Token token) {
        return knownTokens.contains(token);
    }

    public boolean contains(String tokenStr) {
        return get(tokenStr) != null;
    }

    public boolean remove(Token token) {
        return knownTokens.remove(token);
    }

    public boolean remove(String tokenStr) {
        Token t = get(tokenStr);
        return knownTokens.remove(t);
    }

}
