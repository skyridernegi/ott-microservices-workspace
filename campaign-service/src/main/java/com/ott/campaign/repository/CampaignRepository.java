package com.ott.campaign.repository;
import com.ott.campaign.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Find all currently active and within date range campaigns
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true AND c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE")
    List<Campaign> findAllActiveCampaigns();

    Optional<Campaign> findByPromoCode(String promoCode);

    // Find campaigns applicable to a specific plan
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true AND c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE AND (c.eligiblePlans = 'ALL' OR c.eligiblePlans LIKE %:planId%)")
    List<Campaign> findCampaignsForPlan(@Param("planId") String planId);
}