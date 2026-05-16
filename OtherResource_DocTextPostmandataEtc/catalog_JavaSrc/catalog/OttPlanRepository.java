package com.ott.catalog.repository;

import com.ott.catalog.model.OttPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for OttPlan database operations.
 *
 * By extending JpaRepository, Spring automatically provides:
 * - findAll()         → SELECT * FROM ott_plans
 * - findById()        → SELECT * FROM ott_plans WHERE plan_id = ?
 * - save()            → INSERT or UPDATE
 * - deleteById()      → DELETE FROM ott_plans WHERE plan_id = ?
 *
 * No SQL needed for basic operations!
 */
@Repository
public interface OttPlanRepository extends JpaRepository<OttPlan, String> {

    /**
     * Find all active plans only.
     * Spring generates SQL: SELECT * FROM ott_plans WHERE is_active = true
     */
    List<OttPlan> findByIsActiveTrue();

    /**
     * Find plans by duration type (MONTHLY or YEARLY)
     * Spring generates SQL: SELECT * FROM ott_plans WHERE duration = ? AND is_active = true
     */
    List<OttPlan> findByDurationAndIsActiveTrue(OttPlan.Duration duration);
}
