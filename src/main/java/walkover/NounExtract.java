package walkover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;


public class NounExtract {


	static Set<String> nounPhrases = new HashSet<>();
	
	

	public static void main(String[] args) {

		InputStream modelInParse = null;
		try {

			URL inputLink = new URL("https://norvig.com/big.txt");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(inputLink.openStream()));

			String inputLine="";
			StringBuilder builder=new StringBuilder();
			//load chunking model
			modelInParse = new FileInputStream("/tmp/en-parser-chunking.bin"); 
			ParserModel model = new ParserModel(modelInParse);

			//create parse tree
			Parser parser = ParserFactory.create(model);
			//sorting
			ArrayList<String> nounList = new ArrayList<>();
			while ((inputLine = in.readLine()) != null) {
				//	builder.append(inputLine);


				Parse topParses[] = ParserTool.parseLine(inputLine, parser, 1);
				builder.setLength(0);
				//call subroutine to extract noun phrases
				for (Parse p : topParses)
					getNounPhrases(p);

				//print noun phrases
				
				for (String s : nounPhrases)
					nounList.add(s);
				/*
				 * if(nounList.size() == 100) { break; }
				 */
			}
			in.close();

			Collections.sort(nounList);
			
			for (String line : nounList) {
				System.out.println(line);
				
			}


		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelInParse != null) {
				try {
					modelInParse.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	//recursively loop through tree, extracting noun phrases
	public static void getNounPhrases(Parse p) {

		if (p.getType().equals("NP")) { //NP=noun phrase
			nounPhrases.add(p.getCoveredText());
		}
		for (Parse child : p.getChildren())
			getNounPhrases(child);
	}
}