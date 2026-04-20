package com.aprendemosya.aprendemosya_api.domain.file.repository;

import com.aprendemosya.aprendemosya_api.domain.file.entity.FileAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {

    Optional<FileAsset> findByStorageKey(String storageKey);

    List<FileAsset> findAllByUserId(Long userId);
}
