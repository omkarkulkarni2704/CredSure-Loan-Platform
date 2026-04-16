package com.credsure.controller;

import com.credsure.entity.LoanApplication;
import com.credsure.repository.LoanApplicationRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PdfController {

    @Autowired
    private LoanApplicationRepository repository;

    @GetMapping("/download-report")
    public void downloadReport(HttpServletResponse response) throws Exception {

        LoanApplication app = repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                                        .stream()
                                        .findFirst()
                                        .orElse(null);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=CredSure-Loan-Report.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font title = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.BLUE);
        Font heading = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font normal = new Font(Font.FontFamily.HELVETICA, 13);

        document.add(new Paragraph("CredSure Loan Report", title));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Generated Loan Analysis Report", normal));
        document.add(new Paragraph(" "));

        if(app != null){

            document.add(new Paragraph("Applicant Details", heading));
            document.add(new Paragraph("Full Name: " + app.getFullName(), normal));
            document.add(new Paragraph("City: " + app.getCity(), normal));
            document.add(new Paragraph("Employment Type: " + app.getEmploymentType(), normal));
            document.add(new Paragraph("Monthly Income: ₹" + app.getSalary(), normal));
            document.add(new Paragraph("Existing EMI: ₹" + app.getExistingEmi(), normal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Loan Details", heading));
            document.add(new Paragraph("Loan Type: " + app.getLoanType(), normal));
            document.add(new Paragraph("Requested Amount: ₹" + app.getLoanAmount(), normal));
            document.add(new Paragraph("Application Status: " + app.getStatus(), normal));
            document.add(new Paragraph("Risk Score: " + app.getRiskScore() + "/100", normal));
            document.add(new Paragraph(" "));

            String bankName = "Axis Bank";
            String interestRate = "10.5%";

            if(app.getRiskScore() >= 80){
                bankName = "HDFC Bank";
                interestRate = "8.5%";
            } else if(app.getRiskScore() >= 60){
                bankName = "ICICI Bank";
                interestRate = "9.2%";
            }

            double emi = app.getLoanAmount() / 36;

            document.add(new Paragraph("Recommended Bank Offer", heading));
            document.add(new Paragraph("Bank Name: " + bankName, normal));
            document.add(new Paragraph("Interest Rate: " + interestRate, normal));
            document.add(new Paragraph("Estimated EMI (36 Months): ₹" + Math.round(emi), normal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "This loan recommendation is generated automatically by CredSure based on applicant income, employment stability, EMI burden and requested loan amount.",
                    normal));
        }

        document.close();
    }
}