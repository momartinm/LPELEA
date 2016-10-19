package org.pelea;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pelea.core.configuration.*;
import org.pelea.core.module.Module;
import org.pelea.utils.options.Options;
import org.pelea.utils.experimenter.result.Result;

/**
 *
 * @author moises
 */
public class main {
    
    public static void main(String[] args) {
    
        Options options = new Options(args);
        Module module;
        
        if (options.getOption("parse") != null) {
            
            if (options.getNumOptions() < 2) {
                options.showOptionsLine();
            }
            else {
                try {
                    Result results = new Result(args[1], options.getOption("domain"));
                    results.generateLatexTable("tableResult.tex");
                    results.generateGraph();
                } catch (IOException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else {
            if (options.getNumOptions() < 2) {
                options.showOptionsLine();
            }
            else
            {
                if (configuration.getInstance().readConfigFile(options.getOption("file")))
                {
                    try {

                        if (options.getNumOptions() == 3) {
                            module = (Module) (Class.forName(configuration.getInstance().getParameter(options.getOption("name"), "CLASS")).getConstructor(String.class, String.class)).newInstance(options.getOption("name"), options.getOption("pid"));
                        }
                        else {
                            module = (Module) (Class.forName(configuration.getInstance().getParameter(options.getOption("name"), "CLASS")).getConstructor(String.class)).newInstance(options.getOption("name"));
                        }

                        module.syncronize();
                        module.run(10);
                    } catch (Exception ex) {
                        Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                        System.exit(1);
                    }
                }

                System.exit(0);
            }
        }
    }
}
