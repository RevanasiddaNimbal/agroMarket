package com.agri.market.product.specification;

import com.agri.market.product.entity.Product;
import com.agri.market.product.entity.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> hasStatus(
            final ProductStatus status
    ) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(
                        root.get("status"),
                        status.name()
                );
    }

    public static Specification<Product> hasCategory(
            final String categoryId
    ) {
        return (root, query, criteriaBuilder) ->
                categoryId == null || categoryId.isBlank()
                        ? null
                        : criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId.trim()
                );
    }

    public static Specification<Product> belongsToUser(
            final String userId
    ) {
        return (root, query, criteriaBuilder) ->
                userId == null || userId.isBlank()
                        ? null
                        : criteriaBuilder.equal(
                        root.get("farmer").get("id"),
                        userId
                );
    }

    public static Specification<Product> search(
            final String searchQuery
    ) {
        return (root, query, criteriaBuilder) -> {

            if (searchQuery == null || searchQuery.isBlank()) {
                return null;
            }

            final String[] keywords =
                    searchQuery.trim()
                            .toLowerCase()
                            .split("\\s+");

            return criteriaBuilder.or(
                    java.util.Arrays.stream(keywords)
                            .map(keyword -> {

                                final String pattern =
                                        "%" + keyword + "%";

                                return criteriaBuilder.or(
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get("name")
                                                ),
                                                pattern
                                        ),
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get("description")
                                                ),
                                                pattern
                                        ),
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get("unit")
                                                ),
                                                pattern
                                        ),
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get("location")
                                                ),
                                                pattern
                                        )
                                );
                            })
                            .toArray(
                                    jakarta.persistence.criteria.Predicate[]::new
                            )
            );
        };
    }

    public static Specification<Product> nameContains(
            final String name
    ) {
        return (root, query, criteriaBuilder) ->
                name == null || name.isBlank()
                        ? null
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("name")
                        ),
                        "%" + name.trim().toLowerCase() + "%"
                );
    }

    public static Specification<Product> descriptionContains(
            final String description
    ) {
        return (root, query, criteriaBuilder) ->
                description == null || description.isBlank()
                        ? null
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("description")
                        ),
                        "%" + description.trim().toLowerCase() + "%"
                );
    }

    public static Specification<Product> locationContains(
            final String location
    ) {
        return (root, query, criteriaBuilder) ->
                location == null || location.isBlank()
                        ? null
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("location")
                        ),
                        "%" + location.trim().toLowerCase() + "%"
                );
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(
            final BigDecimal minPrice
    ) {
        return (root, query, criteriaBuilder) ->
                minPrice == null
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }

    public static Specification<Product> priceLessThanOrEqualTo(
            final BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) ->
                maxPrice == null
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
    }

    public static Specification<Product> hasUnit(
            final String unit
    ) {
        return (root, query, criteriaBuilder) ->
                unit == null || unit.isBlank()
                        ? null
                        : criteriaBuilder.equal(
                        criteriaBuilder.lower(
                                root.get("unit")
                        ),
                        unit.trim().toLowerCase()
                );
    }
}