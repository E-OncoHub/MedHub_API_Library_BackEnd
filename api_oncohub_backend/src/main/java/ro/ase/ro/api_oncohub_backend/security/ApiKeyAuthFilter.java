package ro.ase.ro.api_oncohub_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ro.ase.ro.api_oncohub_backend.repositories.ApiKeyRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null) {
            var validApiKey = apiKeyRepository.findByKeyValueAndIsActiveTrue(apiKey)
                    .filter(key -> key.getExpiresAt() == null ||
                            key.getExpiresAt().isAfter(java.time.LocalDateTime.now()));

            if (validApiKey.isPresent()) {
                var authentication = new UsernamePasswordAuthenticationToken(
                        apiKey, null, AuthorityUtils.createAuthorityList("API_USER"));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}