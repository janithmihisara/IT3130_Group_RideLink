package com.ridelink.paymentservice.config;

import com.ridelink.paymentservice.model.Payment;
import com.ridelink.paymentservice.model.PaymentMethod;
import com.ridelink.paymentservice.model.PaymentStatus;
import com.ridelink.paymentservice.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(name = "payment.seed-demo-data", havingValue = "true")
public class DatabaseSeeder implements CommandLineRunner {

    private final PaymentRepository paymentRepository;

    public DatabaseSeeder(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (paymentRepository.count() == 0) {
            Payment payment = Payment.builder()
                    .rideId("ride-demo-101")
                    .passengerId("passenger-usr-1")
                    .driverId("driver-usr-1")
                    .amount(BigDecimal.valueOf(18.70))
                    .paymentMethod(PaymentMethod.CARD)
                    .status(PaymentStatus.SUCCESS)
                    .transactionId("TXN-SEED-998877")
                    .receiptNumber("RCPT-SEED-1001")
                    .paidAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);
            System.out.println(">>> [Payment Service] Seeded default payment record into ridelink_payment_db!");
        }
    }
}
