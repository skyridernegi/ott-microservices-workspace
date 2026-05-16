package com.ott.subscription.repository;

import com.ott.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Find all subscriptions for a user
    List<Subscription> findByUser_UserId(Long userId);

    // Find active subscriptions for a user
    List<Subscription> findByUser_UserIdAndStatus(Long userId, Subscription.SubscriptionStatus status);

    // Find subscriptions expiring today (for expiry notification job)
    List<Subscription> findByEndDateAndStatus(LocalDate endDate, Subscription.SubscriptionStatus status);

    // Find all subscriptions for a specific plan
    List<Subscription> findByPlanId(String planId);
}
