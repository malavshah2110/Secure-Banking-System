package org.asu.group8.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsTransaction;
import org.asu.group8.entity.SbsUser;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatementPdfView extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model,
                                    Document document, PdfWriter writer, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {

        System.out.println(model);
        SbsUser sbsUser = (SbsUser) model.get("sbsUser");
        List<SbsAccount> sbsAccounts = (List<SbsAccount>) model.get("sbsAccounts");
        List<List<SbsTransaction>> sbsTransactionsList = (List<List<SbsTransaction>>) model.get("sbsTransactions");

        Font headFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 12f);
        Font cellFont = FontFactory.getFont(FontFactory.TIMES, 10f);
        Font redFont = FontFactory.getFont(FontFactory.TIMES, 10f, BaseColor.RED);

        // add a header
        Paragraph header = new Paragraph();
        header.add(new Paragraph(" "));
        Paragraph title = new Paragraph("Banking Statements", headFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        header.add(new Paragraph(" "));
        if (sbsUser != null) {
            header.add(new Paragraph("Username: " + sbsUser.getUsername() + " (" + sbsUser.getUserType() + ")", cellFont));
            header.add(new Paragraph(" "));
        }
        document.add(header);

        if (sbsAccounts == null || sbsAccounts.isEmpty()) {

            // no transactions message
            Paragraph accountHeader = new Paragraph();
            document.add(new Paragraph("No Accounts", redFont));
            accountHeader.add(new Paragraph(" "));
            document.add(accountHeader);

        } else {
            for (int accountI = 0; accountI < sbsAccounts.size(); accountI++) {
                SbsAccount sbsAccount = sbsAccounts.get(accountI);

                // add an account header
                Paragraph accountHeader = new Paragraph();
                document.add(new Paragraph("Account Number: " + sbsAccount.getAccountNumber(), headFont));
                accountHeader.add(new Paragraph(" "));
                document.add(accountHeader);

                List<SbsTransaction> sbsTransactions = sbsTransactionsList.get(accountI);

                if (sbsTransactions == null || sbsTransactions.isEmpty()) {

                    // no transactions message
                    Paragraph transactionHeader = new Paragraph();
                    document.add(new Paragraph("No Transactions", redFont));
                    transactionHeader.add(new Paragraph(" "));
                    document.add(transactionHeader);

                } else {

                    PdfPTable table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new int[] {1, 3, 1, 1, 3, 1});

                    PdfPCell hcell;
                    hcell = new PdfPCell(new Phrase("Type", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    hcell = new PdfPCell(new Phrase("Time", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    hcell = new PdfPCell(new Phrase("From", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    hcell = new PdfPCell(new Phrase("To", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    hcell = new PdfPCell(new Phrase("Amount", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    hcell = new PdfPCell(new Phrase("Initiated By", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    for (SbsTransaction sbsTransaction : sbsTransactions) {

                        hcell = new PdfPCell(new Phrase(sbsTransaction.getType(), cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                        hcell = new PdfPCell(new Phrase(sbsTransaction.getTimestamp().toString(), cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                        hcell = new PdfPCell(new Phrase(
                                (sbsTransaction.getFromAccount() == null ? "---" : sbsTransaction.getFromAccount() + ""),
                                cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                        hcell = new PdfPCell(new Phrase(
                                (sbsTransaction.getToAccount() == null ? "---" : sbsTransaction.getToAccount() + ""),
                                cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                        hcell = new PdfPCell(new Phrase(sbsTransaction.getAmount().toString(), cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                        hcell = new PdfPCell(new Phrase(sbsTransaction.getUsername(), cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                    }

                    document.add(table);
                }

            }
        }

    }
}
