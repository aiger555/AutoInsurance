package com.ain.insuranceservice.services;

import com.ain.insuranceservice.models.InsurancePolicy;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PDFService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Маппинг кириллицы на латиницу
    private static final Map<Character, String> CYRILLIC_TO_LATIN = new HashMap<>();

    static {
        // Русские буквы
        CYRILLIC_TO_LATIN.put('а', "a"); CYRILLIC_TO_LATIN.put('б', "b"); CYRILLIC_TO_LATIN.put('в', "v");
        CYRILLIC_TO_LATIN.put('г', "g"); CYRILLIC_TO_LATIN.put('д', "d"); CYRILLIC_TO_LATIN.put('е', "e");
        CYRILLIC_TO_LATIN.put('ё', "yo"); CYRILLIC_TO_LATIN.put('ж', "zh"); CYRILLIC_TO_LATIN.put('з', "z");
        CYRILLIC_TO_LATIN.put('и', "i"); CYRILLIC_TO_LATIN.put('й', "y"); CYRILLIC_TO_LATIN.put('к', "k");
        CYRILLIC_TO_LATIN.put('л', "l"); CYRILLIC_TO_LATIN.put('м', "m"); CYRILLIC_TO_LATIN.put('н', "n");
        CYRILLIC_TO_LATIN.put('о', "o"); CYRILLIC_TO_LATIN.put('п', "p"); CYRILLIC_TO_LATIN.put('р', "r");
        CYRILLIC_TO_LATIN.put('с', "s"); CYRILLIC_TO_LATIN.put('т', "t"); CYRILLIC_TO_LATIN.put('у', "u");
        CYRILLIC_TO_LATIN.put('ф', "f"); CYRILLIC_TO_LATIN.put('х', "kh"); CYRILLIC_TO_LATIN.put('ц', "ts");
        CYRILLIC_TO_LATIN.put('ч', "ch"); CYRILLIC_TO_LATIN.put('ш', "sh"); CYRILLIC_TO_LATIN.put('щ', "shch");
        CYRILLIC_TO_LATIN.put('ъ', ""); CYRILLIC_TO_LATIN.put('ы', "y"); CYRILLIC_TO_LATIN.put('ь', "");
        CYRILLIC_TO_LATIN.put('э', "e"); CYRILLIC_TO_LATIN.put('ю', "yu"); CYRILLIC_TO_LATIN.put('я', "ya");

        // Заглавные буквы
        CYRILLIC_TO_LATIN.put('А', "A"); CYRILLIC_TO_LATIN.put('Б', "B"); CYRILLIC_TO_LATIN.put('В', "V");
        CYRILLIC_TO_LATIN.put('Г', "G"); CYRILLIC_TO_LATIN.put('Д', "D"); CYRILLIC_TO_LATIN.put('Е', "E");
        CYRILLIC_TO_LATIN.put('Ё', "Yo"); CYRILLIC_TO_LATIN.put('Ж', "Zh"); CYRILLIC_TO_LATIN.put('З', "Z");
        CYRILLIC_TO_LATIN.put('И', "I"); CYRILLIC_TO_LATIN.put('Й', "Y"); CYRILLIC_TO_LATIN.put('К', "K");
        CYRILLIC_TO_LATIN.put('Л', "L"); CYRILLIC_TO_LATIN.put('М', "M"); CYRILLIC_TO_LATIN.put('Н', "N");
        CYRILLIC_TO_LATIN.put('О', "O"); CYRILLIC_TO_LATIN.put('П', "P"); CYRILLIC_TO_LATIN.put('Р', "R");
        CYRILLIC_TO_LATIN.put('С', "S"); CYRILLIC_TO_LATIN.put('Т', "T"); CYRILLIC_TO_LATIN.put('У', "U");
        CYRILLIC_TO_LATIN.put('Ф', "F"); CYRILLIC_TO_LATIN.put('Х', "Kh"); CYRILLIC_TO_LATIN.put('Ц', "Ts");
        CYRILLIC_TO_LATIN.put('Ч', "Ch"); CYRILLIC_TO_LATIN.put('Ш', "Sh"); CYRILLIC_TO_LATIN.put('Щ', "Shch");
        CYRILLIC_TO_LATIN.put('Ъ', ""); CYRILLIC_TO_LATIN.put('Ы', "Y"); CYRILLIC_TO_LATIN.put('Ь', "");
        CYRILLIC_TO_LATIN.put('Э', "E"); CYRILLIC_TO_LATIN.put('Ю', "Yu"); CYRILLIC_TO_LATIN.put('Я', "Ya");
    }

    public byte[] generateInsurancePolicyPDF(InsurancePolicy policy) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Шрифты
            PdfFont boldFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            // ===== HEADER =====
            Paragraph header = new Paragraph("INSURANCE POLICY")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(header);

            // ===== POLICY INFO SECTION =====
            document.add(createSectionHeader("POLICY INFORMATION", boldFont));

            Table policyTable = createStyledTable(new float[]{40, 60});

            addTableRow(policyTable, "Policy Number:", transliterate(safeGet(policy.getPolicyNumber())), boldFont, normalFont);
            addTableRow(policyTable, "Policy Type:", getPolicyTypeDisplayName(policy.getPolicyType()), boldFont, normalFont);
            addTableRow(policyTable, "Policy Holder:", transliterate(safeGet(policy.getPolicyHolder())), boldFont, normalFont);
            addTableRow(policyTable, "Premium:", formatPremium(policy.getPremium()), boldFont, normalFont);
            addTableRow(policyTable, "Start Date:", formatDate(policy.getStartDate()), boldFont, normalFont);
            addTableRow(policyTable, "End Date:", formatDate(policy.getEndDate()), boldFont, normalFont);
            addTableRow(policyTable, "Status:", transliterate(safeGet(policy.getStatus())), boldFont, normalFont);
            addTableRow(policyTable, "Commissioner:", safeGet(policy.getComissarNumber()), boldFont, normalFont);
            addTableRow(policyTable, "Company Number:", safeGet(policy.getCompanyNumber()), boldFont, normalFont);

            document.add(policyTable);
            document.add(new Paragraph(" "));

            // ===== VEHICLE OWNER SECTION =====
            if (policy.getVehicleOwner() != null) {
                document.add(createSectionHeader("VEHICLE OWNER", boldFont));

                Table ownerTable = createStyledTable(new float[]{40, 60});

                addTableRow(ownerTable, "Full Name:", transliterate(safeGet(policy.getVehicleOwner().getFullName())), boldFont, normalFont);
                addTableRow(ownerTable, "Birth Date:", formatDate(policy.getVehicleOwner().getDateOfBirth()), boldFont, normalFont);
                addTableRow(ownerTable, "Phone:", safeGet(policy.getVehicleOwner().getPhoneNumber()), boldFont, normalFont);
                addTableRow(ownerTable, "Passport:", safeGet(policy.getVehicleOwner().getPassport_number()), boldFont, normalFont);
                addTableRow(ownerTable, "PIN:", safeGet(policy.getVehicleOwner().getPin()), boldFont, normalFont);
                addTableRow(ownerTable, "Address:", transliterate(safeGet(policy.getVehicleOwner().getAddress())), boldFont, normalFont);

                document.add(ownerTable);
                document.add(new Paragraph(" "));
            }

            // ===== VEHICLE INFO SECTION =====
            if (policy.getInsuredCar() != null) {
                document.add(createSectionHeader("VEHICLE INFORMATION", boldFont));

                Table vehicleTable = createStyledTable(new float[]{40, 60});

                addTableRow(vehicleTable, "Brand:", transliterate(safeGet(policy.getInsuredCar().getBrand())), boldFont, normalFont);
                addTableRow(vehicleTable, "Model:", transliterate(safeGet(policy.getInsuredCar().getModel())), boldFont, normalFont);
                addTableRow(vehicleTable, "Year:", safeGet(String.valueOf(policy.getInsuredCar().getManufactureYear())), boldFont, normalFont);
                addTableRow(vehicleTable, "VIN:", safeGet(policy.getInsuredCar().getVin()), boldFont, normalFont);
                addTableRow(vehicleTable, "License Plate:", safeGet(policy.getInsuredCar().getLicensePlate()), boldFont, normalFont);
                addTableRow(vehicleTable, "Vehicle Type:", getVehicleTypeDisplayName(policy.getInsuredCar().getVehicleType()), boldFont, normalFont);
                addTableRow(vehicleTable, "Registration Authority:", transliterate(safeGet(policy.getInsuredCar().getRegistrationAuthority())), boldFont, normalFont);
                addTableRow(vehicleTable, "Registration Date:", formatDate(policy.getInsuredCar().getRegistrationDate()), boldFont, normalFont);
                addTableRow(vehicleTable, "Tech Passport:", safeGet(policy.getInsuredCar().getTechPassportNumber()), boldFont, normalFont);

                // Specific fields
                if (policy.getInsuredCar().getEngineVolume() != null) {
                    addTableRow(vehicleTable, "Engine Volume:", policy.getInsuredCar().getEngineVolume() + " L", boldFont, normalFont);
                }
                if (policy.getInsuredCar().getBatteryCapacity() != null) {
                    addTableRow(vehicleTable, "Battery Capacity:", policy.getInsuredCar().getBatteryCapacity() + " kWh", boldFont, normalFont);
                }
                if (policy.getInsuredCar().getMaxAllowedWeight() != null) {
                    addTableRow(vehicleTable, "Max Weight:", policy.getInsuredCar().getMaxAllowedWeight() + " kg", boldFont, normalFont);
                }
                if (policy.getInsuredCar().getPassengerCapacity() != null) {
                    addTableRow(vehicleTable, "Passenger Capacity:", policy.getInsuredCar().getPassengerCapacity() + " passengers", boldFont, normalFont);
                }

                document.add(vehicleTable);
                document.add(new Paragraph(" "));
            }

            // ===== DRIVERS SECTION =====
            if (policy.getDrivers() != null && !policy.getDrivers().isEmpty()) {
                document.add(createSectionHeader("AUTHORIZED DRIVERS", boldFont));

                Table driversTable = new Table(UnitValue.createPercentArray(new float[]{35, 15, 25, 25}));
                driversTable.setWidth(UnitValue.createPercentValue(100));
                driversTable.setMarginBottom(10);

                // Table headers
                addHeaderCell(driversTable, "Full Name", boldFont);
                addHeaderCell(driversTable, "Birth Date", boldFont);
                addHeaderCell(driversTable, "License Number", boldFont);
                addHeaderCell(driversTable, "Driving Experience", boldFont);

                // Table data
                for (int i = 0; i < policy.getDrivers().size(); i++) {
                    var driver = policy.getDrivers().get(i);
                    addDataCell(driversTable, transliterate(safeGet(driver.getFullName())), normalFont);
                    addDataCell(driversTable, formatDate(driver.getBirthDate()), normalFont);
                    addDataCell(driversTable, safeGet(driver.getLicenseNumber()), normalFont);
                    addDataCell(driversTable, formatDate(driver.getDrivingExperience()), normalFont);
                }

                document.add(driversTable);
            }

            // ===== FOOTER =====
            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("Document generated: " + java.time.LocalDate.now().format(DATE_FORMATTER))
                    .setFont(normalFont)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setItalic();
            document.add(footer);

            document.close();

            log.info("PDF successfully generated for policy: {}", policy.getPolicyNumber());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF for policy {}", policy.getPolicyNumber(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    // ===== ТРАНСЛИТЕРАЦИЯ КИРИЛЛИЦЫ =====
    private String transliterate(String text) {
        if (text == null) return "Not specified";

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (CYRILLIC_TO_LATIN.containsKey(c)) {
                result.append(CYRILLIC_TO_LATIN.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    private Paragraph createSectionHeader(String text, PdfFont font) {
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(14)
                .setMarginBottom(10);
    }

    private Table createStyledTable(float[] columnWidths) {
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(15);
        return table;
    }

    private void addTableRow(Table table, String label, String value, PdfFont labelFont, PdfFont valueFont) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFont(labelFont).setFontSize(10))
                .setPadding(5)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(labelCell);

        Cell valueCell = new Cell()
                .add(new Paragraph(value).setFont(valueFont).setFontSize(10))
                .setPadding(5)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(valueCell);
    }

    private void addHeaderCell(Table table, String text, PdfFont font) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setPadding(6)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addHeaderCell(cell);
    }

    private void addDataCell(Table table, String text, PdfFont font) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setPadding(5)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(cell);
    }

    private String safeGet(String value) {
        return value != null ? value : "Not specified";
    }

    private String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "Not specified";
    }

    private String formatPremium(java.math.BigDecimal premium) {
        if (premium == null) return "Not specified";
        try {
            return String.format("%,.2f KGS", premium);
        } catch (Exception e) {
            return premium.toString() + " KGS";
        }
    }

    private String getPolicyTypeDisplayName(com.ain.insuranceservice.models.PolicyType policyType) {
        if (policyType == null) return "Not specified";
        return switch (policyType) {
            case OSAGO -> "OSAGO - Compulsory Insurance";
            case CASCO -> "CASCO - Comprehensive Insurance";
            case DSAGO -> "DSAGO - Voluntary Insurance";
        };
    }

    private String getVehicleTypeDisplayName(com.ain.insuranceservice.models.VehicleType vehicleType) {
        if (vehicleType == null) return "Not specified";
        return switch (vehicleType) {
            case PASSENGER_CAR -> "Passenger Car";
            case TRUCK -> "Truck";
            case ELECTRIC_CAR -> "Electric Car";
            case MOTORCYCLE -> "Motorcycle";
            case TRAILER -> "Trailer";
            case SEMI_TRAILER -> "Semi-Trailer";
            case BUS -> "Bus";
            case MINIBUS -> "Minibus";
            case SPECIAL_VEHICLE -> "Special Vehicle";
        };
    }
}