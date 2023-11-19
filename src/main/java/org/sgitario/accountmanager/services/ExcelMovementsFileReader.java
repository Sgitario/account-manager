package org.sgitario.accountmanager.services;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelMovementsFileReader implements MovementsFileReader {

    private final Workbook workbook;
    private final Sheet sheet;
    private final Profile profile;

    private int rowCount = 1;

    protected ExcelMovementsFileReader(File file, Profile profile) throws IOException {
        this.workbook = WorkbookFactory.create(new FileInputStream(file));
        this.sheet = workbook.getSheetAt(0);
        this.profile = profile;
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }

    @Override
    public boolean hasNext() {
        if (rowCount >= sheet.getPhysicalNumberOfRows()) {
            return false;
        }

        var row = sheet.getRow(rowCount);
        String subject = row.getCell(profile.columnSubject).getStringCellValue();
        return subject != null && !subject.isEmpty();
    }

    @Override
    public Movement next() {
        var row = sheet.getRow(rowCount);
        String subject = row.getCell(profile.columnSubject).getStringCellValue();
        Movement movement = new Movement();
        movement.subject = subject;
        movement.accountingDate = row.getCell(profile.columnAccountingDate).getDateCellValue();
        movement.valueDate = row.getCell(profile.columnValueDate).getDateCellValue();
        movement.quantity = row.getCell(profile.columnQuantity).getNumericCellValue();
        movement.profile = profile;
        rowCount++;
        return movement;
    }
}
