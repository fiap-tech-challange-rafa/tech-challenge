package br.com.fiap.techchallange.interfaces.rest.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Acesso ADMIN autorizado ✅");
    }
}
