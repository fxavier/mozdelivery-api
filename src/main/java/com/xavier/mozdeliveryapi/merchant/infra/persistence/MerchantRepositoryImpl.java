package com.xavier.mozdeliveryapi.merchant.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.merchant.application.usecase.port.MerchantRepository;
import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Implementation of MerchantRepository using JPA.
 */
@Component
public class MerchantRepositoryImpl implements MerchantRepository {
    
    private final JpaMerchantRepository jpaRepository;
    
    public MerchantRepositoryImpl(JpaMerchantRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Merchant save(Merchant merchant) {
        MerchantEntity entity = jpaRepository.findById(merchant.getMerchantId().value())
            .map(existing -> {
                existing.updateFrom(merchant);
                return existing;
            })
            .orElse(new MerchantEntity(merchant));
        
        MerchantEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Merchant> findById(MerchantId id) {
        return jpaRepository.findById(id.value())
            .map(MerchantEntity::toDomain);
    }
    
    public List<Merchant> findAll() {
        return jpaRepository.findAll().stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public void delete(Merchant merchant) {
        jpaRepository.deleteById(merchant.getMerchantId().value());
    }
    
    public void deleteById(MerchantId id) {
        jpaRepository.deleteById(id.value());
    }
    
    @Override
    public boolean existsById(MerchantId id) {
        return jpaRepository.existsById(id.value());
    }
    
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public Optional<Merchant> findByBusinessName(String businessName) {
        return jpaRepository.findByBusinessName(businessName)
            .map(MerchantEntity::toDomain);
    }
    
    @Override
    public Optional<Merchant> findByContactEmail(String contactEmail) {
        return jpaRepository.findByContactEmail(contactEmail)
            .map(MerchantEntity::toDomain);
    }
    
    @Override
    public List<Merchant> findByVertical(Vertical vertical) {
        return jpaRepository.findByVertical(vertical).stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Merchant> findByStatus(MerchantStatus status) {
        return jpaRepository.findByStatus(status).stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Merchant> findByCity(String city) {
        return jpaRepository.findByCity(city).stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Merchant> findByCityAndVertical(String city, Vertical vertical) {
        return jpaRepository.findByCityAndVertical(city, vertical).stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Merchant> findAllActive() {
        return jpaRepository.findAllActive().stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Merchant> findAllPubliclyVisible() {
        return jpaRepository.findAllPubliclyVisible().stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Merchant> findPubliclyVisibleByCity(String city) {
        return jpaRepository.findPubliclyVisibleByCity(city).stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public List<Merchant> findAllPendingApproval() {
        return jpaRepository.findAllPendingApproval().stream()
            .map(MerchantEntity::toDomain)
            .toList();
    }
    
    @Override
    public boolean existsByBusinessName(String businessName) {
        return jpaRepository.existsByBusinessName(businessName);
    }
    
    @Override
    public boolean existsByContactEmail(String contactEmail) {
        return jpaRepository.existsByContactEmail(contactEmail);
    }
    
    @Override
    public long countByVertical(Vertical vertical) {
        return jpaRepository.countByVertical(vertical);
    }
    
    @Override
    public long countByStatus(MerchantStatus status) {
        return jpaRepository.countByStatus(status);
    }
    
    @Override
    public long countByCity(String city) {
        return jpaRepository.countByCity(city);
    }
}