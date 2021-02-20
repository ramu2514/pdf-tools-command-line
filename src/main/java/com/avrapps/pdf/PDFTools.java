package com.avrapps.pdf;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.itextpdf.kernel.crypto.BadPasswordException;

import java.io.File;

import static com.avrapps.pdf.PDFUtilities.compressPdf;
import static com.avrapps.pdf.PDFUtilities.removePasswordFromPDF;
import static com.avrapps.pdf.PDFUtilities.setPasswordToPDF;
import static java.lang.System.exit;

public class PDFTools {
    @Parameter(names = {"--tool", "-t"}, description = "Options: compress", required = true)
    String tool;

    @Parameter(names = {"--source", "-s"}, description = "Source File Path")
    String source;

    @Parameter(names = {"--destination", "-d"}, description = "Destination file path")
    String destination;

    @Parameter(names = {"--password", "-p"}, description = "Password for the source PDF")
    String password = "";

    @Parameter(names = {"--new-password", "-n"}, description = "Password for the source PDF")
    String newPassword = "";

    @Parameter(names = {"--help", "-h"})
    private boolean help;

    public static void main(String[] args) {
        PDFTools tools = new PDFTools();
        JCommander.newBuilder()
                .addObject(tools)
                .build()
                .parse(args);
        tools.run();
    }

    public void run() {
        if (help) {
            System.out.println("Takes the path of PDF file, compress it and stores it in destination path. \n Fails with exit code: 5 if password is required to access PDF and if its not provided");
            return;
        }
        try {
            if ("compress".equals(tool)) {
                validateSourceDestinations();
                compressPdf(source, destination, password);
            } else if ("protect".equals(tool)) {
                validateSourceDestinations();
                if (newPassword.isEmpty()) {
                    System.out.println("New password must be provide to set as password");
                    exit(6);
                }
                setPasswordToPDF(source, destination, password, newPassword, newPassword);
            } else if ("remove".equals(tool)) {
                validateSourceDestinations();
                removePasswordFromPDF(source, destination, password);
            } else {
                System.out.println("Unrecognised Tool : " + tool);
                exit(99);
            }
        } catch (BadPasswordException ex) {
            ex.printStackTrace();
            exit(9);
        } catch (Exception ex) {
            ex.printStackTrace();
            exit(1);
        }
    }


    private void validateSourceDestinations() {
        if (source == null) {
            System.out.println("Source file must be provided");
            exit(3);
        }
        if (destination == null) {
            System.out.println("Destination file must be provided");
            exit(3);
        }
        if (!new File(source).exists()) {
            System.out.println("Source file does not exist : " + source);
            exit(4);
        }
        if (new File(destination).getParentFile().exists()) {
            boolean folders = new File(destination).getParentFile().exists() || new File(destination).getParentFile().mkdirs();
            if (!folders) {
                System.out.println("Destination folder does not exist & could not be created : " + new File(destination).getParentFile());
                exit(5);
            }
        }
    }
}
