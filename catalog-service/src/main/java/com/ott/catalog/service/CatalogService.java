package com.ott.catalog.service;

import com.ott.catalog.exception.PlanNotFoundException;
import com.ott.catalog.model.OttPlan;
import com.ott.catalog.repository.OttPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

	
@Service
public class CatalogService {

    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    // Spring automatically injects the repository (Dependency Injection) here we can use @autowired annotation or constructor injection as well
    private final OttPlanRepository ottPlanRepository;

    public CatalogService(OttPlanRepository ottPlanRepository) {
        this.ottPlanRepository = ottPlanRepository;
    }

    /**
     * Get all active OTT plans
     */
    public List<OttPlan> getAllActivePlans() {
        log.info("Fetching all active OTT plans from database");
        return ottPlanRepository.findByIsActiveTrue();
    }

    /**
     * Get a specific plan by ID
     * Throws PlanNotFoundException if not found → GlobalExceptionHandler catches it
     */
    public OttPlan getPlanById(String planId) {
        log.info("Fetching plan with id: {}", planId);
        return ottPlanRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));
    }

    /**
     * Get plans filtered by duration (MONTHLY or YEARLY)
     */
    public List<OttPlan> getPlansByDuration(OttPlan.Duration duration) {
        log.info("Fetching plans with duration: {}", duration);
        return ottPlanRepository.findByDurationAndIsActiveTrue(duration);
    }

    /**
     * Add a new OTT plan (Admin operation)
     */
    public OttPlan createPlan(OttPlan ottPlan) {
        log.info("Creating new plan: {}", ottPlan.getPlanId());
        return ottPlanRepository.save(ottPlan);
    }

    /**
     * Update an existing plan
     */
    public OttPlan updatePlan(String planId, OttPlan updatedPlan) {
        log.info("Updating plan: {}", planId);

        // First check if plan exists — throws exception if not
        OttPlan existingPlan = getPlanById(planId);

        existingPlan.setPlanName(updatedPlan.getPlanName());
        existingPlan.setPrice(updatedPlan.getPrice());
        existingPlan.setDuration(updatedPlan.getDuration());
        existingPlan.setDescription(updatedPlan.getDescription());
        existingPlan.setIsActive(updatedPlan.getIsActive());

        return ottPlanRepository.save(existingPlan);
    }

    /**
     * Soft delete - just marks plan as inactive, doesn't delete from DB
     * This is enterprise standard — never hard delete important data
     */
    public void deactivatePlan(String planId) {
        log.info("Deactivating plan: {}", planId);
        OttPlan plan = getPlanById(planId);
        plan.setIsActive(false);
        ottPlanRepository.save(plan);
    }
}
