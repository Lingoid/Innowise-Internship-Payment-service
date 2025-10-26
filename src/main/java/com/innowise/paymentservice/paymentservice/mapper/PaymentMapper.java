package com.innowise.paymentservice.paymentservice.mapper;

import com.innowise.paymentservice.paymentservice.dto.PaymentDTO;
import com.innowise.paymentservice.paymentservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    PaymentDTO toDto(Payment payment);
    Payment toEntity(PaymentDTO paymentDto);
}
