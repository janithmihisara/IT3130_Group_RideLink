package com.ridelink.paymentservice.repository;

import com.ridelink.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByRideId(String rideId);
    Optional<Payment> findByReceiptNumber(String receiptNumber);
    List<Payment> findByPassengerId(String passengerId);
    List<Payment> findByDriverId(String driverId);
}
