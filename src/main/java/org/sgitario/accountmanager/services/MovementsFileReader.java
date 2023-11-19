package org.sgitario.accountmanager.services;

import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.sgitario.accountmanager.entities.Movement;
import org.sgitario.accountmanager.entities.Profile;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public interface MovementsFileReader extends Closeable, Iterator<Movement> {

    static MovementsFileReader readerFor(FileUpload file, Profile profile) throws IOException {
        if (file.fileName().endsWith(".csv")) {
            return new CsvMovementsFileReader(file.uploadedFile().toFile(), profile);
        }

        return new ExcelMovementsFileReader(file.uploadedFile().toFile(), profile);
    }
}
