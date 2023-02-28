package it.polito.wa2.g17.travelerservice.utils

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


@Component
class PDFGenerator {

    fun generatePDF(userName:String, ticketID:UUID, orderID: UUID, ticketType: String, iat: Date, expiry: Date, validFrom: String, imgData: ByteArray): InputStreamResource{
        val outStream = ByteArrayOutputStream()
        val document = Document()
        PdfWriter.getInstance(document, outStream)
        document.open()

        val fontIntro : Font = FontFactory.getFont(FontFactory.HELVETICA, 20.0f, BaseColor.BLUE)
        val chunk1 = Chunk("WA2G17 ticket QR CODE", fontIntro)
        val paragraphIntro= Paragraph(chunk1)
        paragraphIntro.alignment = Element.ALIGN_CENTER
        document.add(paragraphIntro)
        document.add(Chunk.NEWLINE)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

        val table = PdfPTable(2)
        table.setWidths(arrayOf(1f,2f).toFloatArray())
        table.widthPercentage = 100f // Width 100%
        table.setSpacingBefore(10f) // Space before table
        table.setSpacingAfter(10f) // Space after table
        table.defaultCell.borderWidth = 0f

        val cell1 = PdfPCell(Paragraph("Passenger:"))
        cell1.borderWidth = 0f
        cell1.paddingLeft = 105f
        cell1.paddingTop = 5f
        cell1.paddingBottom = 5f
        cell1.horizontalAlignment = Element.ALIGN_LEFT
        cell1.verticalAlignment = Element.ALIGN_CENTER

        val cell2 = PdfPCell(Paragraph(userName))
        cell2.borderWidth = 0f
        cell2.paddingLeft = 75f
        cell2.paddingTop = 5f
        cell2.paddingBottom = 5f
        cell2.horizontalAlignment = Element.ALIGN_LEFT
        cell2.verticalAlignment = Element.ALIGN_CENTER

        val cell3 = PdfPCell(Paragraph("Ticket ID:"))
        cell3.borderWidth = 0f
        cell3.paddingLeft = 105f
        cell3.paddingTop = 5f
        cell3.paddingBottom = 5f
        cell3.horizontalAlignment = Element.ALIGN_LEFT
        cell3.verticalAlignment = Element.ALIGN_CENTER

        val cell4 = PdfPCell(Paragraph(ticketID.toString()))
        cell4.borderWidth = 0f
        cell4.paddingLeft = 75f
        cell4.paddingTop = 5f
        cell4.paddingBottom = 5f
        cell4.horizontalAlignment = Element.ALIGN_LEFT
        cell4.verticalAlignment = Element.ALIGN_CENTER

        val cell5 = PdfPCell(Paragraph("Order ID:"))
        cell5.borderWidth = 0f
        cell5.paddingLeft = 105f
        cell5.paddingTop = 5f
        cell5.paddingBottom = 5f
        cell5.horizontalAlignment = Element.ALIGN_LEFT
        cell5.verticalAlignment = Element.ALIGN_CENTER

        val cell6 = PdfPCell(Paragraph(orderID.toString()))
        cell6.borderWidth = 0f
        cell6.paddingLeft = 75f
        cell6.paddingTop = 5f
        cell6.paddingBottom = 5f
        cell6.horizontalAlignment = Element.ALIGN_LEFT
        cell6.verticalAlignment = Element.ALIGN_CENTER

        val cell7 = PdfPCell(Paragraph("Ticket Type:"))
        cell7.borderWidth = 0f
        cell7.paddingLeft = 105f
        cell7.paddingTop = 5f
        cell7.paddingBottom = 5f
        cell7.horizontalAlignment = Element.ALIGN_LEFT
        cell7.verticalAlignment = Element.ALIGN_CENTER

        val cell8 = PdfPCell(Paragraph(ticketType))
        cell8.borderWidth = 0f
        cell8.paddingLeft = 75f
        cell8.paddingTop = 5f
        cell8.paddingBottom = 5f
        cell8.horizontalAlignment = Element.ALIGN_LEFT
        cell8.verticalAlignment = Element.ALIGN_CENTER

        val cell9 = PdfPCell(Paragraph("Issued at:"))
        cell9.borderWidth = 0f
        cell9.paddingLeft = 105f
        cell9.paddingTop = 5f
        cell9.paddingBottom = 5f
        cell9.horizontalAlignment = Element.ALIGN_LEFT
        cell9.verticalAlignment = Element.ALIGN_CENTER

        val cell10 = PdfPCell(Paragraph(sdf.format(iat)))
        cell10.borderWidth = 0f
        cell10.paddingLeft = 75f
        cell10.paddingTop = 5f
        cell10.paddingBottom = 5f
        cell10.horizontalAlignment = Element.ALIGN_LEFT
        cell10.verticalAlignment = Element.ALIGN_CENTER

        val cell11 = PdfPCell(Paragraph("Expires at:"))
        cell11.borderWidth = 0f
        cell11.paddingLeft = 105f
        cell11.paddingTop = 5f
        cell11.paddingBottom = 5f
        cell11.horizontalAlignment = Element.ALIGN_LEFT
        cell11.verticalAlignment = Element.ALIGN_CENTER

        val cell12 = PdfPCell(Paragraph(sdf.format(expiry)))
        cell12.borderWidth = 0f
        cell12.paddingLeft = 75f
        cell12.paddingTop = 5f
        cell12.paddingBottom = 5f
        cell12.horizontalAlignment = Element.ALIGN_LEFT
        cell12.verticalAlignment = Element.ALIGN_CENTER

        val cell13 = PdfPCell(Paragraph("Valid from:"))
        cell13.borderWidth = 0f
        cell13.paddingLeft = 105f
        cell13.paddingTop = 5f
        cell13.paddingBottom = 5f
        cell13.horizontalAlignment = Element.ALIGN_LEFT
        cell13.verticalAlignment = Element.ALIGN_CENTER

        val cell14 = PdfPCell(Paragraph(validFrom))
        cell14.borderWidth = 0f
        cell14.paddingLeft = 75f
        cell14.paddingTop = 5f
        cell14.paddingBottom = 5f
        cell14.horizontalAlignment = Element.ALIGN_LEFT
        cell14.verticalAlignment = Element.ALIGN_CENTER

        table.addCell(cell1)
        table.addCell(cell2)
        table.addCell(cell3)
        table.addCell(cell4)
        table.addCell(cell5)
        table.addCell(cell6)
        table.addCell(cell7)
        table.addCell(cell8)
        table.addCell(cell9)
        table.addCell(cell10)
        table.addCell(cell11)
        table.addCell(cell12)
        table.addCell(cell13)
        table.addCell(cell14)

        document.add(table)
        document.add(Chunk.NEWLINE)

        val image : Image = Image.getInstance(imgData)
        image.scaleAbsolute(120f, 120f)
        image.alignment = Element.ALIGN_CENTER

        document.add(image)
        document.close()
        val bis = ByteArrayInputStream(outStream.toByteArray())
        return InputStreamResource(bis)

    }
}