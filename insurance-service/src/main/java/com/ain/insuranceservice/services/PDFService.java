package com.ain.insuranceservice.services;

import com.ain.insuranceservice.models.InsurancePolicy;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PDFService {

    private static final Logger log = LoggerFactory.getLogger(PDFService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateInsurancePolicyPDF(InsurancePolicy policy) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Fonts
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Header
            document.add(new Paragraph("СТРАХОВОЙ ПОЛИС")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Policy Info Table
            Table policyTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
            policyTable.setWidth(UnitValue.createPercentValue(100));

            addTableRow(policyTable, "Номер полиса:", policy.getPolicyNumber(), boldFont, normalFont);
            addTableRow(policyTable, "Тип полиса:", getPolicyTypeDescription(policy.getPolicyType()), boldFont, normalFont);
            addTableRow(policyTable, "Страхователь:", policy.getPolicyHolder(), boldFont, normalFont);
            addTableRow(policyTable, "Страховая премия:", policy.getPremium() + " сом", boldFont, normalFont);
            addTableRow(policyTable, "Дата начала:", policy.getStartDate().format(DATE_FORMATTER), boldFont, normalFont);
            addTableRow(policyTable, "Дата окончания:", policy.getEndDate().format(DATE_FORMATTER), boldFont, normalFont);
            addTableRow(policyTable, "Статус:", policy.getStatus(), boldFont, normalFont);

            document.add(policyTable);
            document.add(new Paragraph("\n"));

            // Vehicle Owner Info
            document.add(new Paragraph("ДАННЫЕ СОБСТВЕННИКА ТС")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setMarginBottom(10));

            Table ownerTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
            if (policy.getVehicleOwner() != null) {
                addTableRow(ownerTable, "ФИО:", policy.getVehicleOwner().getFullName(), boldFont, normalFont);
                addTableRow(ownerTable, "Дата рождения:", policy.getVehicleOwner().getDateOfBirth().format(DATE_FORMATTER), boldFont, normalFont);
                addTableRow(ownerTable, "Телефон:", policy.getVehicleOwner().getPhoneNumber(), boldFont, normalFont);
                addTableRow(ownerTable, "Паспорт:", policy.getVehicleOwner().getPassport_number(), boldFont, normalFont);
                addTableRow(ownerTable, "ПИН:", policy.getVehicleOwner().getPin(), boldFont, normalFont);
                addTableRow(ownerTable, "Адрес:", policy.getVehicleOwner().getAddress(), boldFont, normalFont);
            }

            document.add(ownerTable);
            document.add(new Paragraph("\n"));

            // Vehicle Info
            document.add(new Paragraph("ДАННЫЕ ТРАНСПОРТНОГО СРЕДСТВА")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setMarginBottom(10));

            Table vehicleTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
            if (policy.getInsuredCar() != null) {
                addTableRow(vehicleTable, "Марка:", policy.getInsuredCar().getBrand(), boldFont, normalFont);
                addTableRow(vehicleTable, "Модель:", policy.getInsuredCar().getModel(), boldFont, normalFont);
                addTableRow(vehicleTable, "Год выпуска:", String.valueOf(policy.getInsuredCar().getManufactureYear()), boldFont, normalFont);
                addTableRow(vehicleTable, "VIN:", policy.getInsuredCar().getVin(), boldFont, normalFont);
                addTableRow(vehicleTable, "Гос. номер:", policy.getInsuredCar().getLicensePlate(), boldFont, normalFont);
                addTableRow(vehicleTable, "Тип ТС:", policy.getInsuredCar().getVehicleType().getDisplayName(), boldFont, normalFont);
            }

            document.add(vehicleTable);
            document.add(new Paragraph("\n"));

            // Drivers Info
            if (policy.getDrivers() != null && !policy.getDrivers().isEmpty()) {
                document.add(new Paragraph("ДОПУЩЕННЫЕ ВОДИТЕЛИ")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setMarginBottom(10));

                Table driversTable = new Table(UnitValue.createPercentArray(new float[]{25, 20, 30, 25}));
                driversTable.addHeaderCell(new Paragraph("ФИО").setFont(boldFont));
                driversTable.addHeaderCell(new Paragraph("Дата рождения").setFont(boldFont));
                driversTable.addHeaderCell(new Paragraph("Номер прав").setFont(boldFont));
                driversTable.addHeaderCell(new Paragraph("Стаж вождения").setFont(boldFont));

                policy.getDrivers().forEach(driver -> {
                    driversTable.addCell(new Paragraph(driver.getFullName()).setFont(normalFont));
                    driversTable.addCell(new Paragraph(driver.getBirthDate().format(DATE_FORMATTER)).setFont(normalFont));
                    driversTable.addCell(new Paragraph(driver.getLicenseNumber()).setFont(normalFont));
                    driversTable.addCell(new Paragraph(driver.getDrivingExperience().format(DATE_FORMATTER)).setFont(normalFont));
                });

                document.add(driversTable);
            }

            // Footer
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Дата генерации: " + java.time.LocalDate.now().format(DATE_FORMATTER))
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error generating PDF for policy {}", policy.getPolicyNumber(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addTableRow(Table table, String label, String value, PdfFont labelFont, PdfFont valueFont) {
        table.addCell(new Paragraph(label).setFont(labelFont));
        table.addCell(new Paragraph(value != null ? value : "Не указано").setFont(valueFont));
    }

    private String getPolicyTypeDescription(com.ain.insuranceservice.models.PolicyType policyType) {
        return switch (policyType) {
            case OSAGO -> "Обязательное страхование автогражданской ответственности";
            case CASCO -> "Добровольное комплексное автострахование";
            case DSAGO -> "Добровольное страхование автогражданской ответственности";
        };
    }
}
