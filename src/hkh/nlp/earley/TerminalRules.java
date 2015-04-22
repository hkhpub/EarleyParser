package hkh.nlp.earley;

import java.util.ArrayList;
import java.util.List;

public class TerminalRules {

	public ArrayList<TRule> trules = null;
	public TerminalRules() {
		trules = new ArrayList<TRule>();
	}
	
	public void addRule(String raw) {
//		System.out.println("terminal: "+raw);
		trules.add(TRule.makeRule(raw));
	}
	
	/**
	 * 다음 string이 POS인지 판단. (POS: Parts of Speech)
	 * @param next
	 * @return
	 */
	public boolean isPOS(String next) {
		for (int i=0; i<trules.size(); i++) {
			if (trules.get(i).lhs.equals(next)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 해당 state, terminal rule중 word에 맞는 terminal이 존재하면 state 생성
	 * @param state
	 * @param word
	 * @return
	 */
	public List<State> partsOfSpeech(State state, String word) {
		List<State> matched = new ArrayList<State>();
		String next = state.getNextString();
		for (TRule trule : trules) {
			if (trule.lhs.equals(next) && trule.rhs.equals(word)) {
				// matched, add to new state
				String[] elm = {word};
				ArrayList<String[]> completed = new ArrayList<String[]>();
				completed.add(elm);
				String[] incompleted = {};
				State newState = new State(state.current, state.current+1, next, completed, incompleted);
				newState.operation = "Scanner";
				matched.add(newState);
			}
		}
		return matched;
	}
	
	static class TRule {
		
		public String lhs = null;
		public String rhs = null;
		
		private TRule() {
		}
		
		public static TRule makeRule(String raw) {
			TRule rule = new TRule();
			String[] elems = raw.split("\\s");
			try {
				rule.lhs = elems[0];
				rule.rhs = elems[2];
			} catch (RuntimeException re) {
				System.err.println("malformed grammar file !");
				System.exit(0);
			}
			return rule;
		}
		
		@Override
		public String toString() {
			StringBuffer ret = new StringBuffer();
			ret.append(lhs);
			ret.append(" >> ");
			ret.append(rhs);
			return ret.toString();
		}
	}
	
	public void printRules() {
		System.out.println("-- Terminal Rules --");
		for (TRule rule : trules) {
			System.out.println(rule.toString());
		}
	}
}
