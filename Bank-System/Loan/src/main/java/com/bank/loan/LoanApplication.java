package com.bank.loan;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(
        info = @Info(
                title = "Loans microservice REST API Documentation",
                description = "SBI Loans microservice REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Atul Patel",
                        email = "atulpatel@gmail.com",
                        url = "https://www.atulpatel.com/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.atulpatel.com/"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "SBI Loans microservice REST API Documentation",
                url = "https://www.atulpatel.com/"
        )
)
public class LoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanApplication.class, args);
    }

}
