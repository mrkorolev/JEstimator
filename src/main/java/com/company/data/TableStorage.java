package com.company.data;

import com.company.*;
import com.company.formulas.Curves;
import com.company.formulas.Formulas;
import com.company.interfaces.Actions;
import com.company.interfaces.Calculations;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.Getter;
import java.awt.*;
import java.util.ArrayList;

@Getter
public class TableStorage {

    private static final ArrayList<ObjectZ> tableValues = new ArrayList<>();
    private final float x0;
    private final int rows;
    private final int columns;
    private final float stepRows;
    private final float stepColumns;

    public TableStorage(float x0, int rows, int columns, float stepRows, float stepColumns) {
        this.x0 = x0;
        this.stepRows = stepRows;
        this.stepColumns = stepColumns;
        this.rows = rows;
        this.columns = columns;
    }

    public void initializeTableValues(){
        for(int i = 0; i < this.rows; i++){
            float currentRow = (this.x0 + i*stepRows);
            for(int j = 0; j < columns; j++){
                float prob = Calculations.roundToDecimalPlaces(Calculations.computeArea(
                        Curves.NORMAL_PDF,
                        Formulas.SIMPSON,
                        x0, x0 + i*stepRows + j*stepColumns,
                        Main.arguments.getDivisions()), Main.arguments.getDecimalPlaces());     // CHANGE!!!

                tableValues.add(new ObjectZ(Calculations.roundToDecimalPlaces(currentRow, 1), Calculations.roundToDecimalPlaces(j*stepColumns, 2), prob));
            }
        }
    }

    public void generatePdfDescription(Document document) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLDITALIC);
        document.add(new Paragraph("JEstimator v. 1.0 NORMAL TABLE     Timestamp:   " + Actions.getTimestamp() + "\n", font));
        document.add(new Paragraph(Actions.listProvidedArguments(Main.arguments) + "\n\n", font));
    }

    public void generatePdfTableHeader(PdfPTable table){
        Font font = new Font(Font.HELVETICA, 10, Font.BOLDITALIC);
        PdfPCell cell = new PdfPCell();
        Color background = new Color(0xc8c4dc);
        cell.setBorderColor(Color.black);
        cell.setBorderWidth((float)1.5);
        cell.setBackgroundColor(background);

        cell.setPhrase(new Phrase("\n X\n \n", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        for(int i = 0; i < this.columns; i++){
            cell.setPhrase(new Phrase("\n " + Calculations.roundToDecimalPlaces(i * this.stepColumns, 2) + "\n \n", font));
            table.addCell(cell);
        }
        table.completeRow();
    }

    public void generatePdfTableContent(PdfPTable table, int decimalPlaces) {
        Font font = new Font(Font.HELVETICA, 8, Font.NORMAL);
        if(decimalPlaces > 6){
            font.setSize(4);
        }
        PdfPCell cell = createConfiguredCell();
        for(int k = 0; k < tableValues.size(); k++){
            ObjectZ current = tableValues.get(k);
            if(k % this.columns == 0){
                cell.setPhrase(new Phrase("" + current.getDecimal(), font));
                table.addCell(cell);
            }
            cell.setPhrase(new Phrase("" + current.getProbability(), font));
            table.addCell(cell);
            if((k + 1) % this.columns == 0){
                table.completeRow();
                cell = createConfiguredCell();
            }
        }
    }

    public PdfPCell createConfiguredCell(){
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderWidth((float)1.0);
        cell.setBackgroundColor(Color.white);
        return cell;
    }

    public String generateHeaderForConsole(int columns, float stepColumns){
        StringBuilder sb = new StringBuilder();
        sb.append("\n  X  |");
        for(int i = 0; i < columns; i++){
            sb.append("  " + Calculations.roundToDecimalPlaces(i*stepColumns, 2) + " |");
            if(i == 9) sb.append("\n");
        }
        sb.append("------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }

    public String readTable(){
        StringBuilder sb = new StringBuilder();
        for(int k = 0; k < tableValues.size(); k++){
            ObjectZ current = tableValues.get(k);

            if((k % (tableValues.size()/2) == 0)){
                sb.append(generateHeaderForConsole(columns, stepColumns));
            }
            if(k % columns == 0){
                sb.append(" " + current.getDecimal() + " |");
            }

            if(current.getProbability() == 0.0){
                sb.append(current.getProbability() + "   |");
            }else{
                sb.append(current.getProbability() + "|");
            }
            if((k + 1) % columns == 0){
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public int binarySearchForArea(int last, float area){
        int mid, low = 0, high = last - 1;
        while(low <= high){
            mid = (low + high)/2;
            float currArea = Calculations.roundToDecimalPlaces(tableValues.get(mid).getProbability(), 4);
            if(Math.abs(currArea - area) <= 0.001){
                return mid;
            }else if(currArea > area){
                high = mid - 1;
            }else{
                low = mid + 1;
            }
        }
        return -1;
    }

    public int binarySearchForX(int last, float x){
        int mid, low = 0, high = last - 1;
        while(low <= high){
            mid = (low + high)/2;
            float currX = tableValues.get(mid).totalNumber();
            if(currX == x){           //
                return mid;
            }else if(currX > x){
                high = mid - 1;
            }else{
                low = mid + 1;
            }
        }
        return -1;
    }

    public float computeCdf(float x) {
        int index = -2;
        float cdfArea;
        if(x > 0){
            index = binarySearchForX(tableValues.size(), x);
            cdfArea = (tableValues.get(index).getProbability() + 0.5f);
        }else{
            index = binarySearchForX(tableValues.size(), (-1)*x);
            cdfArea = (0.5f - tableValues.get(index).getProbability());
        }
        return cdfArea;
    }

    public static ArrayList<ObjectZ> getTableValues() {
        return tableValues;
    }
}
