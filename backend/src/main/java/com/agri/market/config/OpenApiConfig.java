package com.agri.market.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "AgriMarket",
                        email = "contact@agrimarket.com",
                        url = "https://agrimarket.com"
                ),
                description = """
                        OpenAPI documentation for AgriMarket.
                        
                        AgriMarket is an agriculture marketplace platform
                        that connects farmers directly with customers.
                        Farmers can list agricultural products such as seeds,
                        fertilizers, fresh produce, and other agricultural
                        products, while customers can browse and purchase
                        available products.
                        """,
                title = "AgriMarket API",
                version = "1.0.0",
                license = @License(
                        name = "AgriMarket License",
                        url = "https://agrimarket.com/license"
                ),
                termsOfService = "https://agrimarket.com/terms"
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production Environment",
                        url = "https://api.agrimarket.com"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication using Bearer token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}