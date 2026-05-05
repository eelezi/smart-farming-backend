package com.timmk22.smartfarming.service.impl;

import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.service.HealthSummaryService;
import com.timmk22.smartfarming.service.PdfReportService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PdfReportServiceImpl implements PdfReportService {

    private static final float MARGIN = 48f;
    private static final float BODY_FONT_SIZE = 10.5f;
    private static final float SECTION_FONT_SIZE = 13f;
    private static final float TITLE_FONT_SIZE = 21f;

    private final HealthSummaryService healthSummaryService;
    private final String companyName;

    public PdfReportServiceImpl(
            HealthSummaryService healthSummaryService,
            @Value("${report.pdf.company-name:Smart Farming Analytics}") String companyName
    ) {
        this.healthSummaryService = healthSummaryService;
        this.companyName = companyName;
    }

    @Override
    public byte[] generateReportFromDatabase(List<PlantingInformation> plantings, String farmName, String reportDate, String preparedBy) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            LayoutWriter writer = new LayoutWriter(document);
            renderHeader(writer, farmName, reportDate, preparedBy);
            renderStatsTable(writer, plantings);
            renderAiSummary(writer, plantings, farmName);
            renderFooter(writer);
            writer.close();

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to generate PDF report", exception);
        }
    }

    private void renderHeader(LayoutWriter writer, String farmName, String reportDate, String preparedBy) throws IOException {
        writer.writeText(companyName, PDType1Font.HELVETICA_BOLD, 11f, new Color(58, 76, 106));
        writer.moveY(16f);

        writer.writeText("Farming Health Report", PDType1Font.HELVETICA_BOLD, TITLE_FONT_SIZE, Color.BLACK);
        writer.moveY(15f);

        writer.drawLine(new Color(210, 214, 220), 1f);
        writer.moveY(15f);

        writer.writeText(
                "Farm: " + farmName,
                PDType1Font.HELVETICA_BOLD,
                BODY_FONT_SIZE,
                Color.DARK_GRAY
        );
        writer.moveY(15f);

        writer.writeText(
                "Report Date: " + reportDate,
                PDType1Font.HELVETICA,
                BODY_FONT_SIZE,
                Color.DARK_GRAY
        );
        writer.moveY(15f);

        writer.writeText(
                "Prepared By: " + preparedBy,
                PDType1Font.HELVETICA,
                BODY_FONT_SIZE,
                Color.DARK_GRAY
        );
        writer.moveY(32f);
    }

    private void renderStatsTable(LayoutWriter writer, List<PlantingInformation> plantings) throws IOException {
        writer.writeText("Planting Information", PDType1Font.HELVETICA_BOLD, SECTION_FONT_SIZE, Color.BLACK);
        writer.moveY(12f);

        String tableHeader = String.format(
                Locale.US,
                "%-14s %-10s %7s %-12s %-12s %-12s",
                "Field",
                "Crop",
                "Acres",
                "Status",
                "Planting",
                "Harvest"
        );

        writer.writeText(tableHeader, PDType1Font.COURIER_BOLD, 9.5f, new Color(30, 30, 30));
        writer.moveY(7f);
        writer.drawLine(new Color(200, 205, 210), 0.8f);
        writer.moveY(9f);

        for (PlantingInformation planting : plantings) {
            String row = String.format(
                    Locale.US,
                    "%-14s %-10s %7.1f %-12s %-12s %-12s",
                    trimToLength(planting.getLocationName() != null ? planting.getLocationName() : "Field-" + planting.getPlantingId(), 14),
                    trimToLength(planting.getCrop().getName(), 10),
                    planting.getArea(),
                    trimToLength(planting.getCurrentStatus().toString(), 12),
                    planting.getPlantingDate(),
                    planting.getExpectedHarvestDate() != null ? planting.getExpectedHarvestDate().toString() : "N/A"
            );

            writer.writeText(row, PDType1Font.COURIER, 9.5f, Color.BLACK);
            writer.moveY(12.5f);
        }

        writer.moveY(16f);
    }

    private void renderAiSummary(LayoutWriter writer, List<PlantingInformation> plantings, String farmName) throws IOException {
        writer.writeText("AI-Generated Health Summary", PDType1Font.HELVETICA_BOLD, SECTION_FONT_SIZE, Color.BLACK);
        writer.moveY(10f);

        // Create a summary request from the plantings
        String summary = healthSummaryService.generateSummary(farmName, plantings);
        writer.writeWrappedText(summary, PDType1Font.HELVETICA, BODY_FONT_SIZE, Color.BLACK, 18f);
        writer.moveY(12f);
    }

    private void renderFooter(LayoutWriter writer) throws IOException {
        writer.drawLine(new Color(220, 224, 228), 0.8f);
        writer.moveY(9f);
        writer.writeText(
                "Generated by Smart Farming Backend - ready for digital sharing and professional printing.",
                PDType1Font.HELVETICA_OBLIQUE,
                8.8f,
                new Color(90, 90, 90)
        );
    }

    private String trimToLength(String value, int length) {
        if (value == null) {
            return "";
        }
        if (value.length() <= length) {
            return value;
        }
        return value.substring(0, length - 1) + "~";
    }

    private static final class LayoutWriter {

        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream contentStream;
        private float y;

        private LayoutWriter(PDDocument document) throws IOException {
            this.document = document;
            startPage();
        }

        private void startPage() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }

            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - MARGIN;
        }

        private void ensureSpace(float requiredHeight) throws IOException {
            if (y - requiredHeight < MARGIN) {
                startPage();
            }
        }

        private float usableWidth() {
            return page.getMediaBox().getWidth() - (2 * MARGIN);
        }

        private void moveY(float delta) throws IOException {
            ensureSpace(delta);
            y -= delta;
        }

        private void drawLine(Color color, float width) throws IOException {
            ensureSpace(3f);
            contentStream.setLineWidth(width);
            contentStream.setStrokingColor(color);
            contentStream.moveTo(MARGIN, y);
            contentStream.lineTo(MARGIN + usableWidth(), y);
            contentStream.stroke();
        }

        private void writeText(String text, PDFont font, float fontSize, Color color) throws IOException {
            ensureSpace(fontSize + 4f);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.newLineAtOffset(MARGIN, y);
            contentStream.showText(text);
            contentStream.endText();
        }

        private void writeWrappedText(
                String text,
                PDFont font,
                float fontSize,
                Color color,
                float lineSpacing
        ) throws IOException {
            for (String line : wrapText(text, font, fontSize, usableWidth())) {
                if (line.isEmpty()) {
                    moveY(lineSpacing);
                    continue;
                }
                writeText(line, font, fontSize, color);
                moveY(lineSpacing);
            }
        }

        private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
            List<String> lines = new ArrayList<>();
            String[] paragraphs = text.split("\n");

            for (String paragraph : paragraphs) {
                if (paragraph.trim().isEmpty()) {
                    lines.add("");
                    continue;
                }
                String[] words = paragraph.trim().split("\\s+");
                StringBuilder currentLine = new StringBuilder();

                for (String word : words) {
                    String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
                    float candidateWidth = font.getStringWidth(candidate) / 1000f * fontSize;

                    if (candidateWidth > maxWidth && !currentLine.isEmpty()) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else {
                        currentLine = new StringBuilder(candidate);
                    }
                }

                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                }
            }

            return lines;
        }

        private void close() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }
        }
    }

}
