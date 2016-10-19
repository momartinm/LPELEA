/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2012, Moisés Martínez
 *
 * (Questions/bug reports now to be sent to Moisés Martínez)
 *
 * This file is part of Pelea.
 * 
 * Pelea is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Pelea is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Pelea.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package org.pelea.utils.experimenter.result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Result {
    
    private final String[] names;
    private final List<ProblemNode> problems;
    private final List<Integer> horizons;
    private final String folder; 
    
    public Result(String name, String domain) throws IOException {
        this.problems = new ArrayList<ProblemNode>();
        this.horizons = new ArrayList<Integer>();
        this.folder = name;
        this.names = new String[]{};
        this.analyzeResults(name, domain);
        Collections.sort(this.problems);
    }
    
    public Result(String name, String domain, String problems) throws IOException {
        this.names = problems.toUpperCase().trim().split(",");
        this.problems = new ArrayList<ProblemNode>();
        this.horizons = new ArrayList<Integer>();
        this.folder = name;
        this.analyzeResults(name, domain);
        Collections.sort(this.problems);
    }
    
    private void analyzeResults(String name, String domain) throws IOException {
        
        File node = new File(name);
        
        if (node.isDirectory()) {
            for (String nodeName : node.list()) {
                this.analyzeResults(node.getAbsolutePath() + "/" + nodeName, domain);
            }
        }
        else {
            if (node.getName().substring(node.getName().lastIndexOf(".") + 1).equals("xml")) {
                try {
                    this.loadFile(node.getAbsolutePath(), domain);
                } catch (JDOMException ex) {
                    System.out.println("ERROR: FILE NOT WELL FORMED: " + node.getAbsolutePath());
                }
            }
        } 
    }
    
    public void loadFile(String fileName, String domain) throws JDOMException, IOException {
        
        SAXBuilder builder = new SAXBuilder();
        Document document = (Document) builder.build(new StringReader(this.readFile(fileName)));
        List<Element> experiments = (List<Element>) document.getRootElement().getChildren();
        
        for (int i = 0; i < experiments.size(); i++) {
                
            Element experiment = experiments.get(i);
            
            if (experiment.getChild("DOMAIN").getValue().toUpperCase().trim().compareTo(domain.toUpperCase().trim()) == 0) {
                
                boolean validProblem = false;
                
                String name = experiment.getChild("PROBLEM").getValue().toUpperCase().trim();
                
                if (this.names.length > 0) {
                
                    for (int j = 0; j < this.names.length; j++) {
                        if (name.compareTo(this.names[j]) == 0) {
                            validProblem = true;
                            break;
                        }
                    }
                }
                else
                    validProblem = true;

                //if (Integer.parseInt(experiment.getChild("RESULT").getValue()) == 1) {

                if (validProblem) {
                    int value = Integer.parseInt(experiment.getChild("RESULT").getValue());
                    int horizon = (experiment.getChild("Horizon") != null) ? Integer.parseInt(experiment.getChild("Horizon").getValue()):1000000;

                    if (!this.existHorizon(horizon))
                        this.horizons.add(horizon);

                    ProblemNode problem = this.getProblem(experiment.getChild("PROBLEM").getValue().trim());

                    if (problem == null) {
                        problem = new ProblemNode(experiment.getChild("PROBLEM").getValue().trim());
                        problem.addExperiment(experiment, horizon);

                        this.problems.add(problem);
                    }
                    else
                        problem.addExperiment(experiment, horizon);
                }
            }
        }
    }
    
    private boolean existHorizon(int horizon) {
        for (Integer h : this.horizons) {
            if (h == horizon) {
                return true;
            }
        }
        return false;
    }
    
    private ProblemNode getProblem(String name) {
        for (ProblemNode problem : this.problems) {
            if (problem.getName().equals(name)) {
                return problem;
            }
        }
        return null;
    }
    
    private String readFile(String fileName) throws FileNotFoundException, IOException {
        
        String xml = "";
        String line = "";
        BufferedReader buffer = new BufferedReader(new FileReader(fileName));
        
        while ((line = buffer.readLine()) != null) {
            xml += line;
        }
        
        return xml;
    }
    
    public void generateLatexTable(String fileName) throws IOException {
        
        int i,j = 0;
        
        //Reparar para win
        BufferedWriter buffer = new BufferedWriter(new FileWriter(this.folder + "/" + fileName));
        
        buffer.write("\\documentclass[twoside,11pt]{article}"); buffer.newLine();
        buffer.write("\\usepackage{array, multirow}"); buffer.newLine();
        buffer.write("\\begin{document}"); buffer.newLine();
        
        buffer.write("\\begin{table}[t]"); buffer.newLine();
        buffer.write("\\begin{center}"); buffer.newLine();
        buffer.write("\\begin{small}"); buffer.newLine();
        
        buffer.write("\\begin{tabular}{|c|l|"); 
        
        for (i = 0; i < this.problems.size(); i++) {
            buffer.write("r|");
        }
        
        buffer.write("}"); buffer.newLine();
        buffer.write("\\hline"); buffer.newLine();
        buffer.write("\\multirow{2}{*}{Planner} & \\multirow{2}{*}{Metrics} & \\multicolumn{" + this.problems.size() + "}{ |c| }{Problem} \\\\"); buffer.newLine();
        buffer.write("\\cline{3-" + (3+this.problems.size()-1) +"}"); buffer.newLine();
        
        buffer.write("& ");
        
        for (i = 0; i < this.problems.size(); i++) {
            buffer.write("& \\shortstack{" + this.problems.get(i).getName() + " \\\\ ()}"); 
            
            if (i == this.problems.size()-1) {
                buffer.write("\\\\");
            }
            
            buffer.newLine();
        }
        
        buffer.write("\\hline"); buffer.newLine();
        
        for (i = 0; i < this.horizons.size(); i++) {

            if (this.horizons.get(i) == 1000000) {
                buffer.write("\\multirow{5}{*}{FD}"); buffer.newLine();
            }
            else { 
                buffer.write("\\multirow{5}{*}{AKFD (k = " + this.horizons.get(i) + ")}"); buffer.newLine();
            }
                
            buffer.write("& FPT(s) ");
            
            for (j = 0; j < this.problems.size(); j++) {
                buffer.write("& " + this.problems.get(j).printData(1, this.horizons.get(i)) + " ");
            }
            
            buffer.write("\\\\"); buffer.newLine();
            
            buffer.write("& time(s) ");
            
            for (j = 0; j < this.problems.size(); j++) {
                buffer.write("& " + this.problems.get(j).printData(2, this.horizons.get(i)) + " ");
            }
            
            buffer.write("\\\\"); buffer.newLine();
            
            buffer.write("& replanning ");
            
            for (j = 0; j < this.problems.size(); j++) {
                buffer.write("& " + this.problems.get(j).printData(3, this.horizons.get(i)) + " ");
            }
            
            buffer.write("\\\\"); buffer.newLine();
            
            buffer.write("& actions ");
            
            for (j = 0; j < this.problems.size(); j++) {
                buffer.write("& " + this.problems.get(j).printData(4, this.horizons.get(i)) + " ");
            }
            
            buffer.write("\\\\"); buffer.newLine();
            
            buffer.write("& coverage ");
            
            for (j = 0; j < this.problems.size(); j++) {
                buffer.write("& " + this.problems.get(j).printData(5, this.horizons.get(i)));
            }
            
            buffer.write("\\\\"); buffer.newLine();
            
            buffer.write("\\hline"); buffer.newLine();
        }

        buffer.write("\\end{tabular}"); buffer.newLine();
        buffer.write("\\end{small}"); buffer.newLine();
        buffer.write("\\end{center}"); buffer.newLine();
        buffer.write("\\caption{}"); buffer.newLine();
        buffer.write("\\label{tab:}"); buffer.newLine();
        buffer.write("\\end{table}"); buffer.newLine();  
        buffer.write("\\end{document}"); buffer.newLine(); 
        buffer.close();
    }
    
    public void generateGraph() throws IOException {
        
        for (int i = 0; i < this.problems.size(); i++) {
            this.problems.get(i).generateGraph(this.horizons.size(), this.folder);
        }
    }
}
