package com.ott.campaign.repository;
import com.ott.campaign.model.CampaignApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignApplicationRepository extends JpaRepository<CampaignApplication, Long> {
    boolean existsByCampaignIdAndUserId(Long campaignId, Long userId);
}