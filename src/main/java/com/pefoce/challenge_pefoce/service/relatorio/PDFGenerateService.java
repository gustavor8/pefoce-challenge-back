package com.pefoce.challenge_pefoce.service.relatorio;

import com.pefoce.challenge_pefoce.dto.blockchain.CadeiaCustodiaTransferenciaDTO;
import com.pefoce.challenge_pefoce.dto.transferencia.TransferenciaDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PDFGenerateService {
  public byte[] gerarPdfCadeiaCustodia(CadeiaCustodiaTransferenciaDTO dadosRelatorio) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(baos);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    document.add(new Paragraph("Relatório de Cadeia de Custódia referente ao vestígio " + dadosRelatorio.dadosDoVestigio().id()).setBold().setFontSize(18));
    document.add(new Paragraph("Dados do Vestígio").setBold().setFontSize(14).setMarginTop(20));
    document.add(new Paragraph("ID: " + dadosRelatorio.dadosDoVestigio().id()));
    document.add(new Paragraph("Tipo: " + dadosRelatorio.dadosDoVestigio().tipo()));
    document.add(new Paragraph("Descrição: " + dadosRelatorio.dadosDoVestigio().descricao()));
    document.add(new Paragraph("Coletado em: " + dadosRelatorio.dadosDoVestigio().localColeta()));
    document.add(new Paragraph("Custódia Atual: " + dadosRelatorio.dadosDoVestigio().responsavelAtual().nome()));

    document.add(new Paragraph("Status de Integridade da Cadeia").setBold().setFontSize(14).setMarginTop(20));
    String statusIntegridade = dadosRelatorio.statusIntegridadeCadeia().valid() ? "ÍNTEGRA":"CORROMPIDA";
    document.add(new Paragraph("Validação: " + statusIntegridade));
    document.add(new Paragraph("Mensagem: " + dadosRelatorio.statusIntegridadeCadeia().message()));

    // Histórico de Transferências
    document.add(new Paragraph("Histórico de Transferências").setBold().setFontSize(14).setMarginTop(20));
    Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 3}));
    table.setWidth(UnitValue.createPercentValue(100));

    table.addHeaderCell(new Cell().add(new Paragraph("Data").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Origem").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Destino").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Motivo").setBold()));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    for (TransferenciaDTO t : dadosRelatorio.historicoDeTransferencias()) {
      table.addCell(t.dataTransferencia().format(formatter));
      table.addCell(t.responsavelOrigem().nome());
      table.addCell(t.responsavelDestino().nome());
      table.addCell(t.motivo()!=null ? t.motivo():"");
    }
    document.add(table);
    document.close();
    return baos.toByteArray();
  }
}