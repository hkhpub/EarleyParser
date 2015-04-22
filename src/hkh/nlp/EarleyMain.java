package hkh.nlp;

import java.util.ArrayList;

import hkh.nlp.earley.EarleyParser;
import hkh.nlp.util.FileUtil;

public class EarleyMain {

	public static void main(String args[]) {
		
		String inputFile = "input.txt";
		String grammarFile = "grammar.txt";
		
		String sentence = FileUtil.readStringFromFile(inputFile);
		ArrayList<String> grammar = FileUtil.readFile(grammarFile);
		
		EarleyParser parser = new EarleyParser(sentence);
		parser.setGrammar(grammar);
		parser.parse();
		
		ArrayList<StringBuffer> bufferList = parser.getResults();
		
		StringBuffer sb2write = new StringBuffer();
		for (StringBuffer sbuf : bufferList) {
			System.out.println(sbuf.toString());
			sb2write.append(sbuf.toString()+"\n");
		}
		FileUtil.WriteFile("output.txt", sb2write.toString());
	}
}
