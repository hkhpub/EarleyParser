package hkh.nlp.earley;

import java.util.ArrayList;
import java.util.List;

/**
 * Earley Parser
 * @author hkh
 *
 */
public class EarleyParser {

	private NonTerminalRules ntrulesInst = null;
	private TerminalRules trulesInst = null;
	
	private int n = 0;
	private String[] mWords = null;
	private Chart[] charts = null;
	
	public EarleyParser(String sentence) {
		this.mWords = sentence.split("\\s");
		n = this.mWords.length;
		
		charts = new Chart[n+1];
		for (int i=0; i<=n; i++) {
			charts[i] = new Chart();
		}
	}
	
	/**
	 * parse function
	 */
	public void parse() {
		charts = earleyParse();
	}
	
	public ArrayList<StringBuffer> getResults() {
		ArrayList<StringBuffer> bufferList = new ArrayList<StringBuffer>();
		for (State state : charts[n].getFinalStates()) {
			if (State.isFinalState(state)) {
				State.getFinalStateResults(bufferList, state);
			}
		}
		return bufferList;
	}
	
	/**
	 * 문법추가
	 * @param rules
	 */
	public void setGrammar(ArrayList<String> rules) {
		ntrulesInst = new NonTerminalRules();
		trulesInst = new TerminalRules();
		
		int splitIndex = rules.size();
		for (int i=0; i<rules.size(); i++) {
			if (rules.get(i).length() == 0) {
				splitIndex = i;
				continue;
			}
			if (i<splitIndex) {
				ntrulesInst.addRule(rules.get(i));
			} else {
				trulesInst.addRule(rules.get(i));
			}
		}
		
//		ntrulesInst.printRules();
//		trulesInst.printRules();
//		System.out.println();
	}
	
	/**
	 * 파싱 로직
	 * @return
	 */
	public Chart[] earleyParse() {
		// add dummy start state
		State dummyState = State.createDummyState();
		charts[0].addState(dummyState);
		
		// for each i in words[i]
		for (int i=0; i<=n; i++) {
			
			// for each state in i-th chart
			State state = null;
			while ((state = charts[i].getState())!=null) {
				System.out.println(getWordsInPosition(i));
				if (!state.isComplete() && !state.isNextPOS(trulesInst)) {
					predictor(state);
					
				} else if (!state.isComplete() && state.isNextPOS(trulesInst)) {
					scanner(state);
					
				} else {
					completer(state);
					
				}
			}
		}
		
		return charts;
	}
	
	/**
	 * PREDICTOR operation
	 * @param state
	 */
	public void predictor(State state) {
//		System.out.println(">> predictor");
		System.out.println(state.toString());
		int j = state.current;
		List<State> matched = ntrulesInst.grammarRulesFor(state);
		for (State s : matched) {
			charts[j].addState(s);
		}
	}
	
	/**
	 * SCANNER operation
	 * @param state
	 */
	public void scanner(State state) {
//		System.out.println(">> scanner");
		System.out.println(state.toString());
		int j = state.current;
		if (j == n)
			return;
		List<State> matched = trulesInst.partsOfSpeech(state, mWords[j]);
		for (State s : matched) {
			charts[j+1].addState(s);
		}
	}
	
	/**
	 * COMPLETER operation
	 * @param state
	 */
	public void completer(State state) {
//		System.out.println(">> completer");
		System.out.println(state.toString());
		int j = state.start;
		int k = state.current;
		List<State> activeStates = charts[j].getMatchedActiveStates(state);
		for (State s : activeStates) {
			charts[k].addState(s);
		}
	}
	
	/**
	 * 현재 파싱 위치에 "+" 기호를 추가하여 sentence 출력
	 */
	public String getWordsInPosition(int i) {
		StringBuffer sb = new StringBuffer();
		for (int j=0; j<=n; j++) {
			if (j == i) {
				sb.append("+ ");
			}
			if (j < n) {
				sb.append(mWords[j]).append(" ");
			}
		}
		return sb.toString();
	}
}
