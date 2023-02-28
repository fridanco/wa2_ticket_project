package it.polito.wa2.g17.travelerservice.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import org.springframework.stereotype.Component

@Component
class QRCodeGenerator {

    fun getQRCode(text: String, width: Int, height: Int) : ByteArray{
        val qrCodeWriter : QRCodeWriter = QRCodeWriter()
        var bitMatrix : BitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
        var imgOutStream : ByteArrayOutputStream = ByteArrayOutputStream()

        val conf : MatrixToImageConfig = MatrixToImageConfig()
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", imgOutStream, conf)

        val imgData : ByteArray = imgOutStream.toByteArray()
        return imgData
    }

}