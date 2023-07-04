package com.company.interfaces;

import com.beust.jcommander.JCommander;
import com.company.Main;
import com.company.data.TableStorage;
import com.company.formulas.Formulas;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

/*
    Class that represents the list of actions (and helper methods) for the software
 */
public class Actions {

    public static TableStorage storage;

    // Methods for UI in CLI, for general information and CLI interaction:
    public static String greet(){
        StringBuilder sb = new StringBuilder();
        sb.append("Welcome to JEstimator, v. 1.0    ");
        sb.append(getTimestamp());
        sb.append("JEstimator is used for confidence interval calculation\n");
        sb.append("Developed by: github.com/mrkorolev\n");
        sb.append("Java version: 17.0.5\n");
        return sb.toString();
    }

    public static String listProvidedArguments(Main.Args cmdArgs){
        StringBuilder sb = new StringBuilder();
        sb.append("Arguments\n");
        sb.append("Decimal places: " + cmdArgs.getDecimalPlaces() + "\n");
        sb.append("Number of divisions: " + cmdArgs.getDivisions() + "\n");
        sb.append("Numeric method: " + Main.method);
        return sb.toString();
    }

    public static int availableOperations(Main.Args cmdArgs){
        System.out.println(listProvidedArguments(cmdArgs));
        System.out.println("======================================================");
        System.out.println("Operations");
        System.out.println("(1) output to console");
        System.out.println("(2) output to PDF file");
        System.out.println("(3) compute CDF value");
        System.out.println("(4) confidence interval");
        System.out.println("(5) quit \n");
        System.out.print("Choose operation: ");
        Scanner sc = new Scanner(System.in);
        int input = sc.nextInt();
        System.out.println("========================");
        return input;
    }

    public static String getTimestamp(){
        StringBuilder sb = new StringBuilder();
        return sb.append(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss (dd.MM.yyyy)\n"))
        ).toString();
    }

    public static void outputToConsole(){
        System.out.println(storage.readTable());
    }

    /*
        PDF output methods
     */
    public static void outputToPdf(int decimalPlaces){
        Scanner sc = new Scanner(System.in);
        String filename = "-";
        while(true){
            System.out.print("Name for file:     ");
            filename = sc.nextLine();
            if(Objects.equals(filename, null) || filename.isEmpty()){
                System.out.println("Non-empty file name is accepted only!");
            }else{
                break;
            }
        }
        generatePdfTable(filename, decimalPlaces);
        System.out.println("File with name " + filename + ".pdf successfully created");
        System.out.println("======================================================\n");
    }

    public static void generatePdfTable(String pdfFileName, int decimalPlaces){
        try {
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFileName + ".pdf"));
            PdfPTable table = new PdfPTable(11);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f});
            doc.open();
            storage.generatePdfDescription(doc);
            storage.generatePdfTableHeader(table);
            storage.generatePdfTableContent(table, decimalPlaces);
            doc.add(table);
            doc.close();
            writer.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Helper methods, added to improve comprehension for code reviewer
     */
    public static void analyzeCmdArgs(JCommander jc, Main.Args options, String[] args){
        if(args.length == 0){
            System.out.println("Use -h or --help for help with usage");
            System.exit(2);
        } else if((args[0].equals("-h") || args[0].equals("--help"))){
            jc.usage();
            System.exit(3);
        } else if((options.isRectangular() && options.isTrapezium()) ||
                (options.isTrapezium() && options.isSimpson()) ||
                (options.isSimpson() && options.isRectangular()) ||
                (options.isRectangular() && options.isTrapezium() && options.isSimpson()) ||
                !(options.isRectangular() || options.isTrapezium() || options.isSimpson())){
            System.out.println("Only one option for numerical method can/must be chosen");
            jc.usage();
            System.exit(4);
        } else if(options.getDecimalPlaces() < 5 || options.getDecimalPlaces() > 8){
            System.out.println("Only from 5 to 8 decimal places allowed!!!");
            System.exit(5);
        }else if(options.getDivisions() > 100000 || options.getDivisions() < 1000){
            System.out.println("Divisions range is (1000; 100000)");
            System.exit(6);
        }else{
            proceedToExecutionBranches(options);
        }
    }

    public static void proceedToExecutionBranches(Main.Args options){
        analyzeNumericMethod(options);
        storage = new TableStorage(0f,40, 10, 0.1f, 0.01f);
        storage.initializeTableValues();
        System.out.println(Actions.greet());
        int option = 0;
        while(option != 5){
            option = Actions.availableOperations(options);
            if(option > 5 || option < 1){
                System.out.println("Allowed operation codes are in range (1-5)");
            }else{
                switch(option){
                    case 1:
                        Actions.outputToConsole();
                        break;
                    case 2:
                        Actions.outputToPdf(options.getDecimalPlaces());
                        break;
                    case 3:
                        Actions.findCdfValue();
                        break;
                    case 4:
                        Actions.findConfidenceInterval();
                        break;
                }
            }
        }
    }

    private static void findCdfValue() {
        System.out.print("Provide the value of x:   ");
        Scanner sc = new Scanner(System.in);
        float x = Calculations.roundToDecimalPlaces(sc.nextFloat(), 2);

        if((Math.abs(storage.getRows()/10) < x) || x == 0.0f){
            System.out.println("Only allowed x values in range (" + (-1)*Math.abs(storage.getRows()/10) + " ; " + Math.abs(storage.getRows()/10) + ")\n");
        }else{
            System.out.println("CDF value for x = " + x + ": " + storage.computeCdf(x) + "\n");
        }
    }

    public static void analyzeNumericMethod(Main.Args cmdArgs){
        if(cmdArgs.isSimpson()){
            Main.method = "Simpson's";
            Calculations.setActive(Formulas.SIMPSON);
        }else if(cmdArgs.isTrapezium()){
            Main.method = "Trapezium";
            Calculations.setActive(Formulas.TRAPEZIUM);
        }else if(cmdArgs.isRectangular()){
            Main.method = "Rectangular";
            Calculations.setActive(Formulas.RECTANGLE_MID);
        }
    }

    public static void findConfidenceInterval(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter sample data for sample X:");
        System.out.print("Size:               ");
        int size = sc.nextInt();
        System.out.print("Mean:               ");
        float mean = sc.nextFloat();
        System.out.print("Variance:           ");
        float variance = sc.nextFloat();
        System.out.print("D.p. for result:    ");
        int dp = sc.nextInt();
        System.out.print("Confidence level:   ");
        float cl = sc.nextFloat();

        StringBuilder sb = new StringBuilder();
        sb.append("\nFor confidence level of " + cl*100 + "% and sample data provided,\nthe confidence interval is "
                + Calculations.calculateConfidenceInterval(size, mean, variance, cl, dp));
        System.out.println(sb);
    }
}
