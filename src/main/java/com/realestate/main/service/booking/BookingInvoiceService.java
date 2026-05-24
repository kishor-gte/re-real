package com.realestate.main.service.booking;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.realestate.main.config.BookingProperties;
import com.realestate.main.entity.BookingInvoice;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.User;
import com.realestate.main.repository.BookingInvoiceRepository;

@Service
public class BookingInvoiceService {

	private final BookingInvoiceRepository invoiceRepository;
	private final BookingProperties bookingProperties;

	public BookingInvoiceService(BookingInvoiceRepository invoiceRepository, BookingProperties bookingProperties) {
		this.invoiceRepository = invoiceRepository;
		this.bookingProperties = bookingProperties;
	}

	@Transactional
	public BookingInvoice generate(PropertyBooking booking, Property property, User user) throws IOException {
		String invoiceNumber = "INV-" + booking.getBookingCode();
		Path dir = Paths.get(bookingProperties.getInvoiceDir());
		Files.createDirectories(dir);
		String fileName = invoiceNumber + ".pdf";
		Path filePath = dir.resolve(fileName);

		writePdf(filePath, booking, property, user, invoiceNumber);

		BookingInvoice invoice = invoiceRepository.findByBookingId(booking.getId()).orElse(new BookingInvoice());
		invoice.setInvoiceNumber(invoiceNumber);
		invoice.setBookingId(booking.getId());
		invoice.setFilePath("/" + bookingProperties.getInvoiceDir() + "/" + fileName);
		invoice.setSubtotal(booking.getBasePrice());
		invoice.setGstAmount(booking.getGstAmount());
		invoice.setTotalAmount(booking.getTotalAmount());
		invoice.setIssuedAt(LocalDateTime.now());
		return invoiceRepository.save(invoice);
	}

	private void writePdf(Path filePath, PropertyBooking booking, Property property, User user, String invoiceNumber)
			throws IOException {
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, Files.newOutputStream(filePath));
			document.open();
			Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
			Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);
			document.add(new Paragraph("EstateVault — Booking Invoice", title));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Invoice: " + invoiceNumber, normal));
			document.add(new Paragraph("Booking: " + booking.getBookingCode(), normal));
			document.add(new Paragraph("Date: "
					+ booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")), normal));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Buyer: " + user.getFullName() + " (" + user.getEmail() + ")", normal));
			document.add(new Paragraph("Property: " + property.getTitle() + " [" + property.getPropertyCode() + "]",
					normal));
			document.add(new Paragraph("Location: " + property.getLocality() + ", " + property.getCity(), normal));
			document.add(new Paragraph(" "));
			addLine(document, "Base price", booking.getBasePrice(), normal);
			addLine(document, "GST", booking.getGstAmount(), normal);
			addLine(document, "Registration", booking.getRegistrationCharges(), normal);
			addLine(document, "Booking charges", booking.getBookingCharges(), normal);
			addLine(document, "Discount", booking.getDiscountAmount().negate(), normal);
			document.add(new Paragraph(" "));
			addLine(document, "Total amount", booking.getTotalAmount(), title);
			addLine(document, "Paid now", booking.getPaidAmount(), normal);
			addLine(document, "Remaining", booking.getRemainingAmount(), normal);
			document.add(new Paragraph("Payment type: " + booking.getPaymentType(), normal));
			if (booking.getTransactionId() != null) {
				document.add(new Paragraph("Transaction: " + booking.getTransactionId(), normal));
			}
		} catch (DocumentException e) {
			throw new IOException("Failed to generate invoice PDF", e);
		} finally {
			document.close();
		}
	}

	private static void addLine(Document document, String label, BigDecimal amount, Font font) throws DocumentException {
		document.add(new Paragraph(label + " : ₹" + amount, font));
	}
}
