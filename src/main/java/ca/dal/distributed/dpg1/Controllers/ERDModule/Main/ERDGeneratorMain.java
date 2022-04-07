package ca.dal.distributed.dpg1.Controllers.ERDModule.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ca.dal.distributed.dpg1.Controllers.ERDModule.Exceptions.ERDGeneratorException;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.EventLogger;
import ca.dal.distributed.dpg1.Controllers.LoggerModule.Main.GeneralLogger;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description ERDGeneratorMain Abstract class gives an idea of the structure
 *              of the child class
 */
public abstract class ERDGeneratorMain {

    private EventLogger eventLogger;
    private GeneralLogger generalLogger;

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Core main function that creates the ERD and writes into a file
     */
    protected abstract void createERD(final File[] allTables, final String databaseName) throws ERDGeneratorException;

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Writes the ERD Message to the file
     */
    protected abstract void writeERDToFile(final FileWriter fileWriter, final String tableNameString,
            final String[] tableColumnNamesSplit, final List<String> cardinalities) throws IOException;

    /**
     * @author Bharatwaaj Shankaranarayanan
     * @description Only accessible master function that will be called from other
     *              module to generate the ERD Diagram
     */
    public abstract boolean generateERD(final String databaseName) throws ERDGeneratorException;
}
