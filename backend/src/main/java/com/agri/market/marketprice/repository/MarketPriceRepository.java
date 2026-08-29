package com.agri.market.marketprice.repository;

import com.agri.market.marketprice.entity.MarketPrice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MarketPriceRepository
        extends JpaRepository<MarketPrice, String> {

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE m.arrivalDate = (
                SELECT MAX(m2.arrivalDate)
                FROM MarketPrice m2
            )
            ORDER BY m.commodity ASC, m.state ASC, m.market ASC
            """)
    List<MarketPrice> findLatestPrices(Pageable pageable);

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND LOWER(m.district) = LOWER(:district)
            AND LOWER(m.market) = LOWER(:market)
            ORDER BY m.arrivalDate DESC
            """)
    List<MarketPrice> findByMarket(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("district") String district,
            @Param("market") String market
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND LOWER(m.district) = LOWER(:district)
            ORDER BY m.arrivalDate DESC
            """)
    List<MarketPrice> findByCommodityStateDistrict(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("district") String district
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            ORDER BY m.arrivalDate DESC
            """)
    List<MarketPrice> findByCommodityState(
            @Param("commodity") String commodity,
            @Param("state") String state
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            ORDER BY m.arrivalDate DESC
            """)
    List<MarketPrice> findByCommodity(
            @Param("commodity") String commodity
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.state) = LOWER(:state)
            ORDER BY m.arrivalDate DESC
            """)
    List<MarketPrice> findByState(
            @Param("state") String state
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.district) = LOWER(:district)
            ORDER BY m.arrivalDate DESC
            """)
    List<MarketPrice> findByDistrict(
            @Param("district") String district
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.market) = LOWER(:market)
            ORDER BY m.arrivalDate DESC
            """)
    List<MarketPrice> findByMarketOnly(
            @Param("market") String market
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND m.arrivalDate = :date
            ORDER BY m.state ASC, m.market ASC
            """)
    List<MarketPrice> findByCommodityAndDate(
            @Param("commodity") String commodity,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND m.arrivalDate = :date
            ORDER BY m.market ASC
            """)
    List<MarketPrice> findByCommodityStateAndDate(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND LOWER(m.district) = LOWER(:district)
            AND m.arrivalDate = :date
            ORDER BY m.market ASC
            """)
    List<MarketPrice> findByCommodityStateDistrictAndDate(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("district") String district,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND LOWER(m.district) = LOWER(:district)
            AND LOWER(m.market) = LOWER(:market)
            AND m.arrivalDate = :date
            """)
    List<MarketPrice> findByMarketAndDate(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("district") String district,
            @Param("market") String market,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.state) = LOWER(:state)
            AND m.arrivalDate = :date
            ORDER BY m.commodity ASC, m.market ASC
            """)
    List<MarketPrice> findByStateAndDate(
            @Param("state") String state,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.district) = LOWER(:district)
            AND m.arrivalDate = :date
            ORDER BY m.commodity ASC, m.market ASC
            """)
    List<MarketPrice> findByDistrictAndDate(
            @Param("district") String district,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.market) = LOWER(:market)
            AND m.arrivalDate = :date
            ORDER BY m.commodity ASC
            """)
    List<MarketPrice> findByMarketAndDate(
            @Param("market") String market,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND m.arrivalDate BETWEEN :fromDate AND :toDate
            ORDER BY m.arrivalDate ASC
            """)
    List<MarketPrice> findByCommodityAndDateRange(
            @Param("commodity") String commodity,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND m.arrivalDate BETWEEN :fromDate AND :toDate
            ORDER BY m.arrivalDate ASC
            """)
    List<MarketPrice> findByCommodityStateAndDateRange(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND LOWER(m.district) = LOWER(:district)
            AND m.arrivalDate BETWEEN :fromDate AND :toDate
            ORDER BY m.arrivalDate ASC
            """)
    List<MarketPrice> findByCommodityStateDistrictAndDateRange(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("district") String district,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT m
            FROM MarketPrice m
            WHERE LOWER(m.commodity) = LOWER(:commodity)
            AND LOWER(m.state) = LOWER(:state)
            AND LOWER(m.district) = LOWER(:district)
            AND LOWER(m.market) = LOWER(:market)
            AND m.arrivalDate BETWEEN :fromDate AND :toDate
            ORDER BY m.arrivalDate ASC
            """)
    List<MarketPrice> findByMarketAndDateRange(
            @Param("commodity") String commodity,
            @Param("state") String state,
            @Param("district") String district,
            @Param("market") String market,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    boolean existsByCommodityAndStateAndDistrictAndMarketAndArrivalDate(
            String commodity,
            String state,
            String district,
            String market,
            LocalDate arrivalDate
    );
}
