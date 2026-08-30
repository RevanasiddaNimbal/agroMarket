package com.agri.market.cropinfo.repository;

import com.agri.market.cropinfo.entity.CropInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CropInfoRepository extends JpaRepository<CropInfo, String> {

    Optional<CropInfo> findByCropNameIgnoreCase(String cropName);

    Optional<CropInfo> findByScientificNameIgnoreCase(String scientificName);
}