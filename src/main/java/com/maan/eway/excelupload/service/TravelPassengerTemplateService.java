package com.maan.eway.excelupload.service;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.dhatim.fastexcel.BorderSide;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Builds the travel passenger upload template using org.dhatim.fastexcel (writer) — NOT POI.
 *
 * fastexcel's writer has no API for data validation (dropdowns), freeze panes, or cell/sheet
 * protection. All three are done here by writing the base workbook with fastexcel, then
 * patching the raw OOXML directly: unzip the .xlsx, edit xl/worksheets/sheet1.xml,
 * xl/styles.xml and xl/workbook.xml as text, rezip. This works, but it is string-surgery on
 * a real file format rather than a library call — if fastexcel's internal output shape
 * changes (adds mergeCells/pageMargins/alignment on cells, etc.) these patches may need
 * re-checking against an actual generated file. Recommend opening one generated file in
 * Excel after any fastexcel version bump to confirm freeze/lock/dropdowns still work.
 *
 * What's locked vs editable:
 *  - SIno (A) and Category (F) stay locked (default — cells get locked=1 unless we say
 *    otherwise, so no extra work needed for these).
 *  - Relationship (E) on the very first data row is locked too (it's fixed to "Self").
 *  - First name, Last name, Gender, Relationship (rows after the first), DOB, Nationality,
 *    Passport no are unlocked so the user can actually type into them.
 *  - Sheet protection is turned on with no password — it's there to stop accidental edits
 *    to Category/SIno, not to be a real security boundary.
 */

@Service
public class TravelPassengerTemplateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String[] HEADERS = {
            "SINO", "First name", "Last name", "Gender", "Relationship",
            "Category", "DOB", "Nationality", "Passport no"
    };

    private static final int COL_SINO = 0;
    private static final int COL_RELATION = 4;
    private static final int COL_CATEGORY = 5;

    // Column letters, index-aligned with HEADERS
    private static final String[] COL_LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};

    private static final String LISTS_SHEET_NAME = "Lists";

    public byte[] generateTemplate(String requestReferenceNo) throws Exception {

        List<GroupRow> groups = fetchGroupDetails(requestReferenceNo);
        if (groups.isEmpty()) {
            throw new RuntimeException("No group details found for " + requestReferenceNo);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lastDataRow; // 0-indexed last row containing data

        try (Workbook wb = new Workbook(baos, "eway", "1.0")) {

            Worksheet sheet = wb.newWorksheet("Passengers");
            Worksheet lists = wb.newWorksheet(LISTS_SHEET_NAME);

            String[] maleRelations = RelationshipMaster.descArray(RelationshipMaster.MALE_RELATIONS);
            String[] femaleRelations = RelationshipMaster.descArray(RelationshipMaster.FEMALE_RELATIONS);
            for (int i = 0; i < maleRelations.length; i++) lists.value(i, 0, maleRelations[i]);
            for (int i = 0; i < femaleRelations.length; i++) lists.value(i, 1, femaleRelations[i]);

            for (int c = 0; c < HEADERS.length; c++) {
                sheet.value(0, c, HEADERS[c]);
                sheet.style(0, c).bold().fillColor("D9D9D9").set();
                border(sheet, 0, c, 0, c);
            }

            int rowIdx = 1;
            int sino = 1;
            boolean firstDataRowWritten = false;

            for (GroupRow g : groups) {
                for (int i = 0; i < g.groupMembers; i++) {
                    sheet.value(rowIdx, COL_SINO, sino++);
                    sheet.value(rowIdx, COL_CATEGORY, g.groupDesc);

                    if (!firstDataRowWritten) {
                        sheet.value(rowIdx, COL_RELATION, "Self");
                        firstDataRowWritten = true;
                    }

                    border(sheet, rowIdx, 0, rowIdx, HEADERS.length - 1);
                    rowIdx++;
                }
            }

            lastDataRow = rowIdx - 1;

            for (int c = 0; c < HEADERS.length; c++) sheet.width(c, 18);
        }

        byte[] base = baos.toByteArray();
        return patchWorkbook(base, lastDataRow);
        
        
//        return base;
    }

    private void border(Worksheet sheet, int r1, int c1, int r2, int c2) {
        sheet.range(r1, c1, r2, c2).style()
                .borderStyle(BorderSide.TOP, "thin")
                .borderStyle(BorderSide.BOTTOM, "thin")
                .borderStyle(BorderSide.LEFT, "thin")
                .borderStyle(BorderSide.RIGHT, "thin")
                .set();
    }

    // ---------------------------------------------------------------------
    // OOXML patch: freeze pane + unlock editable cells + dropdowns + protect
    // ---------------------------------------------------------------------

    private byte[] patchWorkbook(byte[] xlsxBytes, int lastDataRow) throws Exception {
    	
    	System.out.println("Length : " + xlsxBytes.length);

    	System.out.println(
    	    Integer.toHexString(xlsxBytes[0] & 0xFF) + " " +
    	    Integer.toHexString(xlsxBytes[1] & 0xFF) + " " +
    	    Integer.toHexString(xlsxBytes[2] & 0xFF) + " " +
    	    Integer.toHexString(xlsxBytes[3] & 0xFF)
    	);

        Map<String, byte[]> entries = unzip(xlsxBytes);

        String sheetXml = new String(entries.get("xl/worksheets/sheet1.xml"), StandardCharsets.UTF_8);
        String stylesXml = new String(entries.get("xl/styles.xml"), StandardCharsets.UTF_8);

        sheetXml = freezeHeaderRow(sheetXml);

        UnlockResult unlocked = unlockEditableCells(sheetXml, stylesXml, lastDataRow);
        sheetXml = unlocked.sheetXml;
        stylesXml = unlocked.stylesXml;
//
        sheetXml = injectSheetProtection(sheetXml);
        sheetXml = injectDataValidations(sheetXml, lastDataRow);

        entries.put("xl/worksheets/sheet1.xml", sheetXml.getBytes(StandardCharsets.UTF_8));
        entries.put("xl/styles.xml", stylesXml.getBytes(StandardCharsets.UTF_8));

        String workbookXml = new String(entries.get("xl/workbook.xml"), StandardCharsets.UTF_8);
        workbookXml = workbookXml.replace(
                "<sheet name=\"" + LISTS_SHEET_NAME + "\"",
                "<sheet name=\"" + LISTS_SHEET_NAME + "\" state=\"hidden\"");
        entries.put("xl/workbook.xml", workbookXml.getBytes(StandardCharsets.UTF_8));
        
        System.out.println("freezeHeaderRow executed");
        System.out.println(sheetXml);

        return rezip(entries);
    }

    /** Freezes row 1 (the header) so it stays visible while scrolling through passengers. */
    private String freezeHeaderRow(String sheetXml) {

        String oldTag = "<sheetView workbookViewId=\"0\"/>";

        String newTag =
                "<sheetView workbookViewId=\"0\">"
              + "<pane ySplit=\"1\" topLeftCell=\"A2\" activePane=\"bottomLeft\" state=\"frozen\"/>"
              + "<selection pane=\"bottomLeft\" activeCell=\"A2\" sqref=\"A2\"/>"
              + "</sheetView>";

        return sheetXml.replace(oldTag, newTag);
    }

    /** Adds sheetProtection right after sheetData closes — must come before dataValidations. */
    private String injectSheetProtection(String sheetXml) {
        String protection = "<sheetProtection sheet=\"1\" selectLockedCells=\"0\" "
                + "selectUnlockedCells=\"0\" password=\"\"/>";
        return sheetXml.replace("</sheetData>", "</sheetData>" + protection);
    }

    /**
     * For every editable column/row combo, finds that cell's current style index, clones the
     * matching <xf> in styles.xml with a <protection locked="0"/> child appended (caching by
     * original index so repeats reuse the same clone), and rewrites the cell's s= attribute
     * in sheet1.xml to point at the clone. SIno/Category (and the first row's fixed "Self")
     * are left untouched — they stay at the default locked=1.
     */
    private UnlockResult unlockEditableCells(String sheetXml, String stylesXml, int lastDataRow) {

        int firstRow1Indexed = 2;
        int lastRow1Indexed = lastDataRow + 1;

        Map<Integer, Integer> cloneCache = new LinkedHashMap<>(); // originalXf -> clonedXf

        for (int r = firstRow1Indexed; r <= lastRow1Indexed; r++) {
            for (int colIdx = 0; colIdx < COL_LETTERS.length; colIdx++) {
                boolean isSinoOrCategory = (colIdx == 0 || colIdx == COL_CATEGORY);
                boolean isFixedSelfRow = (r == firstRow1Indexed && colIdx == COL_RELATION);
                if (isSinoOrCategory || isFixedSelfRow) continue;

                String ref = COL_LETTERS[colIdx] + r;
                Matcher cellMatcher = Pattern.compile(
                        "<c r=\"" + ref + "\" s=\"(\\d+)\"").matcher(sheetXml);
                if (!cellMatcher.find()) continue; // cell wasn't materialized, nothing to unlock

                int originalXf = Integer.parseInt(cellMatcher.group(1));
                Integer clonedXf = cloneCache.get(originalXf);
                if (clonedXf == null) {
                    CloneResult cloneResult = cloneXfUnlocked(stylesXml, originalXf);
                    stylesXml = cloneResult.stylesXml;
                    clonedXf = cloneResult.newIndex;
                    cloneCache.put(originalXf, clonedXf);
                }
                sheetXml = sheetXml.replace(
                        "<c r=\"" + ref + "\" s=\"" + originalXf + "\"",
                        "<c r=\"" + ref + "\" s=\"" + clonedXf + "\"");
            }
        }

        return new UnlockResult(sheetXml, stylesXml);
    }

    private CloneResult cloneXfUnlocked(String stylesXml, int xfIndex) {
        int cellXfsStart = stylesXml.indexOf("<cellXfs");
        int openTagEnd = stylesXml.indexOf('>', cellXfsStart) + 1;
        int closeTag = stylesXml.indexOf("</cellXfs>", openTagEnd);

        String before = stylesXml.substring(0, closeTag);
        String after = stylesXml.substring(closeTag);

        Pattern xfPattern = Pattern.compile("<xf\\b[^>]*/>");
        Matcher m = xfPattern.matcher(stylesXml.substring(openTagEnd, closeTag));
        int count = 0;
        String targetXf = null;
        while (m.find()) {
            if (count == xfIndex) {
                targetXf = m.group();
                break;
            }
            count++;
        }
        if (targetXf == null) {
            // Couldn't find the source xf — bail out without modifying anything.
            return new CloneResult(stylesXml, xfIndex);
        }

        String cloned = targetXf.substring(0, targetXf.length() - 2) // strip trailing "/>"
                + " applyProtection=\"1\"><protection locked=\"0\"/></xf>";

        int newIndex = countXfs(stylesXml);
        String newStylesXml = before + cloned + after;

        // Bump the count="N" attribute on <cellXfs count="N">
        Pattern countPattern = Pattern.compile("(<cellXfs count=\")(\\d+)(\")");
        Matcher countMatcher = countPattern.matcher(newStylesXml);
        if (countMatcher.find()) {
            int oldCount = Integer.parseInt(countMatcher.group(2));
            newStylesXml = countMatcher.replaceFirst(
                    Matcher.quoteReplacement(countMatcher.group(1) + (oldCount + 1) + countMatcher.group(3)));
        }

        return new CloneResult(newStylesXml, newIndex);
    }

    private int countXfs(String stylesXml) {
        int cellXfsStart = stylesXml.indexOf("<cellXfs");
        int openTagEnd = stylesXml.indexOf('>', cellXfsStart) + 1;
        int closeTag = stylesXml.indexOf("</cellXfs>", openTagEnd);
        Matcher m = Pattern.compile("<xf\\b[^>]*/>").matcher(stylesXml.substring(openTagEnd, closeTag));
        int count = 0;
        while (m.find()) count++;
        return count;
    }

    private String injectDataValidations(String sheetXml, int lastDataRow) {

        int firstRow = 2;
        int lastRow = lastDataRow + 1;

        String genderCol = COL_LETTERS[3];
        String relationCol = COL_LETTERS[COL_RELATION];

        StringBuilder dv = new StringBuilder();
        int count = 0;

        dv.append("<dataValidation type=\"list\" allowBlank=\"1\" showErrorMessage=\"1\" sqref=\"")
          .append(genderCol).append(firstRow).append(":").append(genderCol).append(lastRow)
          .append("\">")
          .append("<formula1>\"M,F\"</formula1>")
          .append("</dataValidation>");

        count++;

        for (int r = firstRow + 1; r <= lastRow; r++) {

            dv.append("<dataValidation type=\"list\" allowBlank=\"1\" showErrorMessage=\"1\" sqref=\"")
              .append(relationCol).append(r)
              .append("\">")
              .append("<formula1>")
              .append("INDIRECT(IF(")
              .append(genderCol).append(r)
              .append("=&quot;M&quot;,&quot;")
              .append(LISTS_SHEET_NAME)
              .append("!$A$1:$A$6&quot;,&quot;")
              .append(LISTS_SHEET_NAME)
              .append("!$B$1:$B$6&quot;))")
              .append("</formula1>")
              .append("</dataValidation>");

            count++;
        }

        String block = "<dataValidations count=\"" + count + "\">"
                + dv
                + "</dataValidations>";

        if (sheetXml.contains("<pageMargins")) {
            return sheetXml.replace("<pageMargins",
                    block + "<pageMargins");
        }

        return sheetXml.replace("</worksheet>",
                block + "</worksheet>");
    }

    private Map<String, byte[]> unzip(byte[] xlsxBytes) throws Exception {

        Path temp = Files.createTempFile("travel-template", ".xlsx");
        Files.write(temp, xlsxBytes);

        Map<String, byte[]> entries = new LinkedHashMap<>();

        try (ZipFile zipFile = new ZipFile(temp.toFile())) {

            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

            while (enumeration.hasMoreElements()) {

                ZipEntry entry = enumeration.nextElement();

                System.out.println("Reading Entry : " + entry.getName());

                try (InputStream is = zipFile.getInputStream(entry)) {
                    entries.put(entry.getName(), is.readAllBytes());
                }
            }
        }

        Files.deleteIfExists(temp);

        return entries;
    }
    
    private byte[] rezip(Map<String, byte[]> entries) throws Exception {
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(zipOut)) {
            for (Map.Entry<String, byte[]> e : entries.entrySet()) {
                byte[] data = e.getValue();

                ZipEntry entry = new ZipEntry(e.getKey());
                // STORED (not the default streamed DEFLATE) so the local file header carries
                // the real size/CRC up front instead of deferring them to a data descriptor.
                // Some xlsx readers (including the fastexcel reader we parse uploads with)
                // read the declared size straight off the local header and choke if it's the
                // placeholder 0 a streamed DEFLATE entry writes there.
                entry.setMethod(ZipEntry.STORED);
                entry.setSize(data.length);
                entry.setCompressedSize(data.length);
                CRC32 crc = new CRC32();
                crc.update(data);
                entry.setCrc(crc.getValue());

                zos.putNextEntry(entry);
                zos.write(data);
                zos.closeEntry();
            }
        }
        return zipOut.toByteArray();
    }

    private List<GroupRow> fetchGroupDetails(String requestReferenceNo) {
        String sql = "SELECT GROUP_ID, GROUP_DESC, GROUP_MEMBERS FROM eservice_travel_group_details " +
                "WHERE REQUEST_REFERENCE_NO = ? ORDER BY GROUP_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GroupRow g = new GroupRow();
            g.groupId = rs.getInt("GROUP_ID");
            g.groupDesc = rs.getString("GROUP_DESC");
            g.groupMembers = rs.getInt("GROUP_MEMBERS");
            return g;
        }, requestReferenceNo);
    }

    private static class GroupRow {
        int groupId;
        String groupDesc;
        int groupMembers;
    }

    private static class UnlockResult {
        final String sheetXml;
        final String stylesXml;
        UnlockResult(String sheetXml, String stylesXml) {
            this.sheetXml = sheetXml;
            this.stylesXml = stylesXml;
        }
    }

    private static class CloneResult {
        final String stylesXml;
        final int newIndex;
        CloneResult(String stylesXml, int newIndex) {
            this.stylesXml = stylesXml;
            this.newIndex = newIndex;
        }
    }
}