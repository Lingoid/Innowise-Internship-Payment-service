package com.innowise.paymentservice.paymentservice.Repository;

import com.innowise.paymentservice.paymentservice.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByOrderId(String orderId);
    List<Payment> findByUserId(String userId);
    List<Payment> findByStatusIn(List<String> statuses);
    List<Payment> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
