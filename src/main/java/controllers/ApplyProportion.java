package controllers;

import model.Ticket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ApplyProportion {

//
//
//    public static float computeProportion(List<Ticket> fixedTicketsList, String projName, Ticket ticket, boolean doActualComputation) throws URISyntaxException {
//        float proportion = 0;
//        try {
//            File file = new File("output/report/" + projName);
//            if (!file.exists()) {
//                boolean created = file.mkdirs();
//                if (!created) {
//                    throw new IOException();
//                }
//            }
//            file = new File("outputFiles/reportFiles/" + projName + "/Proportion.txt");
//            try(FileWriter fileWriter = new FileWriter(file)) {
//                if (fixedTicketsList.size() >= THRESHOLD_FOR_COLD_START) {
//                    proportion = ComputeProportion.incrementalProportionComputation(fixedTicketsList, ticket, true, doActualComputation);
//                } else {
//                    proportion = ComputeProportion.coldStartProportionComputation(ticket, doActualComputation);
//                }
//                fileWriter.append(outputToFile.toString());
//                FileWriterUtils.flushAndCloseFW(fileWriter, logger, NAME_OF_THIS_CLASS);
//            }
//        } catch(IOException | IOException e){
//            logger.info("Error in ComputeProportion when trying to create directory");
//        }
//        return proportion;
//    }

}
