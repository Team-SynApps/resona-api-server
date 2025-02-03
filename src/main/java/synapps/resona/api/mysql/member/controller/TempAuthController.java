package synapps.resona.api.mysql.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synapps.resona.api.mysql.member.service.TempTokenService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TempAuthController {
    private final TempTokenService tempTokenService;

    @PostMapping("/temp")
    public ResponseEntity<?> createTempToken(HttpServletRequest request, HttpServletResponse response) {
        return tempTokenService.createTemporaryToken(request, response);
    }
}
