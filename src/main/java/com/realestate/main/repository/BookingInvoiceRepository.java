package com.realestate.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.main.entity.BookingInvoice;

public interface BookingInvoiceRepository extends JpaRepository<BookingInvoice, Long> {

	Optional<BookingInvoice> findByBookingId(Long bookingId);

	Optional<BookingInvoice> findByInvoiceNumber(String invoiceNumber);
}
