package com.av.controller;

import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.av.model.Customer;
import com.av.model.Machinery;
import com.av.model.Sales;
import com.av.model.WorkingCapitol;
import com.av.repository.CustomerRepository;

@CrossOrigin(origins = "http://localhost:4200")
// @CrossOrigin(origins = "https://aadhivedha-be-10.onrender.com")

@RestController
@RequestMapping("/api")

public class ReportController {

  @Autowired
  CustomerRepository customerRepository;

  @GetMapping("/report/excel")
  public ResponseEntity<byte[]> downloadExcel(@RequestParam("code") int code) {

    try {
      Customer customer = customerRepository.findByCode(code);// 1183
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Customer Details");
        sheet.setColumnWidth(0, 20 * 256);
        Font bigFont = workbook.createFont();
        bigFont.setFontHeightInPoints((short) 20); // 14pt font size
        bigFont.setBold(true); // optional
        bigFont.setColor(IndexedColors.YELLOW.getIndex());
        Row header = sheet.createRow(0);
        CellStyle fieldStyle = workbook.createCellStyle();
        fieldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        fieldStyle.setAlignment(HorizontalAlignment.CENTER);
        fieldStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        fieldStyle.setFont(bigFont);
        Cell fieldHeader = header.createCell(0);
        fieldHeader.setCellValue("Personal Details");
        fieldHeader.setCellStyle(fieldStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        // Add customer details to rows
        Object[][] rows = {{"Shop Name", customer.getPersonal().getShopName()},
            {"Building Number", customer.getPersonal().getBuildingNumber()},
            {"Shop License Number", customer.getPersonal().getShopLicenseNumber()},
            {"Monthly Rent", customer.getPersonal().getMonthlyRent()},
            {"Village", customer.getPersonal().getVillage()},
            {"Panchayath", customer.getPersonal().getPanchayath()},
            {"Post Office", customer.getPersonal().getPostOffice()},
            {"Taluk", customer.getPersonal().getTaluk()},
            {"Block", customer.getPersonal().getBlock()},
            {"District", customer.getPersonal().getDistrict()},
            {"Pincode", customer.getPersonal().getPinCode()},
            {"gender As Per Aadhaar", customer.getPersonal().getGenderAsPerAadhaar()},
            {"Proprietor Name", customer.getPersonal().getProprietorName()},
            {"Relation Prefix", customer.getPersonal().getRelationPrefix()},
            {"Spouse Name", customer.getPersonal().getSpouseName()},
            {"House Name", customer.getPersonal().getHouseName()},
            {"Residence Village", customer.getPersonal().getResidenceVillage()},
            {"Residence Panchayath", customer.getPersonal().getResidencePanchayath()},
            {"Residence Post Office", customer.getPersonal().getResidencePostOffice()},
            {"Residence Taluk", customer.getPersonal().getResidenceTaluk()},
            {"Residence Block", customer.getPersonal().getResidenceBlock()},
            {"Residence District", customer.getPersonal().getResidenceDistrict()},
            {"Contact Number", customer.getPersonal().getContactNumber()},
            {"Residence Pincode", customer.getPersonal().getResidencePinCode()},
            {"Date Of Birth", customer.getPersonal().getDateOfBirth()},
            {"Passport Number", customer.getPersonal().getPassportNumber()},
            {"PAN Number", customer.getPersonal().getPanNumber()},
            {"Aadhaar Number", customer.getPersonal().getAadhaarNumber()},
            {"Line Of Activity", customer.getPersonal().getLineOfActivity()},
            {"Unit Status", customer.getPersonal().getUnitStatus()},
            {"Qualification", customer.getPersonal().getQualification()},
            {"Experience Years", customer.getPersonal().getExperienceYears()},
            {"Proposed Business", customer.getPersonal().getProposedBusiness()},
            {"Loan Scheme", customer.getPersonal().getLoanScheme()},
            {"Loan Term Years", customer.getPersonal().getLoanTermYears()},
            {"Bank Name", customer.getPersonal().getBankName()},
            {"Bank Branch", customer.getPersonal().getBankBranch()}};
        Font valueFont = workbook.createFont();
        valueFont.setFontHeightInPoints((short) 18); // 14pt font size
        valueFont.setBold(true); // optional
        valueFont.setColor(IndexedColors.BLACK.getIndex());
        int rowIdx = 1;
        for (Object[] rowData : rows) {
          Row row = sheet.createRow(rowIdx++);
          row.createCell(0).setCellValue(rowData[0].toString());
          row.getCell(0).setCellStyle(createPersonalCellStyle(workbook));
          row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : ""); // Handle
          row.getCell(1).setCellStyle(createPersonalCellStyle(workbook));
          row.getCell(1).getCellStyle().setFont(valueFont);
        }
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        Sheet sheet2 = createRowWithStyle(workbook);
        createMachinerySheet(sheet2, workbook, customer);
        createWCSheet(sheet2, workbook, customer);
        createSalesSheet(sheet2, workbook, customer);
        // Write to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType
            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer_details.xlsx");

        return ResponseEntity.ok().headers(headers).body(out.toByteArray());

        // return ResponseEntity.ok()
        // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer_details.xlsx")
        // .contentType(MediaType.APPLICATION_OCTET_STREAM).body(out.toByteArray());
      } catch (Exception e) {
        throw new RuntimeException("Error creating Excel", e);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private CellStyle createTitleStyle(Workbook workbook) {
    CellStyle titleStyle = workbook.createCellStyle();
    Font titleFont = workbook.createFont();
    titleFont.setFontHeightInPoints((short) 18);
    titleFont.setBold(true);
    titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
    titleStyle.setFont(titleFont);
    titleStyle.setAlignment(HorizontalAlignment.CENTER);
    titleStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
    // titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    // titleStyle.setBorderBottom(BorderStyle.THIN);
    // titleStyle.setBorderTop(BorderStyle.THIN);
    // titleStyle.setBorderLeft(BorderStyle.THIN);
    // titleStyle.setBorderRight(BorderStyle.THIN);
    return titleStyle;
  }



  private CellStyle createPersonalCellStyle(Workbook workbook) {
    CellStyle titleStyle = workbook.createCellStyle();
    Font titleFont = workbook.createFont();
    titleFont.setFontHeightInPoints((short) 16);
    titleFont.setBold(true);
    titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
    titleStyle.setFont(titleFont);
    titleStyle.setAlignment(HorizontalAlignment.LEFT);
    titleStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());

    return titleStyle;
  }



  private Sheet createRowWithStyle(Workbook workbook) {
    Sheet sheet = workbook.createSheet("WC | Machinery Details | Sales ");
    Row titleRow = sheet.createRow(0);
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("  Machinery Details ");

    CellStyle titleStyle = createTitleStyle(workbook);
    titleCell.setCellStyle(titleStyle);
    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

    Cell titleCell2 = titleRow.createCell(6);
    titleCell2.setCellValue("  Working Capital ");
    titleCell2.setCellStyle(titleStyle);
    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 6, 9));

    Cell titleCell3 = titleRow.createCell(11);
    titleCell3.setCellValue("Sales");
    titleCell3.setCellStyle(titleStyle);
    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 11, 14));
    return sheet;
  }

  private CellStyle createHeaderStyle(Workbook workbook) {
    CellStyle headerStyle = workbook.createCellStyle();
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerStyle.setFont(headerFont);
    headerStyle.setAlignment(HorizontalAlignment.CENTER);
    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    headerStyle.setAlignment(HorizontalAlignment.CENTER);
    // headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    headerStyle.setBorderBottom(BorderStyle.THIN);
    headerStyle.setBorderTop(BorderStyle.THIN);
    headerStyle.setBorderLeft(BorderStyle.THIN);
    headerStyle.setBorderRight(BorderStyle.THIN);

    return headerStyle;
  }

  private CellStyle setCellStyle(Workbook workbook) {
    Font cellFont = workbook.createFont();
    cellFont.setFontHeightInPoints((short) 16);
    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(cellFont);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    // cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    // cellStyle.setBorderBottom(BorderStyle.THIN);
    // cellStyle.setBorderTop(BorderStyle.THIN);
    // cellStyle.setBorderLeft(BorderStyle.THIN);
    // cellStyle.setBorderRight(BorderStyle.THIN);
    return cellStyle;
  }

  private void setFooterRow(Sheet sheet, Workbook workbook, int rowno, int cellIndex, String label,
      String value) {
    CellStyle boldStyle = setBoldStyle(workbook);
    Row totalRow = sheet.createRow(rowno);
    Cell totalLabel = totalRow.createCell(cellIndex);
    totalLabel.setCellValue(label);
    totalLabel.setCellStyle(boldStyle);
    Cell totalValue = totalRow.createCell(cellIndex + 1);
    // totalValue.setCellFormula("SUM(D2:D" + rowNum + ")");
    totalValue.setCellValue(value);
    totalValue.setCellStyle(boldStyle);
  }

  private void setFooterRowCommon(Sheet sheet, Workbook workbook, int rowno, int cellIndex,
      String label, String value) {
    CellStyle boldStyle = setBoldStyle(workbook);
    Row totalRow = sheet.getRow(rowno) == null ? sheet.createRow(rowno) : sheet.getRow(rowno);
    Cell totalLabel = totalRow.createCell(cellIndex);
    totalLabel.setCellValue(label);
    totalLabel.setCellStyle(boldStyle);
    Cell totalValue = totalRow.createCell(cellIndex + 1);
    // totalValue.setCellFormula("SUM(D2:D" + rowNum + ")");
    totalValue.setCellValue(value);
    totalValue.setCellStyle(boldStyle);
  }

  private CellStyle setBoldStyle(Workbook workbook) {
    CellStyle boldStyle = workbook.createCellStyle();
    Font boldFont = workbook.createFont();
    boldFont.setBold(true);
    boldStyle.setFont(boldFont);
    return boldStyle;
  }

  private void setHeaderRow(Sheet sheet, Workbook workbook, String[] columns) {
    Row headerRow = sheet.createRow(1);
    for (int i = 0; i < columns.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(columns[i]);
      cell.setCellStyle(createHeaderStyle(workbook));
    }
  }

  private void setHeaderRow(Sheet sheet, Workbook workbook, String[] columns, int startIndex) {
    Row headerRow = sheet.getRow(1);
    int coloumnCount = 0;
    for (int i = startIndex; i < columns.length + startIndex; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(columns[coloumnCount++]);
      cell.setCellStyle(createHeaderStyle(workbook));
    }
  }

  private Sheet createMachinerySheet(Sheet sheet, Workbook workbook, Customer customer) {
    try {
      // Sheet sheet = createRowWithStyle(workbook);
      String[] columns = {"Particular", "Rate", "Quantity", "Amount"};
      setHeaderRow(sheet, workbook, columns);
      int rowNum = 2;
      int totalAMount = 0;
      CellStyle cellStyle = setCellStyle(workbook);
      for (Machinery m : customer.getMachinery()) {
        // Row row = sheet.createRow(rowNum++);
        Row row =
            sheet.getRow(rowNum++) == null ? sheet.createRow(rowNum - 1) : sheet.getRow(rowNum - 1);
        if (null != row) {
          row.createCell(0).setCellValue(m.getParticular());
          row.createCell(1).setCellValue(m.getRate());
          row.createCell(2).setCellValue(m.getQty());
          row.createCell(3).setCellValue(m.getAmount());
          totalAMount += m.getAmount();
          for (int i = 0; i < 4; i++) {
            row.getCell(i).setCellStyle(cellStyle);
          }
        }
      }
      int totalRowNum = rowNum + 1;
      setFooterRow(sheet, workbook, totalRowNum, 2, "Total Amount:", String.valueOf(totalAMount));
      setFooterRow(sheet, workbook, totalRowNum + 1, 2, "GST (18%):",
          String.valueOf(totalAMount * 0.18));
      setFooterRow(sheet, workbook, totalRowNum + 2, 2, "Grand Total",
          String.valueOf(totalAMount + (totalAMount * 0.18)));
      for (int i = 0; i < columns.length; i++) {
        sheet.autoSizeColumn(i);
      }
      return sheet;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private Sheet createWCSheet(Sheet sheet, Workbook workbook, Customer customer) {
    try {
      // Sheet sheet = createRowWithStyle(workbook);
      String[] columns = {"Particular", "Box Rate", "Quantity", "Amount"};
      setHeaderRow(sheet, workbook, columns, 6);
      int rowNum = 2;
      int totalAMount = 0;
      CellStyle cellStyle = setCellStyle(workbook);
      for (WorkingCapitol m : customer.getWorkingCapitol()) {
        Row row =
            sheet.getRow(rowNum++) == null ? sheet.createRow(rowNum - 1) : sheet.getRow(rowNum - 1);
        if (null != row) {
          row.createCell(6).setCellValue(m.getParticular());
          row.createCell(7).setCellValue(m.getBoxrate());
          row.createCell(8).setCellValue(m.getQty());
          row.createCell(9).setCellValue(String.valueOf(m.getAmount()));
          totalAMount += m.getAmount();
          for (int i = 6; i < 10; i++) {
            row.getCell(i).setCellStyle(cellStyle);
            // sheet.setColumnWidth(9, 20 * 256); // column 2, width = 20 chars
          }
        }
        DataFormat format = workbook.createDataFormat();
        // row.getCell(9).getCellStyle().setDataFormat(format.getFormat("#,##0.00"));
      }
      int totalRowNum = rowNum + 1;
      setFooterRowCommon(sheet, workbook, totalRowNum, 8, "Total Amount:",
          String.valueOf(totalAMount));
      setFooterRowCommon(sheet, workbook, totalRowNum + 1, 8, "GST (18%):",
          String.valueOf(totalAMount * 0.18));
      setFooterRowCommon(sheet, workbook, totalRowNum + 2, 8, "Grand Total",
          String.valueOf(totalAMount + (totalAMount * 0.18)));
      for (int i = 8; i < columns.length + 8; i++) {
        sheet.autoSizeColumn(i);
      }
      return sheet;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private Sheet createSalesSheet(Sheet sheet, Workbook workbook, Customer customer) {
    try {
      // Sheet sheet = createRowWithStyle(workbook);
      String[] columns = {"Particular", "Rate", "Quantity", "Amount"};
      setHeaderRow(sheet, workbook, columns, 11);
      int rowNum = 2;
      int totalAMount = 0;
      CellStyle cellStyle = setCellStyle(workbook);
      for (Sales sales : customer.getSales()) {
        Row row =
            sheet.getRow(rowNum++) == null ? sheet.createRow(rowNum - 1) : sheet.getRow(rowNum - 1);
        if (null != row) {
          row.createCell(11).setCellValue(sales.getParticular());
          row.createCell(12).setCellValue(sales.getRate());
          row.createCell(13).setCellValue(sales.getQty());
          row.createCell(14).setCellValue(String.valueOf(sales.getAmount()));
          totalAMount += sales.getAmount();
          for (int i = 11; i < 15; i++) {
            row.getCell(i).setCellStyle(cellStyle);
          }
        }
      }
      int totalRowNum = rowNum + 1;
      setFooterRowCommon(sheet, workbook, totalRowNum, 13, "Total Amount:",
          String.valueOf(totalAMount));
      setFooterRowCommon(sheet, workbook, totalRowNum + 1, 13, "GST (18%):",
          String.valueOf(totalAMount * 0.18));
      setFooterRowCommon(sheet, workbook, totalRowNum + 2, 13, "Grand Total",
          String.valueOf(totalAMount + (totalAMount * 0.18)));
      for (int i = 13; i < columns.length + 13; i++) {
        sheet.autoSizeColumn(i);
      }
      return sheet;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


}
